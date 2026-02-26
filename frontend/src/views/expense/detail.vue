<template>
  <div class="expense-detail">
    <el-card v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>报销详情</span>
          <el-button @click="handleBack">返回</el-button>
        </div>
      </template>

      <el-descriptions v-if="expense" :column="2" border>
        <el-descriptions-item label="报销单号">{{ expense.expenseNo }}</el-descriptions-item>
        <el-descriptions-item label="报销类型">{{ getTypeName(expense.expenseType) }}</el-descriptions-item>
        <el-descriptions-item label="报销金额">{{ expense.amount?.toFixed(2) }} 元</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(expense.status)">{{ expense.statusName }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="申请人">{{ expense.applicantName }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ formatDate(expense.applyTime) }}</el-descriptions-item>
        <el-descriptions-item label="报销事由" :span="2">{{ expense.reason }}</el-descriptions-item>
        <el-descriptions-item label="当前节点" v-if="expense.currentActivityName">
          {{ expense.currentActivityName }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getExpense } from '@/api/expense'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const expense = ref(null)

onMounted(() => {
  fetchData()
})

const fetchData = async () => {
  const id = route.params.id
  if (!id) {
    ElMessage.error('参数错误')
    return
  }

  loading.value = true
  try {
    const res = await getExpense(id)
    expense.value = res.data
  } catch (error) {
    ElMessage.error('获取报销详情失败')
  } finally {
    loading.value = false
  }
}

const handleBack = () => {
  router.push('/expense/list')
}

const getTypeName = (type) => {
  const types = {
    travel: '差旅费',
    office: '办公费',
    entertainment: '招待费',
    transport: '交通费',
    other: '其他'
  }
  return types[type] || type
}

const getStatusType = (status) => {
  const types = {
    0: 'info',
    1: 'primary',
    2: 'success',
    3: 'danger',
    4: 'warning'
  }
  return types[status] || 'info'
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
</style>
