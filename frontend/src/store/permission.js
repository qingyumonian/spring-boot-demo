import { defineStore } from 'pinia'

export const usePermissionStore = defineStore('permission', {
  state: () => ({
    routes: [],
    addRoutes: [],
    permissions: []
  }),

  getters: {
    hasPermission: (state) => {
      return (permission) => {
        if (!permission) return true
        if (Array.isArray(permission)) {
          return permission.some(p => state.permissions.includes(p))
        }
        return state.permissions.includes(permission)
      }
    }
  },

  actions: {
    setRoutes(routes) {
      this.addRoutes = routes
      this.routes = routes
    },

    setPermissions(permissions) {
      this.permissions = permissions || []
    },

    resetPermissions() {
      this.permissions = []
    }
  }
})
