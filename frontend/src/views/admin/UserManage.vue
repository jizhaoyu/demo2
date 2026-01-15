<template>
  <div class="user-manage-page">
    <n-card class="main-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <n-icon size="28" color="#18a058"><PeopleOutline /></n-icon>
            <div>
              <h2>用户管理</h2>
              <p>管理平台所有注册用户</p>
            </div>
          </div>
          <n-input-group style="width: 300px">
            <n-input v-model:value="keyword" placeholder="搜索用户名、邮箱、手机号" clearable
                     @keyup.enter="handleSearch">
              <template #prefix><n-icon><SearchOutline /></n-icon></template>
            </n-input>
            <n-button type="primary" @click="handleSearch">搜索</n-button>
          </n-input-group>
        </div>
      </template>
      
      <n-spin :show="loading">
        <div class="user-list">
          <div v-for="user in users" :key="user.id" class="user-item">
            <div class="user-avatar">
              <n-avatar :size="48" :src="user.avatar" round>{{ user.username?.charAt(0).toUpperCase() }}</n-avatar>
              <n-tag v-if="user.role === 1" type="warning" size="small" class="role-tag">管理员</n-tag>
            </div>
            <div class="user-info">
              <div class="user-main">
                <span class="user-name">{{ user.username }}</span>
                <n-tag :type="user.status === 1 ? 'success' : 'error'" size="small">
                  {{ user.status === 1 ? '正常' : '已禁用' }}
                </n-tag>
              </div>
              <div class="user-meta">
                <span><n-icon><MailOutline /></n-icon> {{ user.email || '-' }}</span>
                <span><n-icon><CallOutline /></n-icon> {{ user.phone || '-' }}</span>
              </div>
            </div>
            <div class="user-stats">
              <div class="stat-item">
                <span class="stat-value">{{ user.avgRating?.toFixed(1) || '-' }}</span>
                <span class="stat-label">评分</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">{{ user.ratingCount || 0 }}</span>
                <span class="stat-label">评价数</span>
              </div>
            </div>
            <div class="user-time">
              <span>注册时间</span>
              <span>{{ formatDate(user.createTime) }}</span>
            </div>
            <div class="user-actions">
              <n-button v-if="user.role !== 1" size="small" :type="user.status === 1 ? 'error' : 'success'"
                        @click="handleToggleStatus(user)">
                {{ user.status === 1 ? '禁用' : '启用' }}
              </n-button>
            </div>
          </div>
        </div>
        
        <n-empty v-if="!loading && users.length === 0" description="暂无用户数据" />
        
        <div v-if="total > pageSize" class="pagination">
          <n-pagination v-model:page="currentPage" :page-size="pageSize" :item-count="total"
                        @update:page="loadUsers" />
        </div>
      </n-spin>
    </n-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useMessage } from 'naive-ui'
import { PeopleOutline, SearchOutline, MailOutline, CallOutline } from '@vicons/ionicons5'
import { adminApi } from '@/service/admin'
import type { User } from '@/types'

const message = useMessage()
const loading = ref(false)
const users = ref<User[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const keyword = ref('')

function formatDate(dateStr?: string) {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

async function loadUsers() {
  loading.value = true
  try {
    const res = await adminApi.getUsers({
      page: currentPage.value, size: pageSize.value, keyword: keyword.value || undefined
    })
    users.value = res.data.records || []
    total.value = res.data.total || 0
  } catch {} finally { loading.value = false }
}

function handleSearch() { currentPage.value = 1; loadUsers() }

async function handleToggleStatus(user: User) {
  try {
    await adminApi.updateUserStatus(user.id, user.status === 1 ? 0 : 1)
    message.success('操作成功')
    loadUsers()
  } catch {}
}

onMounted(loadUsers)
</script>

<style scoped>
.user-manage-page { padding: 24px; }

.main-card { border-radius: 16px; }

.card-header { display: flex; justify-content: space-between; align-items: center; }

.header-left { display: flex; align-items: center; gap: 16px; }
.header-left h2 { margin: 0 0 4px; font-size: 20px; font-weight: 600; }
.header-left p { margin: 0; color: #666; font-size: 14px; }

.user-list { display: flex; flex-direction: column; gap: 12px; }

.user-item {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 16px 20px;
  background: #fafafa;
  border-radius: 12px;
  transition: background 0.2s;
}

.user-item:hover { background: #f5f5f5; }

.user-avatar { position: relative; }

.role-tag {
  position: absolute;
  bottom: -4px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 10px;
}

.user-info { flex: 1; min-width: 0; }

.user-main { display: flex; align-items: center; gap: 8px; margin-bottom: 6px; }
.user-name { font-size: 16px; font-weight: 600; color: #1a1a1a; }

.user-meta { display: flex; gap: 20px; font-size: 13px; color: #666; }
.user-meta span { display: flex; align-items: center; gap: 4px; }

.user-stats { display: flex; gap: 24px; }

.stat-item { text-align: center; }
.stat-value { display: block; font-size: 18px; font-weight: 600; color: #1a1a1a; }
.stat-label { font-size: 12px; color: #999; }

.user-time {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  font-size: 13px;
  color: #999;
  min-width: 100px;
}

.user-actions { min-width: 80px; text-align: right; }

.pagination { margin-top: 24px; display: flex; justify-content: center; }

@media (max-width: 768px) {
  .card-header { flex-direction: column; gap: 16px; align-items: flex-start; }
  .user-item { flex-wrap: wrap; }
  .user-stats, .user-time { width: 100%; justify-content: space-between; }
}
</style>
