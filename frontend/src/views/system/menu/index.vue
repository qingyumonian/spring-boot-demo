<template>
  <div class="menu-container">
    <!-- Search Bar -->
    <el-card shadow="never" class="search-card">
      <el-form :model="queryParams" ref="queryRef" :inline="true">
        <el-form-item label="Menu Name" prop="menuName">
          <el-input
            v-model="queryParams.menuName"
            placeholder="Enter menu name"
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
          <span class="title">Menu List</span>
          <div class="actions">
            <el-button type="info" plain :icon="Sort" @click="toggleExpandAll">
              {{ isExpandAll ? 'Collapse All' : 'Expand All' }}
            </el-button>
            <el-button type="primary" :icon="Plus" @click="handleAdd()">Add Menu</el-button>
          </div>
        </div>
      </template>

      <!-- Menu Tree Table -->
      <el-table
        v-if="refreshTable"
        v-loading="loading"
        :data="menuList"
        row-key="id"
        :default-expand-all="isExpandAll"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        border
      >
        <el-table-column prop="menuName" label="Menu Name" min-width="200" />
        <el-table-column prop="icon" label="Icon" width="80" align="center">
          <template #default="scope">
            <el-icon v-if="scope.row.icon">
              <component :is="scope.row.icon" />
            </el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="Sort" width="80" align="center" />
        <el-table-column prop="perms" label="Permission" min-width="150" show-overflow-tooltip />
        <el-table-column prop="path" label="Path" min-width="150" show-overflow-tooltip />
        <el-table-column label="Type" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getMenuTypeTag(scope.row.menuType)">
              {{ getMenuTypeName(scope.row.menuType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Visible" width="80" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.visible === 1 ? 'success' : 'info'">
              {{ scope.row.visible === 1 ? 'Yes' : 'No' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Status" width="80" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? 'On' : 'Off' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="Created At" width="180" />
        <el-table-column label="Actions" width="200" align="center" fixed="right">
          <template #default="scope">
            <el-button type="primary" link :icon="Edit" @click="handleEdit(scope.row)">Edit</el-button>
            <el-button type="primary" link :icon="Plus" @click="handleAdd(scope.row)">Add</el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(scope.row)">Delete</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Add/Edit Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      :close-on-click-modal="false"
      @close="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="Parent Menu">
              <el-tree-select
                v-model="form.parentId"
                :data="menuOptions"
                :props="{ value: 'id', label: 'menuName', children: 'children' }"
                value-key="id"
                placeholder="Select parent menu"
                check-strictly
                clearable
                filterable
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="Menu Type" prop="menuType">
              <el-radio-group v-model="form.menuType">
                <el-radio value="M">Directory</el-radio>
                <el-radio value="C">Menu</el-radio>
                <el-radio value="F">Button</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="form.menuType !== 'F'">
            <el-form-item label="Icon">
              <el-select v-model="form.icon" placeholder="Select icon" clearable filterable style="width: 100%">
                <el-option
                  v-for="icon in iconList"
                  :key="icon"
                  :label="icon"
                  :value="icon"
                >
                  <el-icon style="margin-right: 8px"><component :is="icon" /></el-icon>
                  {{ icon }}
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Menu Name" prop="menuName">
              <el-input v-model="form.menuName" placeholder="Enter menu name" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Sort" prop="sort">
              <el-input-number v-model="form.sort" :min="0" :max="999" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.menuType !== 'F'">
            <el-form-item label="Path" prop="path">
              <el-input v-model="form.path" placeholder="Enter route path" />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.menuType === 'C'">
            <el-form-item label="Component" prop="component">
              <el-input v-model="form.component" placeholder="Enter component path" />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.menuType !== 'M'">
            <el-form-item label="Permission" prop="perms">
              <el-input v-model="form.perms" placeholder="e.g. system:user:list" />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.menuType !== 'F'">
            <el-form-item label="Visible">
              <el-radio-group v-model="form.visible">
                <el-radio :value="1">Yes</el-radio>
                <el-radio :value="0">No</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Status">
              <el-radio-group v-model="form.status">
                <el-radio :value="1">Enabled</el-radio>
                <el-radio :value="0">Disabled</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">Confirm</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, Sort } from '@element-plus/icons-vue'
import { getMenuList, getMenuTree, createMenu, updateMenu, deleteMenu } from '@/api/menu'

// Common icons
const iconList = [
  'House', 'User', 'UserFilled', 'Setting', 'Menu', 'Document', 'Folder',
  'Edit', 'Delete', 'Search', 'Plus', 'Minus', 'Check', 'Close',
  'Upload', 'Download', 'Lock', 'Unlock', 'Key', 'Bell', 'Star',
  'Message', 'ChatDotRound', 'Phone', 'Location', 'Calendar', 'Clock',
  'Link', 'Picture', 'Camera', 'VideoCamera', 'Monitor', 'Cpu', 'Connection'
]

// Query params
const queryParams = reactive({
  menuName: '',
  status: undefined
})

// Data
const loading = ref(false)
const menuList = ref([])
const refreshTable = ref(true)
const isExpandAll = ref(true)

// Dialog
const dialogVisible = ref(false)
const dialogTitle = computed(() => form.id ? 'Edit Menu' : 'Add Menu')
const submitLoading = ref(false)
const formRef = ref(null)
const menuOptions = ref([])

const form = reactive({
  id: undefined,
  parentId: 0,
  menuType: 'M',
  menuName: '',
  icon: '',
  sort: 0,
  path: '',
  component: '',
  perms: '',
  visible: 1,
  status: 1
})

const rules = {
  menuName: [
    { required: true, message: 'Please enter menu name', trigger: 'blur' }
  ],
  menuType: [
    { required: true, message: 'Please select menu type', trigger: 'change' }
  ],
  path: [
    { required: true, message: 'Please enter route path', trigger: 'blur' }
  ]
}

// Methods
const getList = async () => {
  loading.value = true
  try {
    const res = await getMenuTree()
    menuList.value = res.data || []
  } catch (error) {
    console.error('Failed to fetch menus:', error)
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  getList()
}

const resetQuery = () => {
  queryParams.menuName = ''
  queryParams.status = undefined
  handleQuery()
}

const toggleExpandAll = () => {
  refreshTable.value = false
  isExpandAll.value = !isExpandAll.value
  setTimeout(() => {
    refreshTable.value = true
  }, 100)
}

const getMenuTypeTag = (type) => {
  const map = { M: '', C: 'success', F: 'warning' }
  return map[type] || ''
}

const getMenuTypeName = (type) => {
  const map = { M: 'Directory', C: 'Menu', F: 'Button' }
  return map[type] || type
}

const handleAdd = (row) => {
  resetForm()
  getMenuOptions()
  if (row && row.id) {
    form.parentId = row.id
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  resetForm()
  getMenuOptions()
  Object.assign(form, {
    id: row.id,
    parentId: row.parentId,
    menuType: row.menuType,
    menuName: row.menuName,
    icon: row.icon,
    sort: row.sort,
    path: row.path,
    component: row.component,
    perms: row.perms,
    visible: row.visible,
    status: row.status
  })
  dialogVisible.value = true
}

const getMenuOptions = async () => {
  try {
    const res = await getMenuTree()
    const rootNode = { id: 0, menuName: 'Root Directory', children: res.data || [] }
    menuOptions.value = [rootNode]
  } catch (error) {
    console.error('Failed to fetch menu options:', error)
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitLoading.value = true
    try {
      if (form.id) {
        await updateMenu(form.id, form)
        ElMessage.success('Menu updated successfully')
      } else {
        await createMenu(form)
        ElMessage.success('Menu created successfully')
      }
      dialogVisible.value = false
      getList()
    } catch (error) {
      console.error('Failed to save menu:', error)
    } finally {
      submitLoading.value = false
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `Are you sure you want to delete menu "${row.menuName}"?`,
    'Warning',
    {
      confirmButtonText: 'Confirm',
      cancelButtonText: 'Cancel',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteMenu(row.id)
      ElMessage.success('Menu deleted successfully')
      getList()
    } catch (error) {
      console.error('Failed to delete menu:', error)
    }
  }).catch(() => {})
}

const resetForm = () => {
  form.id = undefined
  form.parentId = 0
  form.menuType = 'M'
  form.menuName = ''
  form.icon = ''
  form.sort = 0
  form.path = ''
  form.component = ''
  form.perms = ''
  form.visible = 1
  form.status = 1
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

onMounted(() => {
  getList()
})
</script>

<style lang="scss" scoped>
.menu-container {
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

      .actions {
        display: flex;
        gap: 10px;
      }
    }
  }
}
</style>
