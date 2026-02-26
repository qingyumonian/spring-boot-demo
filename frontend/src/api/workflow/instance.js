import request from '@/utils/request'

// 发起流程
export function startProcess(data) {
  return request({
    url: '/api/workflow/instances/start',
    method: 'post',
    data
  })
}

// 撤销流程
export function cancelProcess(id, reason) {
  return request({
    url: `/api/workflow/instances/${id}/cancel`,
    method: 'post',
    params: { reason }
  })
}

// 查询我的流程
export function listMyProcesses(params) {
  return request({
    url: '/api/workflow/instances/my',
    method: 'get',
    params
  })
}

// 获取流程实例详情
export function getProcessInstance(id) {
  return request({
    url: `/api/workflow/instances/${id}`,
    method: 'get'
  })
}
