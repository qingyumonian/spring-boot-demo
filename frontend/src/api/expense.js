import request from '@/utils/request'

// 创建报销申请
export function createExpense(data) {
  return request({
    url: '/api/workflow/expenses',
    method: 'post',
    data
  })
}

// 提交报销申请
export function submitExpense(id) {
  return request({
    url: `/api/workflow/expenses/${id}/submit`,
    method: 'post'
  })
}

// 获取报销详情
export function getExpense(id) {
  return request({
    url: `/api/workflow/expenses/${id}`,
    method: 'get'
  })
}

// 分页查询我的报销
export function listMyExpenses(params) {
  return request({
    url: '/api/workflow/expenses/my',
    method: 'get',
    params
  })
}
