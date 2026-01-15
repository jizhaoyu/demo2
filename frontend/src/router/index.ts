import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/store/user'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/home',
    component: () => import('@/layouts/MainLayout.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/book/BookList.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'book/publish',
        name: 'BookPublish',
        component: () => import('@/views/book/BookPublish.vue'),
        meta: { title: '发布书籍', requiresAuth: true }
      },
      {
        path: 'book/edit/:id',
        name: 'BookEdit',
        component: () => import('@/views/book/BookPublish.vue'),
        meta: { title: '编辑书籍', requiresAuth: true }
      },
      {
        path: 'book/:id',
        name: 'BookDetail',
        component: () => import('@/views/book/BookDetail.vue'),
        meta: { title: '书籍详情' }
      },
      {
        path: 'my/books',
        name: 'MyBooks',
        component: () => import('@/views/book/MyBooks.vue'),
        meta: { title: '我的书籍', requiresAuth: true }
      },
      {
        path: 'my/favorites',
        name: 'MyFavorites',
        component: () => import('@/views/user/MyFavorites.vue'),
        meta: { title: '我的收藏', requiresAuth: true }
      },
      {
        path: 'my/orders',
        name: 'MyOrders',
        component: () => import('@/views/order/OrderList.vue'),
        meta: { title: '我的订单', requiresAuth: true }
      },
      {
        path: 'order/create/:bookId',
        name: 'OrderCreate',
        component: () => import('@/views/order/OrderCreate.vue'),
        meta: { title: '创建订单', requiresAuth: true }
      },
      {
        path: 'order/:id',
        name: 'OrderDetail',
        component: () => import('@/views/order/OrderDetail.vue'),
        meta: { title: '订单详情', requiresAuth: true }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/user/Profile.vue'),
        meta: { title: '个人中心', requiresAuth: true }
      },
      {
        path: 'user/:id',
        name: 'UserProfile',
        component: () => import('@/views/user/UserProfile.vue'),
        meta: { title: '用户主页' }
      },
      {
        path: 'messages',
        name: 'Messages',
        component: () => import('@/views/message/MessageCenter.vue'),
        meta: { title: '消息中心', requiresAuth: true }
      },
      {
        path: 'chat/:userId',
        name: 'Chat',
        component: () => import('@/views/message/Chat.vue'),
        meta: { title: '聊天', requiresAuth: true }
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: '',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '管理后台' }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/UserManage.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'categories',
        name: 'AdminCategories',
        component: () => import('@/views/admin/CategoryManage.vue'),
        meta: { title: '分类管理' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/NotFound.vue'),
    meta: { title: '页面不存在' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, _from, next) => {
  // 设置页面标题
  document.title = `${to.meta.title || '二手书交易平台'} - 二手书交易平台`
  
  const userStore = useUserStore()
  
  // 需要登录的页面
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
    return
  }
  
  // 需要管理员权限的页面
  if (to.meta.requiresAdmin && !userStore.isAdmin) {
    next('/home')
    return
  }
  
  // 已登录用户访问登录/注册页面
  if ((to.name === 'Login' || to.name === 'Register') && userStore.isLoggedIn) {
    next('/home')
    return
  }
  
  next()
})

export default router
