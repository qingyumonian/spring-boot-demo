import { createRouter, createWebHistory } from 'vue-router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getToken, setToken } from '@/utils/auth'
import { useUserStore } from '@/store/user'

NProgress.configure({ showSpinner: false })

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: 'Login' }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: 'Dashboard', icon: 'House' }
      },
      {
        path: 'system/user',
        name: 'User',
        component: () => import('@/views/system/user/index.vue'),
        meta: { title: 'User Management', icon: 'User' }
      },
      {
        path: 'system/role',
        name: 'Role',
        component: () => import('@/views/system/role/index.vue'),
        meta: { title: 'Role Management', icon: 'UserFilled' }
      },
      {
        path: 'system/menu',
        name: 'Menu',
        component: () => import('@/views/system/menu/index.vue'),
        meta: { title: 'Menu Management', icon: 'Menu' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/dashboard'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Route guards
const whiteList = ['/login']

router.beforeEach(async (to, from, next) => {
  NProgress.start()
  document.title = to.meta.title ? `${to.meta.title} - Admin Panel` : 'Admin Panel'

  // 处理SSO回调后URL中的token参数
  const urlToken = to.query.token
  if (urlToken) {
    setToken(urlToken)
    // 更新 store 中的 token
    const userStore = useUserStore()
    userStore.token = urlToken
    // 移除URL中的token参数，保持URL干净
    const query = { ...to.query }
    delete query.token
    delete query.error
    delete query.error_description
    next({ path: to.path, query, replace: true })
    NProgress.done()
    return
  }

  // 处理SSO错误
  if (to.query.error) {
    console.error('SSO Error:', to.query.error, to.query.error_description)
    next({ path: '/login', replace: true })
    NProgress.done()
    return
  }

  const hasToken = getToken()

  if (hasToken) {
    if (to.path === '/login') {
      next({ path: '/' })
      NProgress.done()
    } else {
      const userStore = useUserStore()
      // Fetch user info if token exists but userInfo is missing (page refresh)
      if (!userStore.userInfo) {
        try {
          await userStore.getUserInfo()
        } catch (error) {
          // Token invalid, clear and redirect to login
          userStore.resetState()
          next(`/login?redirect=${to.path}`)
          NProgress.done()
          return
        }
      }
      next()
    }
  } else {
    if (whiteList.includes(to.path)) {
      next()
    } else {
      next(`/login?redirect=${to.path}`)
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})

export default router
