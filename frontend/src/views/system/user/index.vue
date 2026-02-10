<template>
  <div class="user-container">
    <!-- Search Bar -->
    <el-card shadow="never" class="search-card">
      <el-form :model="queryParams" ref="queryRef" :inline="true">
        <el-form-item label="Username" prop="username">
          <el-input
            v-model="queryParams.username"
            placeholder="Enter username"
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
          <span class="title">User List</span>
          <div class="actions">
            <el-button type="primary" :icon="Plus" @click="handleAdd">Add User</el-button>
          </div>
        </div>
      </template>

      <!-- User Table -->
      <el-table v-loading="loading" :data="userList" border stripe>
        <el-table-column type="index" label="#" width="50" align="center" />
        <el-table-column prop="username" label="Username" min-width="120" />
        <el-table-column prop="nickname" label="Nickname" min-width="120" />
        <el-table-column prop="email" label="Email" min-width="180" />
        <el-table-column prop="phone" label="Phone" min-width="120" />
        <el-table-column label="Status" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? 'Enabled' : 'Disabled' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="Created At" width="180" />
        <el-table-column label="Actions" width="280" align="center" fixed="right">
          <template #default="scope">
            <el-button type="primary" link :icon="Edit" @click="handleEdit(scope.row)">Edit</el-button>
            <el-button type="primary" link :icon="Key" @click="handleResetPwd(scope.row)">Reset Password</el-button>
            <el-button type="primary" link :icon="UserFilled" @click="handleAssignRoles(scope.row)">Roles</el-button>
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
        <el-form-item label="Username" prop="username">
          <el-input v-model="form.username" placeholder="Enter username" :disabled="form.id !== undefined" />
        </el-form-item>
        <el-form-item label="Nickname" prop="nickname">
          <el-input v-model="form.nickname" placeholder="Enter nickname" />
        </el-form-item>
        <el-form-item v-if="!form.id" label="Password" prop="password">
          <el-input v-model="form.password" type="password" placeholder="Enter password" show-password />
        </el-form-item>
        <el-form-item label="Email" prop="email">
          <el-input v-model="form.email" placeholder="Enter email" />
        </el-form-item>
        <el-form-item label="Phone" prop="phone">
          <el-input v-model="form.phone" placeholder="Enter phone number" />
        </el-form-item>
        <el-form-item label="Status" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">Enabled</el-radio>
            <el-radio :value="0">Disabled</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">Confirm</el-button>
      </template>
    </el-dialog>

    <!-- Reset Password Dialog -->
    <el-dialog v-model="pwdDialogVisible" title="Reset Password" width="400px">
      <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="100px">
        <el-form-item label="New Password" prop="password">
          <el-input v-model="pwdForm.password" type="password" placeholder="Enter new password" show-password />
        </el-form-item>
        <el-form-item label="Confirm" prop="confirmPassword">
          <el-input v-model="pwdForm.confirmPassword" type="password" placeholder="Confirm password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pwdDialogVisible = false">Cancel</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitResetPwd">Confirm</el-button>
      </template>
    </el-dialog>

    <!-- Assign Roles Dialog -->
    <el-dialog v-model="roleDialogVisible" title="Assign Roles" width="500px">
      <el-form label-width="100px">
        <el-form-item label="Username">
          <el-input :value="currentUser?.username" disabled />
        </el-form-item>
        <el-form-item label="Roles">
          <el-checkbox-group v-model="selectedRoleIds">
            <el-checkbox
              v-for="role in roleList"
              :key="role.id"
              :value="role.id"
              :label="role.roleName"
            />
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roleDialogVisible = false">Cancel</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitAssignRoles">Confirm</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, Key, UserFilled } from '@element-plus/icons-vue'
import { getUserList, createUser, updateUser, deleteUser, assignRoles } from '@/api/user'
import { getRoleList } from '@/api/role'

// Query params
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  username: '',
  status: undefined
})

// Data
const loading = ref(false)
const userList = ref([])
const total = ref(0)

// Dialog
const dialogVisible = ref(false)
const dialogTitle = computed(() => form.id ? 'Edit User' : 'Add User')
const submitLoading = ref(false)
const formRef = ref(null)
const form = reactive({
  id: undefined,
  username: '',
  nickname: '',
  password: '',
  email: '',
  phone: '',
  status: 1
})

const rules = {
  username: [
    { required: true, message: 'Please enter username', trigger: 'blur' },
    { min: 3, max: 20, message: 'Username should be 3-20 characters', trigger: 'blur' }
  ],
  password: [
    { required: true, message: 'Please enter password', trigger: 'blur' },
    { min: 5, max: 20, message: 'Password should be 5-20 characters', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: 'Please enter a valid email', trigger: 'blur' }
  ]
}

// Password reset
const pwdDialogVisible = ref(false)
const pwdFormRef = ref(null)
const pwdForm = reactive({
  userId: undefined,
  password: '',
  confirmPassword: ''
})

const pwdRules = {
  password: [
    { required: true, message: 'Please enter password', trigger: 'blur' },
    { min: 5, max: 20, message: 'Password should be 5-20 characters', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: 'Please confirm password', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== pwdForm.password) {
          callback(new Error('Passwords do not match'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// Role assignment
const roleDialogVisible = ref(false)
const currentUser = ref(null)
const roleList = ref([])
const selectedRoleIds = ref([])

// Methods
const getList = async () => {
  loading.value = true
  try {
    const res = await getUserList(queryParams)
    userList.value = res.data.records || res.data.list || res.data || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('Failed to fetch users:', error)
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryParams.username = ''
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
    username: row.username,
    nickname: row.nickname,
    email: row.email,
    phone: row.phone,
    status: row.status
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
        await updateUser(form.id, form)
        ElMessage.success('User updated successfully')
      } else {
        await createUser(form)
        ElMessage.success('User created successfully')
      }
      dialogVisible.value = false
      getList()
    } catch (error) {
      console.error('Failed to save user:', error)
    } finally {
      submitLoading.value = false
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `Are you sure you want to delete user "${row.username}"?`,
    'Warning',
    {
      confirmButtonText: 'Confirm',
      cancelButtonText: 'Cancel',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteUser(row.id)
      ElMessage.success('User deleted successfully')
      getList()
    } catch (error) {
      console.error('Failed to delete user:', error)
    }
  }).catch(() => {})
}

const handleResetPwd = (row) => {
  pwdForm.userId = row.id
  pwdForm.password = ''
  pwdForm.confirmPassword = ''
  pwdDialogVisible.value = true
}

const submitResetPwd = async () => {
  if (!pwdFormRef.value) return
  await pwdFormRef.value.validate(async (valid) => {
    if (!valid) return

    submitLoading.value = true
    try {
      await updateUser(pwdForm.userId, { password: pwdForm.password })
      ElMessage.success('Password reset successfully')
      pwdDialogVisible.value = false
    } catch (error) {
      console.error('Failed to reset password:', error)
    } finally {
      submitLoading.value = false
    }
  })
}

const handleAssignRoles = async (row) => {
  currentUser.value = row
  selectedRoleIds.value = row.roleIds || []

  // Load roles if not loaded
  if (roleList.value.length === 0) {
    try {
      const res = await getRoleList({ pageSize: 100 })
      roleList.value = res.data.records || res.data.list || res.data || []
    } catch (error) {
      console.error('Failed to fetch roles:', error)
    }
  }

  roleDialogVisible.value = true
}

const submitAssignRoles = async () => {
  submitLoading.value = true
  try {
    await assignRoles(currentUser.value.id, selectedRoleIds.value)
    ElMessage.success('Roles assigned successfully')
    roleDialogVisible.value = false
    getList()
  } catch (error) {
    console.error('Failed to assign roles:', error)
  } finally {
    submitLoading.value = false
  }
}

const resetForm = () => {
  form.id = undefined
  form.username = ''
  form.nickname = ''
  form.password = ''
  form.email = ''
  form.phone = ''
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
.user-container {
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
