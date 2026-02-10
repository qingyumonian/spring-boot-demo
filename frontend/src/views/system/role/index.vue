<template>
  <div class="role-container">
    <!-- Search Bar -->
    <el-card shadow="never" class="search-card">
      <el-form :model="queryParams" ref="queryRef" :inline="true">
        <el-form-item label="Role Name" prop="roleName">
          <el-input
            v-model="queryParams.roleName"
            placeholder="Enter role name"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="Role Code" prop="roleCode">
          <el-input
            v-model="queryParams.roleCode"
            placeholder="Enter role code"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="Status" prop="status">
          <el-select
            v-model="queryParams.status"
            placeholder="Select status"
            clearable
            style="width: 200px"
          >
            <el-option label="Enabled" :value="1" />
            <el-option label="Disabled" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleQuery">Search</el-button>
          <el-button :icon="Refresh" @click="resetQuery">Reset</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- Table Toolbar -->
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="table-header">
          <span class="title">Role List</span>
          <div class="actions">
            <el-button type="primary" :icon="Plus" @click="handleAdd">Add Role</el-button>
          </div>
        </div>
      </template>

      <!-- Role Table -->
      <el-table v-loading="loading" :data="roleList" border stripe>
        <el-table-column type="index" label="#" width="50" align="center" />
        <el-table-column prop="roleName" label="Role Name" min-width="150" />
        <el-table-column prop="roleCode" label="Role Code" min-width="150" />
        <el-table-column prop="sort" label="Sort Order" width="100" align="center" />
        <el-table-column label="Status" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? 'Enabled' : 'Disabled' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="Remark" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createTime" label="Created At" width="180" />
        <el-table-column label="Actions" width="240" align="center" fixed="right">
          <template #default="scope">
            <el-button type="primary" link :icon="Edit" @click="handleEdit(scope.row)">Edit</el-button>
            <el-button type="primary" link :icon="Menu" @click="handleAssignMenus(scope.row)">Menus</el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(scope.row)">Delete</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- Pagination -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- Add/Edit Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      :close-on-click-modal="false"
      @close="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="Role Name" prop="roleName">
          <el-input v-model="form.roleName" placeholder="Enter role name" />
        </el-form-item>
        <el-form-item label="Role Code" prop="roleCode">
          <el-input v-model="form.roleCode" placeholder="Enter role code" :disabled="form.id !== undefined" />
        </el-form-item>
        <el-form-item label="Sort Order" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="Status" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">Enabled</el-radio>
            <el-radio :value="0">Disabled</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="Remark" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="Enter remark" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">Confirm</el-button>
      </template>
    </el-dialog>

    <!-- Assign Menus Dialog -->
    <el-dialog v-model="menuDialogVisible" title="Assign Menu Permissions" width="500px">
      <el-form label-width="100px">
        <el-form-item label="Role Name">
          <el-input :value="currentRole?.roleName" disabled />
        </el-form-item>
        <el-form-item label="Menus">
          <el-tree
            ref="menuTreeRef"
            :data="menuTree"
            :props="treeProps"
            show-checkbox
            node-key="id"
            :default-checked-keys="selectedMenuIds"
            :check-strictly="false"
            default-expand-all
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="menuDialogVisible = false">Cancel</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitAssignMenus">Confirm</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, Menu } from '@element-plus/icons-vue'
import { getRoleList, createRole, updateRole, deleteRole, assignMenus } from '@/api/role'
import { getMenuTree } from '@/api/menu'

// Query params
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  roleName: '',
  roleCode: '',
  status: undefined
})

// Data
const loading = ref(false)
const roleList = ref([])
const total = ref(0)

// Dialog
const dialogVisible = ref(false)
const dialogTitle = computed(() => form.id ? 'Edit Role' : 'Add Role')
const submitLoading = ref(false)
const formRef = ref(null)
const form = reactive({
  id: undefined,
  roleName: '',
  roleCode: '',
  sort: 0,
  status: 1,
  remark: ''
})

const rules = {
  roleName: [
    { required: true, message: 'Please enter role name', trigger: 'blur' }
  ],
  roleCode: [
    { required: true, message: 'Please enter role code', trigger: 'blur' },
    { pattern: /^[A-Z_]+$/, message: 'Role code should be uppercase letters and underscores', trigger: 'blur' }
  ]
}

// Menu assignment
const menuDialogVisible = ref(false)
const menuTreeRef = ref(null)
const currentRole = ref(null)
const menuTree = ref([])
const selectedMenuIds = ref([])

const treeProps = {
  children: 'children',
  label: 'menuName'
}

// Methods
const getList = async () => {
  loading.value = true
  try {
    const res = await getRoleList(queryParams)
    roleList.value = res.data.records || res.data.list || res.data || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('Failed to fetch roles:', error)
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryParams.roleName = ''
  queryParams.roleCode = ''
  queryParams.status = undefined
  handleQuery()
}

const handleSizeChange = (size) => {
  queryParams.pageSize = size
  getList()
}

const handleCurrentChange = (page) => {
  queryParams.pageNum = page
  getList()
}

const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  resetForm()
  Object.assign(form, {
    id: row.id,
    roleName: row.roleName,
    roleCode: row.roleCode,
    sort: row.sort,
    status: row.status,
    remark: row.remark
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitLoading.value = true
    try {
      if (form.id) {
        await updateRole(form.id, form)
        ElMessage.success('Role updated successfully')
      } else {
        await createRole(form)
        ElMessage.success('Role created successfully')
      }
      dialogVisible.value = false
      getList()
    } catch (error) {
      console.error('Failed to save role:', error)
    } finally {
      submitLoading.value = false
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `Are you sure you want to delete role "${row.roleName}"?`,
    'Warning',
    {
      confirmButtonText: 'Confirm',
      cancelButtonText: 'Cancel',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteRole(row.id)
      ElMessage.success('Role deleted successfully')
      getList()
    } catch (error) {
      console.error('Failed to delete role:', error)
    }
  }).catch(() => {})
}

const handleAssignMenus = async (row) => {
  currentRole.value = row
  selectedMenuIds.value = row.menuIds || []

  // Load menu tree if not loaded
  if (menuTree.value.length === 0) {
    try {
      const res = await getMenuTree()
      menuTree.value = res.data || []
    } catch (error) {
      console.error('Failed to fetch menu tree:', error)
    }
  }

  menuDialogVisible.value = true
}

const submitAssignMenus = async () => {
  submitLoading.value = true
  try {
    const checkedKeys = menuTreeRef.value.getCheckedKeys()
    const halfCheckedKeys = menuTreeRef.value.getHalfCheckedKeys()
    const menuIds = [...checkedKeys, ...halfCheckedKeys]

    await assignMenus(currentRole.value.id, menuIds)
    ElMessage.success('Menu permissions assigned successfully')
    menuDialogVisible.value = false
    getList()
  } catch (error) {
    console.error('Failed to assign menus:', error)
  } finally {
    submitLoading.value = false
  }
}

const resetForm = () => {
  form.id = undefined
  form.roleName = ''
  form.roleCode = ''
  form.sort = 0
  form.status = 1
  form.remark = ''
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

onMounted(() => {
  getList()
})
</script>

<style lang="scss" scoped>
.role-container {
  .search-card {
    margin-bottom: 20px;

    :deep(.el-card__body) {
      padding-bottom: 0;
    }
  }

  .table-card {
    .table-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .title {
        font-size: 16px;
        font-weight: 600;
      }
    }
  }

  .pagination-container {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
  }
}
</style>
