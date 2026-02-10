import request from '@/utils/request'

export function getRoleList(params) {
  return request({
    url: '/api/roles',
    method: 'get',
    params
  })
}

export function getRoleById(id) {
  return request({
    url: `/api/roles/${id}`,
    method: 'get'
  })
}

export function createRole(data) {
  return request({
    url: '/api/roles',
    method: 'post',
    data
  })
}

export function updateRole(id, data) {
  return request({
    url: `/api/roles/${id}`,
    method: 'put',
    data
  })
}

export function deleteRole(id) {
  return request({
    url: `/api/roles/${id}`,
    method: 'delete'
  })
}

export function assignMenus(roleId, menuIds) {
  return request({
    url: `/api/roles/${roleId}/menus`,
    method: 'put',
    data: menuIds
  })
}
