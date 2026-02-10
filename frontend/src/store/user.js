import { defineStore } from 'pinia'
import { login, logout } from '@/api/auth'
import { getToken, setToken, removeToken } from '@/utils/auth'

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
      return data
    },

    async logout() {
      try {
        await logout()
      } finally {
        this.token = ''
        this.userInfo = null
        removeToken()
      }
    },

    resetState() {
      this.token = ''
      this.userInfo = null
      removeToken()
    }
  }
})
