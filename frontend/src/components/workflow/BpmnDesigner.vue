<template>
  <div class="bpmn-designer">
    <div class="toolbar">
      <el-button-group>
        <el-button type="primary" @click="handleSave">
          <el-icon><Download /></el-icon>
          保存
        </el-button>
        <el-button @click="handleUndo">
          <el-icon><RefreshLeft /></el-icon>
          撤销
        </el-button>
        <el-button @click="handleRedo">
          <el-icon><RefreshRight /></el-icon>
          重做
        </el-button>
        <el-button @click="handleZoomIn">
          <el-icon><ZoomIn /></el-icon>
        </el-button>
        <el-button @click="handleZoomOut">
          <el-icon><ZoomOut /></el-icon>
        </el-button>
        <el-button @click="handleResetZoom">
          适应画布
        </el-button>
      </el-button-group>
    </div>
    <div class="designer-container">
      <div ref="canvasRef" class="canvas"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import BpmnModeler from 'bpmn-js/lib/Modeler'
import { Download, RefreshLeft, RefreshRight, ZoomIn, ZoomOut } from '@element-plus/icons-vue'

const props = defineProps({
  xml: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['save'])

const canvasRef = ref(null)
let modeler = null
let currentZoom = 1

// 默认BPMN XML
const defaultXml = `<?xml version="1.0" encoding="UTF-8"?>
<bpmn:definitions xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL"
                  xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI"
                  xmlns:dc="http://www.omg.org/spec/DD/20100524/DC"
                  xmlns:flowable="http://flowable.org/bpmn"
                  targetNamespace="http://bpmn.io/schema/bpmn">
  <bpmn:process id="Process_1" isExecutable="true">
    <bpmn:startEvent id="StartEvent_1" name="开始" />
  </bpmn:process>
  <bpmndi:BPMNDiagram id="BPMNDiagram_1">
    <bpmndi:BPMNPlane id="BPMNPlane_1" bpmnElement="Process_1">
      <bpmndi:BPMNShape id="StartEvent_1_di" bpmnElement="StartEvent_1">
        <dc:Bounds x="179" y="159" width="36" height="36" />
        <bpmndi:BPMNLabel>
          <dc:Bounds x="186" y="202" width="22" height="14" />
        </bpmndi:BPMNLabel>
      </bpmndi:BPMNShape>
    </bpmndi:BPMNPlane>
  </bpmndi:BPMNDiagram>
</bpmn:definitions>`

onMounted(async () => {
  modeler = new BpmnModeler({
    container: canvasRef.value,
    keyboard: {
      bindTo: window
    }
  })

  try {
    const xml = props.xml || defaultXml
    await modeler.importXML(xml)
    modeler.get('canvas').zoom('fit-viewport')
  } catch (err) {
    console.error('Failed to import BPMN:', err)
  }
})

onBeforeUnmount(() => {
  if (modeler) {
    modeler.destroy()
  }
})

const handleSave = async () => {
  try {
    const { xml } = await modeler.saveXML({ format: true })
    emit('save', xml)
  } catch (err) {
    console.error('Failed to save BPMN:', err)
  }
}

const handleUndo = () => {
  modeler.get('commandStack').undo()
}

const handleRedo = () => {
  modeler.get('commandStack').redo()
}

const handleZoomIn = () => {
  currentZoom = Math.min(currentZoom + 0.1, 3)
  modeler.get('canvas').zoom(currentZoom)
}

const handleZoomOut = () => {
  currentZoom = Math.max(currentZoom - 0.1, 0.3)
  modeler.get('canvas').zoom(currentZoom)
}

const handleResetZoom = () => {
  modeler.get('canvas').zoom('fit-viewport')
  currentZoom = 1
}

// 暴露方法供父组件调用
defineExpose({
  getXml: async () => {
    const { xml } = await modeler.saveXML({ format: true })
    return xml
  }
})
</script>

<style scoped>
.bpmn-designer {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.toolbar {
  padding: 10px;
  background: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
}

.designer-container {
  flex: 1;
  position: relative;
  overflow: hidden;
}

.canvas {
  width: 100%;
  height: 100%;
}

/* 引入bpmn-js样式 */
:deep(.bjs-powered-by) {
  display: none;
}
</style>
