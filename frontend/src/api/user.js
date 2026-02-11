import request from '@/utils/request'

export function getCurrentUser() {
  return request({
    url: '/api/users/current',
    method: 'get'
  })
}

export function getUserList(params) {
  return request({
    url: '/api/users/list',
    method: 'get',
    params
  })
}

export function getUserById(id) {
  return request({
    url: `/api/users/${id}`,
    method: 'get'
  })
}

export function createUser(data) {
  return request({
    url: '/api/users',
    method: 'post',
    data
  })
}

export function updateUser(id, data) {
  return request({
    url: `/api/users/${id}`,
    method: 'put',
    data
  })
}

export function deleteUser(id) {
  return request({
    url: `/api/users/${id}`,
    method: 'delete'
  })
}

export function assignRoles(userId, roleIds) {
  return request({
    url: `/api/users/${userId}/roles`,
    method: 'put',
    data: roleIds
  })
}
