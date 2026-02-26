<template>
  <div class="expense-apply">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>发起报销申请</span>
          <el-button @click="handleBack">返回</el-button>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        style="max-width: 600px;"
      >
        <el-form-item label="报销类型" prop="expenseType">
          <el-select v-model="form.expenseType" placeholder="请选择报销类型">
            <el-option label="差旅费" value="travel" />
            <el-option label="办公费" value="office" />
            <el-option label="招待费" value="entertainment" />
            <el-option label="交通费" value="transport" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>

        <el-form-item label="报销金额" prop="amount">
          <el-input-number
            v-model="form.amount"
            :min="0"
            :precision="2"
            :step="100"
            placeholder="请输入报销金额"
          />
        </el-form-item>

        <el-form-item label="报销事由" prop="reason">
          <el-input
            v-model="form.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入报销事由"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSave" :loading="loading">
            保存草稿
          </el-button>
          <el-button type="success" @click="handleSubmit" :loading="loading">
            保存并提交
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createExpense, submitExpense } from '@/api/expense'

const router = useRouter()
const loading = ref(false)
const formRef = ref(null)

const form = reactive({
  expenseType: '',
  amount: 0,
  reason: ''
})

const rules = {
  expenseType: [{ required: true, message: '请选择报销类型', trigger: 'change' }],
  amount: [
    { required: true, message: '请输入报销金额', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '金额必须大于0', trigger: 'blur' }
  ]
}

const handleBack = () => {
  router.push('/expense/list')
}

const handleSave = async () => {
  try {
    await formRef.value.validate()
    loading.value = true

    await createExpense(form)
    ElMessage.success('保存成功')
    router.push('/expense/list')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('保存失败: ' + error.message)
    }
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    loading.value = true

    const res = await createExpense(form)
    const expenseId = res.data?.id
    if (expenseId) {
      await submitExpense(expenseId)
      ElMessage.success('提交成功')
      router.push('/expense/list')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('提交失败: ' + error.message)
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
