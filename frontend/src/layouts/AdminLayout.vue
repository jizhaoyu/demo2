<template>
  <n-layout has-sider class="admin-layout">
    <n-layout-sider bordered collapse-mode="width" :collapsed-width="64" :width="200"
                    :collapsed="collapsed" show-trigger @collapse="collapsed = true" @expand="collapsed = false">
      <div class="logo" @click="router.push('/home')">
        <n-icon size="24" color="#18a058"><BookOutline /></n-icon>
        <span v-if="!collapsed" class="logo-text">管理后台</span>
      </div>
      <n-menu :collapsed="collapsed" :collapsed-width="64" :collapsed-icon-size="22"
              :options="menuOptions" :value="activeMenu" @update:value="handleMenuSelect" />
    </n-layout-sider>
    
    <n-layout>
      <n-layout-header bordered class="header">
        <div class="header-content">
          <n-breadcrumb>
            <n-breadcrumb-item @click="router.push('/')">首页</n-breadcrumb-item>
            <n-breadcrumb-item>{{ currentTitle }}</n-breadcrumb-item>
          </n-breadcrumb>
          
          <div class="header-right">
            <n-dropdown :options="userMenuOptions" @select="handleUserMenuSelect">
              <n-button quaternary>
                <n-avatar :size="28" round>{{ userStore.username?.charAt(0).toUpperCase() }}</n-avatar>
                <span class="username">{{ userStore.username }}</span>
              </n-button>
            </n-dropdown>
          </div>
        </div>
      </n-layout-header>
      
      <n-layout-content class="content">
        <router-view />
      </n-layout-content>
    </n-layout>
  </n-layout>
</template>

<script setup lang="ts">
import { ref, computed, h } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { NIcon, useMessage } from 'naive-ui'
import { BookOutline, HomeOutline, PeopleOutline, GridOutline, LogOutOutline } from '@vicons/ionicons5'
import { useUserStore } from '@/store/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const message = useMessage()

window.$message = message

const collapsed = ref(false)

const currentTitle = computed(() => {
  const titles: Record<string, string> = {
    '/admin': '数据统计',
    '/admin/users': '用户管理',
    '/admin/categories': '分类管理'
  }
  return titles[route.path] || '管理后台'
})

const activeMenu = computed(() => {
  if (route.path === '/admin') return 'dashboard'
  if (route.path === '/admin/users') return 'users'
  if (route.path === '/admin/categories') return 'categories'
  return ''
})

const renderIcon = (icon: any) => () => h(NIcon, null, { default: () => h(icon) })

const menuOptions = [
  { label: '数据统计', key: 'dashboard', icon: renderIcon(HomeOutline) },
  { label: '用户管理', key: 'users', icon: renderIcon(PeopleOutline) },
  { label: '分类管理', key: 'categories', icon: renderIcon(GridOutline) }
]

const userMenuOptions = [
  { label: '返回前台', key: 'home', icon: renderIcon(HomeOutline) },
  { type: 'divider', key: 'd1' },
  { label: '退出登录', key: 'logout', icon: renderIcon(LogOutOutline) }
]

function handleMenuSelect(key: string) {
  const routes: Record<string, string> = {
    dashboard: '/admin',
    users: '/admin/users',
    categories: '/admin/categories'
  }
  router.push(routes[key] || '/admin')
}

function handleUserMenuSelect(key: string) {
  if (key === 'home') {
    router.push('/home')
  } else if (key === 'logout') {
    userStore.logout()
    message.success('已退出登录')
    router.push('/login')
  }
}
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
  border-bottom: 1px solid #e8e8e8;
}

.logo-text {
  font-size: 16px;
  font-weight: 600;
  color: #18a058;
}

.header {
  background: #fff;
}

.header-content {
  height: 60px;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-right {
  display: flex;
  align-items: center;
}

.username {
  margin-left: 8px;
}

.content {
  padding: 24px;
  background: #f5f7f9;
  min-height: calc(100vh - 60px);
}
</style>
