import request from '@/utils/request'

export function getMenuList(params) {
  return request({
    url: '/api/menus',
    method: 'get',
    params
  })
}

export function getMenuTree() {
  return request({
    url: '/api/menus/tree',
    method: 'get'
  })
}

export function getMenuById(id) {
  return request({
    url: `/api/menus/${id}`,
    method: 'get'
  })
}

export function createMenu(data) {
  return request({
    url: '/api/menus',
    method: 'post',
    data
  })
}

export function updateMenu(id, data) {
  return request({
    url: `/api/menus/${id}`,
    method: 'put',
    data
  })
}

export function deleteMenu(id) {
  return request({
    url: `/api/menus/${id}`,
    method: 'delete'
  })
}
