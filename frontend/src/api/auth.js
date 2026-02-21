import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/api/auth/form',
    method: 'post',
    params: data
  })
}

export function logout() {
  return request({
    url: '/api/auth/logout',
    method: 'post'
  })
}

/**
 * 获取所有已启用的SSO提供商
 */
export function getSsoProviders() {
  return request({
    url: '/api/auth/sso/providers',
    method: 'get'
  })
}

/**
 * 获取指定SSO提供商的登录URL
 * @param {string} provider - 提供商名称（cas/keycloak）
 * @param {string} redirectUri - 登录成功后重定向的前端页面URL
 * @param {string} state - 可选的状态参数
 */
export function getSsoLoginUrl(provider, redirectUri, state) {
  return request({
    url: `/api/auth/sso/${provider}/login-url`,
    method: 'get',
    params: { redirectUri, state }
  })
}

/**
 * 获取SSO登出URL
 * @param {string} provider - 提供商名称
 * @param {string} postLogoutRedirectUri - 登出后重定向URL
 */
export function getSsoLogoutUrl(provider, postLogoutRedirectUri) {
  return request({
    url: `/api/auth/sso/${provider}/logout-url`,
    method: 'get',
    params: { postLogoutRedirectUri }
  })
}

/**
 * 执行SSO登出
 * @param {string} provider - 提供商名称
 * @param {string} postLogoutRedirectUri - 登出后重定向URL
 */
export function ssoLogout(provider, postLogoutRedirectUri) {
  return request({
    url: `/api/auth/sso/${provider}/logout`,
    method: 'post',
    params: { postLogoutRedirectUri }
  })
}
