import axios from 'axios'
import Cookies from 'js-cookie'
import { ElMessage } from 'element-plus'
import router from '@/router'

const TOKEN_KEY = 'am_access_token'

const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000
})

// Request interceptor
service.interceptors.request.use(
  config => {
    const token = Cookies.get(TOKEN_KEY) || localStorage.getItem(TOKEN_KEY)
    if (token) {
      config.headers['am_access_token'] = token
    }
    return config
  },
  error => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

// Response interceptor
service.interceptors.response.use(
  response => {
    const res = response.data

    // If code is not 200, treat as error
    if (res.code !== 200) {
      ElMessage.error(res.message || 'Request failed')

      // Token expired or unauthorized
      if (res.code === 401) {
        Cookies.remove(TOKEN_KEY)
        localStorage.removeItem(TOKEN_KEY)
        router.push('/login')
      }

      return Promise.reject(new Error(res.message || 'Error'))
    }

    return res
  },
  error => {
    console.error('Response error:', error)
    ElMessage.error(error.message || 'Network error')
    return Promise.reject(error)
  }
)

export default service
