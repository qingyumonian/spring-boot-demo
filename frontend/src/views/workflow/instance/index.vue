<template>
  <div class="process-instance">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的流程</span>
        </div>
      </template>

      <!-- 数据表格 -->
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="processDefinitionName" label="流程名称" min-width="150" />
        <el-table-column prop="businessKey" label="业务标识" min-width="150" />
        <el-table-column prop="startTime" label="发起时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.ended" type="success">已结束</el-tag>
            <el-tag v-else-if="row.suspended" type="warning">已挂起</el-tag>
            <el-tag v-else type="primary">进行中</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="currentActivityName" label="当前节点" width="120" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button
              v-if="!row.ended"
              link
              type="danger"
              @click="handleCancel(row)"
            >
              撤销
            </el-button>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listMyProcesses, cancelProcess } from '@/api/workflow/instance'

const loading = ref(false)
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

onMounted(() => {
  fetchData()
})

const fetchData = async () => {
  loading.value = true
  try {
    const res = await listMyProcesses({
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })
    tableData.value = res.data || []
    // 注意：这里没有返回total，需要后端支持
  } catch (error) {
    ElMessage.error('获取流程列表失败')
  } finally {
    loading.value = false
  }
}

const handleView = (row) => {
  // TODO: 跳转到流程详情页
  ElMessage.info('查看流程: ' + row.id)
}

const handleCancel = async (row) => {
  try {
    const { value } = await ElMessageBox.prompt('请输入撤销原因', '撤销流程', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPlaceholder: '请输入撤销原因'
    })
    await cancelProcess(row.id, value)
    ElMessage.success('撤销成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('撤销失败')
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

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
