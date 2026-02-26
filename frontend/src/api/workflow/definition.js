import request from '@/utils/request'

// 部署流程
export function deployProcess(data) {
  return request({
    url: '/api/workflow/definitions/deploy',
    method: 'post',
    data
  })
}

// 删除流程定义
export function deleteProcess(deploymentId, cascade = false) {
  return request({
    url: `/api/workflow/definitions/${deploymentId}`,
    method: 'delete',
    params: { cascade }
  })
}

// 查询流程定义列表
export function listProcessDefinitions(params) {
  return request({
    url: '/api/workflow/definitions/list',
    method: 'get',
    params
  })
}

// 获取流程定义详情
export function getProcessDefinition(id) {
  return request({
    url: `/api/workflow/definitions/${id}`,
    method: 'get'
  })
}

// 获取流程定义BPMN XML
export function getBpmnXml(id) {
  return request({
    url: `/api/workflow/definitions/${id}/bpmn`,
    method: 'get'
  })
}

// 挂起流程定义
export function suspendProcess(id) {
  return request({
    url: `/api/workflow/definitions/${id}/suspend`,
    method: 'post'
  })
}

// 激活流程定义
export function activateProcess(id) {
  return request({
    url: `/api/workflow/definitions/${id}/activate`,
    method: 'post'
  })
}
