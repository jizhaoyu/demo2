<template>
  <n-layout class="main-layout">
    <n-layout-header bordered class="header">
      <div class="header-content">
        <div class="logo" @click="router.push('/home')">
          <div class="logo-icon">
            <n-icon size="24" color="#fff"><BookOutline /></n-icon>
          </div>
          <span class="logo-text">二手书交易</span>
        </div>
        
        <div class="nav-menu">
          <div v-for="item in menuItems" :key="item.key" class="nav-item"
               :class="{ active: activeMenu === item.key }" @click="router.push(item.path)">
            {{ item.label }}
          </div>
        </div>
        
        <div class="header-right">
          <template v-if="userStore.isLoggedIn">
            <n-badge :value="unreadCount" :max="99" :show="unreadCount > 0">
              <n-button quaternary circle size="small" @click="router.push('/home/messages')">
                <template #icon><n-icon size="20"><ChatbubbleOutline /></n-icon></template>
              </n-button>
            </n-badge>
            
            <n-dropdown :options="userMenuOptions" @select="handleUserMenuSelect">
              <div class="user-trigger">
                <n-avatar :size="32" :src="userStore.user?.avatar" round>
                  {{ userStore.username?.charAt(0).toUpperCase() }}
                </n-avatar>
                <span class="username">{{ userStore.username }}</span>
                <n-icon size="16"><ChevronDownOutline /></n-icon>
              </div>
            </n-dropdown>
          </template>
          
          <template v-else>
            <n-button quaternary @click="router.push('/login')">登录</n-button>
            <n-button type="primary" @click="router.push('/register')">注册</n-button>
          </template>
        </div>
      </div>
    </n-layout-header>
    
    <n-layout-content class="content">
      <router-view />
    </n-layout-content>
    
    <n-layout-footer bordered class="footer">
      <div class="footer-content">
        <div class="footer-brand">
          <n-icon size="20" color="#18a058"><BookOutline /></n-icon>
          <span>二手书交易平台</span>
        </div>
        <p class="footer-slogan">让闲置书籍流动起来，让知识传递更远</p>
        <p class="footer-copyright">© 2024 二手书交易平台 All Rights Reserved</p>
      </div>
    </n-layout-footer>
  </n-layout>
</template>

<script setup lang="ts">
import { computed, ref, h, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { NIcon, useMessage } from 'naive-ui'
import { BookOutline, ChatbubbleOutline, ChevronDownOutline, PersonOutline, HeartOutline, 
         ListOutline, LogOutOutline, SettingsOutline } from '@vicons/ionicons5'
import { useUserStore } from '@/store/user'
import { messageApi } from '@/service/message'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const message = useMessage()

window.$message = message

const unreadCount = ref(0)

const activeMenu = computed(() => {
  const path = route.path
  if (path === '/home' || path === '/') return 'home'
  if (path.startsWith('/home/book/publish')) return 'publish'
  if (path.startsWith('/home/my/books')) return 'my-books'
  if (path.startsWith('/home/my/orders')) return 'my-orders'
  return ''
})

const menuItems = computed(() => {
  const items = [{ label: '首页', key: 'home', path: '/home' }]
  if (userStore.isLoggedIn) {
    items.push(
      { label: '发布书籍', key: 'publish', path: '/home/book/publish' },
      { label: '我的书籍', key: 'my-books', path: '/home/my/books' },
      { label: '我的订单', key: 'my-orders', path: '/home/my/orders' }
    )
  }
  return items
})

const renderIcon = (icon: any) => () => h(NIcon, null, { default: () => h(icon) })

const userMenuOptions = computed(() => {
  const options: any[] = [
    { label: '个人中心', key: 'profile', icon: renderIcon(PersonOutline) },
    { label: '我的收藏', key: 'favorites', icon: renderIcon(HeartOutline) },
    { label: '我的订单', key: 'orders', icon: renderIcon(ListOutline) },
    { type: 'divider', key: 'd1' }
  ]
  if (userStore.isAdmin) {
    options.push({ label: '管理后台', key: 'admin', icon: renderIcon(SettingsOutline) })
  }
  options.push({ label: '退出登录', key: 'logout', icon: renderIcon(LogOutOutline) })
  return options
})

function handleUserMenuSelect(key: string) {
  const routes: Record<string, string> = {
    profile: '/home/profile',
    favorites: '/home/my/favorites',
    orders: '/home/my/orders',
    admin: '/admin'
  }
  if (key === 'logout') {
    userStore.logout()
    message.success('已退出登录')
    router.push('/login')
  } else if (routes[key]) {
    router.push(routes[key])
  }
}

async function fetchUnreadCount() {
  if (!userStore.isLoggedIn) return
  try {
    const res = await messageApi.getUnreadCount()
    unreadCount.value = res.data
  } catch {}
}

onMounted(() => {
  fetchUnreadCount()
  setInterval(fetchUnreadCount, 30000)
})
</script>

<style scoped>
.main-layout { min-height: 100vh; }

.header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 16px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.logo-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: linear-gradient(135deg, #18a058 0%, #36d399 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
  color: #1a1a1a;
}

.nav-menu {
  display: flex;
  gap: 8px;
  margin-left: 48px;
}

.nav-item {
  padding: 8px 16px;
  font-size: 15px;
  color: #666;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.2s;
}

.nav-item:hover { color: #18a058; background: #f0fdf4; }
.nav-item.active { color: #18a058; background: #f0fdf4; font-weight: 500; }

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px 6px 6px;
  border-radius: 24px;
  cursor: pointer;
  transition: background 0.2s;
}

.user-trigger:hover { background: #f5f5f5; }

.username {
  font-size: 14px;
  color: #333;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.content {
  min-height: calc(100vh - 180px);
  background: #f8f9fa;
}

.footer {
  background: #fff;
  border-top: 1px solid #f0f0f0;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px 16px;
  text-align: center;
}

.footer-brand {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 8px;
}

.footer-slogan {
  margin: 0 0 16px;
  font-size: 14px;
  color: #666;
}

.footer-copyright {
  margin: 0;
  font-size: 13px;
  color: #999;
}

@media (max-width: 768px) {
  .nav-menu { display: none; }
  .logo-text { display: none; }
  .username { display: none; }
}
</style>
