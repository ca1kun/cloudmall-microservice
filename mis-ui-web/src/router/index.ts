import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
// 引入你原来的 admin 路由数据 (假设你把它放在同目录的 router.ts 或 routerData.ts 中)
// 注意：请确保 routerData 里的 component 路径也是对的
import { adminRouters } from './adminRouter' 
import { mallRoutes } from './mallRoutes'


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // -----------------------------------------------------------
    // 1. 登录页 (无需权限)
    // -----------------------------------------------------------
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/login/index.vue'),
    },

    // -----------------------------------------------------------
    // 1. 后台管理入口 (ADMIN)
    // -----------------------------------------------------------
    {
      path: '/', 
      // 👇【修改这里】原来的路径是 @/layout/index.vue，现在要改成 admin 目录下的
      component: () => import('@/layout/admin/index.vue'), 
      redirect: '/home', 
      meta: { requiresAuth: true, role: 'ADMIN' },
      children: [
        ...adminRouters
      ]
    },

    // -----------------------------------------------------------
    // 2. 商城入口 (CUSTOMER)
    // -----------------------------------------------------------
    {
      path: '/mall',
      // 👇【修改这里】指向你新建的 mall 目录下的布局
      component: () => import('@/layout/mall/index.vue'), 
      redirect: '/mall/home',
      meta: { requiresAuth: true, role: 'CUSTOMER' },
      children: [
        ...mallRoutes
      ]
    }, 
    // 404 页面 (可选)
    // {
    //    path: '/:pathMatch(.*)*',
    //    component: () => import('@/views/404.vue')
    // }
  ],   
})

// -----------------------------------------------------------
// 路由守卫 (核心逻辑：拦截器)
// -----------------------------------------------------------
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')

  // 1. 如果没有 Token，且去的不是登录页 -> 强制去登录
  if (!token && to.path !== '/login') {
    return next('/login')
  }

  // 2. 如果有 Token
  if (token) {
    // 2.1 如果想去登录页 -> 根据角色踢回首页
    if (to.path === '/login') {
      if (role === 'ADMIN') return next('/home')
      if (role === 'CUSTOMER') return next('/mall/home')
    }

    // 2.2 【关键修复】权限隔离 & 防止死循环
    // 如果是顾客，试图访问非商城页面 (且不是去商城首页)
    if (role === 'CUSTOMER') {
        // 如果当前路径不是以 /mall 开头，强制踢回商城首页
        if (!to.path.startsWith('/mall')) {
            return next('/mall/home')
        }
        // ⚠️【重点】如果已经是 /mall 开头了，什么都别做，直接执行最后的 next()
    }

    // 如果是管理员，试图访问商城页面 (看你需求，这里假设强制踢回后台)
    if (role === 'ADMIN') {
        if (to.path.startsWith('/mall')) {
            return next('/home')
        }
    }
  }

  // 3. 放行 (死循环通常是因为这行没执行到，或者上面一直 return next(...))
  next()
})

export default router