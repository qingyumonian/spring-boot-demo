import { usePermissionStore } from '@/store/permission'

function checkPermission(el, binding) {
  const permissionStore = usePermissionStore()
  const { value } = binding

  if (value) {
    const hasPermission = permissionStore.hasPermission(value)
    if (!hasPermission) {
      el.parentNode && el.parentNode.removeChild(el)
    }
  }
}

export const hasPermission = {
  mounted(el, binding) {
    checkPermission(el, binding)
  },
  updated(el, binding) {
    checkPermission(el, binding)
  }
}

export default {
  install(app) {
    app.directive('hasPermission', hasPermission)
  }
}
