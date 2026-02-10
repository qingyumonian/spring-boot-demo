import { defineStore } from 'pinia'

export const usePermissionStore = defineStore('permission', {
  state: () => ({
    routes: [],
    addRoutes: []
  }),

  actions: {
    setRoutes(routes) {
      this.addRoutes = routes
      this.routes = routes
    }
  }
})
