import Cookies from 'js-cookie'

const TOKEN_KEY = 'am_access_token'

export function getToken() {
  return Cookies.get(TOKEN_KEY) || localStorage.getItem(TOKEN_KEY)
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
  return Cookies.set(TOKEN_KEY, token)
}

export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
  return Cookies.remove(TOKEN_KEY)
}
