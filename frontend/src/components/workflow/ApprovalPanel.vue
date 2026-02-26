<template>
  <el-card class="approval-panel">
    <template #header>
      <div class="card-header">
        <span>审批操作</span>
      </div>
    </template>

    <el-form :model="form" label-width="80px">
      <el-form-item label="审批意见">
        <el-input
          v-model="form.comment"
          type="textarea"
          :rows="3"
          placeholder="请输入审批意见"
        />
      </el-form-item>

      <el-form-item v-if="showApproveField" label="是否同意">
        <el-radio-group v-model="form.approved">
          <el-radio :label="true">同意</el-radio>
          <el-radio :label="false">不同意</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="handleApprove" :loading="loading">
          通过
        </el-button>
        <el-button type="warning" @click="handleReject" :loading="loading">
          退回
        </el-button>
        <el-button @click="handleDelegate" :loading="loading">
          转办
        </el-button>
      </el-form-item>
    </el-form>

    <!-- 退回节点选择对话框 -->
    <el-dialog
      v-model="rejectDialogVisible"
      title="选择退回节点"
      width="500px"
    >
      <el-form :model="rejectForm" label-width="80px">
        <el-form-item label="退回节点">
          <el-select v-model="rejectForm.targetActivityId" placeholder="请选择退回节点">
            <el-option
              v-for="activity in returnableActivities"
              :key="activity.activityId"
              :label="activity.activityName"
              :value="activity.activityId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="退回原因">
          <el-input
            v-model="rejectForm.reason"
            type="textarea"
            :rows="2"
            placeholder="请输入退回原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmReject" :loading="loading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 转办对话框 -->
    <el-dialog
      v-model="delegateDialogVisible"
      title="转办任务"
      width="500px"
    >
      <el-form :model="delegateForm" label-width="80px">
        <el-form-item label="转办给">
          <el-input v-model="delegateForm.targetUserId" placeholder="请输入目标用户ID" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="delegateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmDelegate" :loading="loading">确定</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { completeTask, rejectTask, transferTask, getReturnableActivities } from '@/api/workflow/task'

const props = defineProps({
  taskId: {
    type: String,
    required: true
  },
  showApproveField: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['success'])

const loading = ref(false)
const rejectDialogVisible = ref(false)
const delegateDialogVisible = ref(false)
const returnableActivities = ref([])

const form = reactive({
  comment: '',
  approved: true
})

const rejectForm = reactive({
  targetActivityId: '',
  reason: ''
})

const delegateForm = reactive({
  targetUserId: ''
})

const handleApprove = async () => {
  loading.value = true
  try {
    await completeTask(props.taskId, {
      comment: form.comment,
      variables: {
        approved: form.approved
      }
    })
    ElMessage.success('审批成功')
    emit('success')
  } catch (error) {
    ElMessage.error('审批失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

const handleReject = async () => {
  loading.value = true
  try {
    const res = await getReturnableActivities(props.taskId)
    returnableActivities.value = res.data || []
    if (returnableActivities.value.length === 0) {
      ElMessage.warning('没有可退回的节点')
      return
    }
    rejectDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取可退回节点失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

const confirmReject = async () => {
  if (!rejectForm.targetActivityId) {
    ElMessage.warning('请选择退回节点')
    return
  }
  loading.value = true
  try {
    await rejectTask(props.taskId, {
      targetActivityId: rejectForm.targetActivityId,
      reason: rejectForm.reason
    })
    ElMessage.success('退回成功')
    rejectDialogVisible.value = false
    emit('success')
  } catch (error) {
    ElMessage.error('退回失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

const handleDelegate = () => {
  delegateDialogVisible.value = true
}

const confirmDelegate = async () => {
  if (!delegateForm.targetUserId) {
    ElMessage.warning('请输入目标用户ID')
    return
  }
  loading.value = true
  try {
    await transferTask(props.taskId, delegateForm.targetUserId)
    ElMessage.success('转办成功')
    delegateDialogVisible.value = false
    emit('success')
  } catch (error) {
    ElMessage.error('转办失败: ' + error.message)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.approval-panel {
  margin-top: 20px;
}

.card-header {
  font-weight: bold;
}
</style>
