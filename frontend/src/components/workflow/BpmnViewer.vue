<template>
  <div class="bpmn-viewer">
    <div class="toolbar">
      <el-button-group>
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
    <div class="viewer-container">
      <div ref="canvasRef" class="canvas"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'
import BpmnViewer from 'bpmn-js/lib/NavigatedViewer'
import { ZoomIn, ZoomOut } from '@element-plus/icons-vue'

const props = defineProps({
  xml: {
    type: String,
    required: true
  },
  highlightActivities: {
    type: Array,
    default: () => []
  }
})

const canvasRef = ref(null)
let viewer = null
let currentZoom = 1

onMounted(async () => {
  viewer = new BpmnViewer({
    container: canvasRef.value
  })

  if (props.xml) {
    await importXml(props.xml)
  }
})

onBeforeUnmount(() => {
  if (viewer) {
    viewer.destroy()
  }
})

watch(() => props.xml, async (newXml) => {
  if (newXml && viewer) {
    await importXml(newXml)
  }
})

watch(() => props.highlightActivities, () => {
  highlightActivities()
})

const importXml = async (xml) => {
  try {
    await viewer.importXML(xml)
    viewer.get('canvas').zoom('fit-viewport')
    highlightActivities()
  } catch (err) {
    console.error('Failed to import BPMN:', err)
  }
}

const highlightActivities = () => {
  if (!viewer || !props.highlightActivities.length) return

  const canvas = viewer.get('canvas')
  const elementRegistry = viewer.get('elementRegistry')

  // 清除之前的高亮
  elementRegistry.forEach((element) => {
    if (element.type !== 'bpmn:Process') {
      canvas.removeMarker(element.id, 'highlight')
    }
  })

  // 添加新的高亮
  props.highlightActivities.forEach((activityId) => {
    canvas.addMarker(activityId, 'highlight')
  })
}

const handleZoomIn = () => {
  currentZoom = Math.min(currentZoom + 0.1, 3)
  viewer.get('canvas').zoom(currentZoom)
}

const handleZoomOut = () => {
  currentZoom = Math.max(currentZoom - 0.1, 0.3)
  viewer.get('canvas').zoom(currentZoom)
}

const handleResetZoom = () => {
  viewer.get('canvas').zoom('fit-viewport')
  currentZoom = 1
}
</script>

<style scoped>
.bpmn-viewer {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.toolbar {
  padding: 10px;
  background: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
}

.viewer-container {
  flex: 1;
  position: relative;
  overflow: hidden;
}

.canvas {
  width: 100%;
  height: 100%;
}

:deep(.bjs-powered-by) {
  display: none;
}

:deep(.highlight .djs-visual > *) {
  stroke: #67c23a !important;
  fill: rgba(103, 194, 58, 0.2) !important;
}
</style>
