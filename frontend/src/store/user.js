import { defineStore } from 'pinia'
import { login, logout } from '@/api/auth'
import { getCurrentUser } from '@/api/user'
import { getToken, setToken, removeToken } from '@/utils/auth'
import { usePermissionStore } from './permission'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken() || '',
    userInfo: null
  }),

  getters: {
    isLoggedIn: (state) => !!state.token
  },

  actions: {
    async login(loginForm) {
      const { data } = await login(loginForm)
      this.token = data.token
      this.userInfo = data.user
      setToken(data.token)

      // Store permissions
      const permissionStore = usePermissionStore()
      if (data.permissions) {
        permissionStore.setPermissions(data.permissions)
      }

      return data
    },

    async getUserInfo() {
      try {
        const { data } = await getCurrentUser()
        this.userInfo = data.user || data

        // Store permissions
        const permissionStore = usePermissionStore()
        if (data.permissions) {
          permissionStore.setPermissions(data.permissions)
        }

        return data
      } catch (error) {
        console.error('Failed to get user info:', error)
        throw error
      }
    },

    async logout() {
      try {
        await logout()
      } finally {
        this.token = ''
        this.userInfo = null
        removeToken()

        // Clear permissions
        const permissionStore = usePermissionStore()
        permissionStore.resetPermissions()
      }
    },

    resetState() {
      this.token = ''
      this.userInfo = null
      removeToken()

      // Clear permissions
      const permissionStore = usePermissionStore()
      permissionStore.resetPermissions()
    }
  }
})
