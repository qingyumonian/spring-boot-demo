import request from '@/utils/request'

// 查询待办任务
export function listTodoTasks(params) {
  return request({
    url: '/api/workflow/tasks/todo',
    method: 'get',
    params
  })
}

// 查询已办任务
export function listDoneTasks(params) {
  return request({
    url: '/api/workflow/tasks/done',
    method: 'get',
    params
  })
}

// 获取任务详情
export function getTask(id) {
  return request({
    url: `/api/workflow/tasks/${id}`,
    method: 'get'
  })
}

// 完成任务
export function completeTask(id, data) {
  return request({
    url: `/api/workflow/tasks/${id}/complete`,
    method: 'post',
    data
  })
}

// 退回任务
export function rejectTask(id, data) {
  return request({
    url: `/api/workflow/tasks/${id}/reject`,
    method: 'post',
    data
  })
}

// 获取可退回节点
export function getReturnableActivities(id) {
  return request({
    url: `/api/workflow/tasks/${id}/returnable-activities`,
    method: 'get'
  })
}

// 签收任务
export function claimTask(id) {
  return request({
    url: `/api/workflow/tasks/${id}/claim`,
    method: 'post'
  })
}

// 取消签收
export function unclaimTask(id) {
  return request({
    url: `/api/workflow/tasks/${id}/unclaim`,
    method: 'post'
  })
}

// 转办任务
export function transferTask(id, targetUserId) {
  return request({
    url: `/api/workflow/tasks/${id}/transfer`,
    method: 'post',
    params: { targetUserId }
  })
}
