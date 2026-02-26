<template>
  <div class="process-designer">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>流程设计器</span>
          <div>
            <el-button @click="handleBack">返回</el-button>
            <el-button type="primary" @click="handleDeploy">部署流程</el-button>
          </div>
        </div>
      </template>

      <div class="designer-wrapper">
        <BpmnDesigner ref="designerRef" :xml="initialXml" @save="handleSave" />
      </div>
    </el-card>

    <!-- 部署对话框 -->
    <el-dialog v-model="deployDialogVisible" title="部署流程" width="500px">
      <el-form :model="deployForm" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="流程名称" prop="name">
          <el-input v-model="deployForm.name" placeholder="请输入流程名称" />
        </el-form-item>
        <el-form-item label="流程分类" prop="category">
          <el-input v-model="deployForm.category" placeholder="请输入流程分类" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="deployDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmDeploy" :loading="loading">部署</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import BpmnDesigner from '@/components/workflow/BpmnDesigner.vue'
import { deployProcess, getBpmnXml } from '@/api/workflow/definition'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const deployDialogVisible = ref(false)
const designerRef = ref(null)
const formRef = ref(null)
const initialXml = ref('')
const currentXml = ref('')

const deployForm = reactive({
  name: '',
  category: ''
})

const rules = {
  name: [{ required: true, message: '请输入流程名称', trigger: 'blur' }]
}

onMounted(async () => {
  // 如果是编辑模式，加载已有流程
  const processDefinitionId = route.query.id
  if (processDefinitionId) {
    try {
      const res = await getBpmnXml(processDefinitionId)
      initialXml.value = res.data
    } catch (error) {
      ElMessage.error('加载流程失败')
    }
  }
})

const handleBack = () => {
  router.push('/workflow/definition')
}

const handleSave = (xml) => {
  currentXml.value = xml
  ElMessage.success('保存成功')
}

const handleDeploy = async () => {
  try {
    currentXml.value = await designerRef.value.getXml()
    deployDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取流程内容失败')
  }
}

const confirmDeploy = async () => {
  try {
    await formRef.value.validate()
    loading.value = true

    await deployProcess({
      name: deployForm.name,
      category: deployForm.category,
      bpmnXml: currentXml.value
    })

    ElMessage.success('部署成功')
    deployDialogVisible.value = false
    router.push('/workflow/definition')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('部署失败: ' + error.message)
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

.designer-wrapper {
  height: calc(100vh - 220px);
  min-height: 500px;
}
</style>
