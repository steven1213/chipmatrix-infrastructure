import { createRouter, createWebHistory, RouteRecordRaw, RouteLocationNormalized, NavigationGuardNext } from 'vue-router'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: '/login'  // 添加根路径重定向
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/Index.vue'),
  },
  {
    path: '/main',  // Dashboard 应该是受保护的路由
    name: 'Main',
    component: () => import('../App.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 白名单路由 - 不需要登录就能访问的页面
const whiteList = ['/login', '/register', '/forgot-password']

/// 全局前置守卫
router.beforeEach((to: RouteLocationNormalized, _: RouteLocationNormalized, next: NavigationGuardNext) => {
  const token = localStorage.getItem('token') // 或其他验证登录状态的方法
  const isWhiteListed = whiteList.includes(to.path)

  if (!token) {
    if (isWhiteListed) {
      // 未登录时访问白名单页面，允许
      next()
    } else {
      // 未登录时访问其他页面，重定向到登录页
      next(`/login?redirect=${to.path}`)
    }
    return
  }

  // 已登录状态
  if (to.path === '/login') {
    // 已登录时访问登录页，重定向到首页
    next('/main')
  } else {
    // 已登录时访问其他页面，允许
    next()
  }

})


export default router