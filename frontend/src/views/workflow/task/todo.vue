<template>
  <div class="todo-tasks">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>待办任务</span>
        </div>
      </template>

      <!-- 数据表格 -->
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="title" label="流程标题" min-width="200" />
        <el-table-column prop="name" label="当前节点" width="120" />
        <el-table-column prop="processDefinitionName" label="流程名称" width="150" />
        <el-table-column prop="initiatorName" label="发起人" width="100" />
        <el-table-column prop="createTime" label="到达时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleProcess(row)">办理</el-button>
            <el-button link type="primary" @click="handleClaim(row)" v-if="!row.assignee">签收</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="fetchData"
          @current-change="fetchData"
        />
      </div>
    </el-card>

    <!-- 办理对话框 -->
    <el-dialog v-model="processDialogVisible" title="办理任务" width="600px">
      <div v-if="currentTask">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="流程标题">{{ currentTask.title }}</el-descriptions-item>
          <el-descriptions-item label="当前节点">{{ currentTask.name }}</el-descriptions-item>
          <el-descriptions-item label="发起人">{{ currentTask.initiatorName }}</el-descriptions-item>
          <el-descriptions-item label="到达时间">{{ formatDate(currentTask.createTime) }}</el-descriptions-item>
        </el-descriptions>

        <el-divider />

        <!-- 根据表单类型显示业务表单 -->
        <div v-if="currentTask.formType === 'expense'" class="business-form">
          <el-button type="primary" link @click="viewExpense">查看报销详情</el-button>
        </div>

        <ApprovalPanel
          :task-id="currentTask.id"
          @success="handleApprovalSuccess"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { listTodoTasks, claimTask } from '@/api/workflow/task'
import ApprovalPanel from '@/components/workflow/ApprovalPanel.vue'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const processDialogVisible = ref(false)
const currentTask = ref(null)

onMounted(() => {
  fetchData()
})

const fetchData = async () => {
  loading.value = true
  try {
    const res = await listTodoTasks({
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })
    tableData.value = res.data || []
  } catch (error) {
    ElMessage.error('获取待办任务失败')
  } finally {
    loading.value = false
  }
}

const handleProcess = (row) => {
  currentTask.value = row
  processDialogVisible.value = true
}

const handleClaim = async (row) => {
  try {
    await claimTask(row.id)
    ElMessage.success('签收成功')
    fetchData()
  } catch (error) {
    ElMessage.error('签收失败')
  }
}

const viewExpense = () => {
  if (currentTask.value && currentTask.value.formId) {
    router.push(`/expense/detail/${currentTask.value.formId}`)
  }
}

const handleApprovalSuccess = () => {
  processDialogVisible.value = false
  fetchData()
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

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.business-form {
  margin-bottom: 20px;
}
</style>
