<template>
  <div class="process-definition">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>流程定义管理</span>
          <el-button type="primary" @click="handleDesign">
            <el-icon><Plus /></el-icon>
            设计流程
          </el-button>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="流程名称">
          <el-input v-model="queryForm.name" placeholder="请输入流程名称" clearable />
        </el-form-item>
        <el-form-item label="流程分类">
          <el-input v-model="queryForm.category" placeholder="请输入流程分类" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="name" label="流程名称" min-width="150" />
        <el-table-column prop="key" label="流程标识" min-width="120" />
        <el-table-column prop="version" label="版本" width="80" />
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.suspended ? 'danger' : 'success'">
              {{ row.suspended ? '挂起' : '激活' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="deploymentTime" label="部署时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.deploymentTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button link type="primary" @click="handleToggleState(row)">
              {{ row.suspended ? '激活' : '挂起' }}
            </el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 查看流程图对话框 -->
    <el-dialog v-model="viewDialogVisible" title="查看流程" width="80%" top="5vh">
      <div style="height: 500px;">
        <BpmnViewer v-if="currentBpmnXml" :xml="currentBpmnXml" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh } from '@element-plus/icons-vue'
import { listProcessDefinitions, deleteProcess, suspendProcess, activateProcess, getBpmnXml } from '@/api/workflow/definition'
import BpmnViewer from '@/components/workflow/BpmnViewer.vue'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const viewDialogVisible = ref(false)
const currentBpmnXml = ref('')

const queryForm = reactive({
  name: '',
  category: ''
})

onMounted(() => {
  fetchData()
})

const fetchData = async () => {
  loading.value = true
  try {
    const res = await listProcessDefinitions(queryForm)
    tableData.value = res.data || []
  } catch (error) {
    ElMessage.error('获取流程列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  fetchData()
}

const handleReset = () => {
  queryForm.name = ''
  queryForm.category = ''
  fetchData()
}

const handleDesign = () => {
  router.push('/workflow/definition/designer')
}

const handleView = async (row) => {
  try {
    const res = await getBpmnXml(row.id)
    currentBpmnXml.value = res.data
    viewDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取流程图失败')
  }
}

const handleToggleState = async (row) => {
  try {
    if (row.suspended) {
      await activateProcess(row.id)
      ElMessage.success('激活成功')
    } else {
      await suspendProcess(row.id)
      ElMessage.success('挂起成功')
    }
    fetchData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该流程定义吗？', '提示', {
      type: 'warning'
    })
    await deleteProcess(row.deploymentId, true)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString()
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}
</style>
