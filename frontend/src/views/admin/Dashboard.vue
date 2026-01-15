<template>
  <div class="dashboard-page">
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <div class="welcome-content">
        <h1>管理后台</h1>
        <p>欢迎回来，管理员！这里是系统数据概览。</p>
      </div>
      <div class="welcome-time">
        {{ currentTime }}
      </div>
    </div>
    
    <!-- 统计卡片 -->
    <n-spin :show="loading">
      <div class="stats-grid">
        <div class="stat-card users">
          <div class="stat-icon">
            <n-icon size="32"><PeopleOutline /></n-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.userCount }}</span>
            <span class="stat-label">注册用户</span>
          </div>
          <div class="stat-trend">
            <n-icon><TrendingUpOutline /></n-icon>
          </div>
        </div>
        
        <div class="stat-card books">
          <div class="stat-icon">
            <n-icon size="32"><BookOutline /></n-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.bookCount }}</span>
            <span class="stat-label">书籍总数</span>
          </div>
          <div class="stat-trend">
            <n-icon><TrendingUpOutline /></n-icon>
          </div>
        </div>
        
        <div class="stat-card orders">
          <div class="stat-icon">
            <n-icon size="32"><CartOutline /></n-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.orderCount }}</span>
            <span class="stat-label">订单总数</span>
          </div>
          <div class="stat-trend">
            <n-icon><TrendingUpOutline /></n-icon>
          </div>
        </div>
        
        <div class="stat-card completed">
          <div class="stat-icon">
            <n-icon size="32"><CheckmarkCircleOutline /></n-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.completedOrderCount }}</span>
            <span class="stat-label">已完成订单</span>
          </div>
          <div class="stat-trend success">
            <n-icon><CheckmarkOutline /></n-icon>
          </div>
        </div>
      </div>
    </n-spin>
    
    <!-- 快捷操作 -->
    <n-card class="quick-actions" title="快捷操作">
      <template #header-extra>
        <n-icon color="#18a058"><FlashOutline /></n-icon>
      </template>
      <div class="action-grid">
        <div class="action-item" @click="router.push('/admin/users')">
          <div class="action-icon users">
            <n-icon size="28"><PeopleOutline /></n-icon>
          </div>
          <span>用户管理</span>
        </div>
        <div class="action-item" @click="router.push('/admin/categories')">
          <div class="action-icon categories">
            <n-icon size="28"><GridOutline /></n-icon>
          </div>
          <span>分类管理</span>
        </div>
        <div class="action-item" @click="router.push('/home')">
          <div class="action-icon home">
            <n-icon size="28"><HomeOutline /></n-icon>
          </div>
          <span>返回前台</span>
        </div>
      </div>
    </n-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { PeopleOutline, BookOutline, CartOutline, CheckmarkCircleOutline, TrendingUpOutline,
         CheckmarkOutline, FlashOutline, GridOutline, HomeOutline } from '@vicons/ionicons5'
import { adminApi } from '@/service/admin'

const router = useRouter()
const loading = ref(false)
const currentTime = ref('')
const stats = reactive({ userCount: 0, bookCount: 0, orderCount: 0, completedOrderCount: 0 })

let timer: number

function updateTime() {
  const now = new Date()
  currentTime.value = now.toLocaleString('zh-CN', {
    year: 'numeric', month: 'long', day: 'numeric',
    weekday: 'long', hour: '2-digit', minute: '2-digit'
  })
}

async function loadStats() {
  loading.value = true
  try {
    const res = await adminApi.getStatistics()
    Object.assign(stats, res.data)
  } catch {} finally { loading.value = false }
}

onMounted(() => {
  updateTime()
  timer = window.setInterval(updateTime, 1000)
  loadStats()
})

onUnmounted(() => { clearInterval(timer) })
</script>

<style scoped>
.dashboard-page { padding: 24px; }

.welcome-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32px;
  background: linear-gradient(135deg, #18a058 0%, #2080f0 100%);
  border-radius: 16px;
  color: #fff;
  margin-bottom: 24px;
}

.welcome-content h1 { margin: 0 0 8px; font-size: 28px; font-weight: 700; }
.welcome-content p { margin: 0; opacity: 0.9; }
.welcome-time { font-size: 14px; opacity: 0.9; }

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
}

.stat-card.users::before { background: #18a058; }
.stat-card.books::before { background: #2080f0; }
.stat-card.orders::before { background: #f0a020; }
.stat-card.completed::before { background: #18a058; }

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.stat-card.users .stat-icon { background: linear-gradient(135deg, #18a058, #36d399); }
.stat-card.books .stat-icon { background: linear-gradient(135deg, #2080f0, #63b3ed); }
.stat-card.orders .stat-icon { background: linear-gradient(135deg, #f0a020, #fbd38d); }
.stat-card.completed .stat-icon { background: linear-gradient(135deg, #18a058, #36d399); }

.stat-info { flex: 1; }
.stat-value { display: block; font-size: 32px; font-weight: 700; color: #1a1a1a; }
.stat-label { font-size: 14px; color: #666; }

.stat-trend { color: #18a058; opacity: 0.6; }

.quick-actions { border-radius: 16px; }

.action-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 24px;
  background: #f9f9f9;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.action-item:hover { background: #f0f0f0; transform: translateY(-2px); }

.action-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.action-icon.users { background: linear-gradient(135deg, #18a058, #36d399); }
.action-icon.categories { background: linear-gradient(135deg, #2080f0, #63b3ed); }
.action-icon.home { background: linear-gradient(135deg, #f0a020, #fbd38d); }

.action-item span { font-size: 14px; color: #333; font-weight: 500; }

@media (max-width: 1024px) {
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
}

@media (max-width: 768px) {
  .welcome-section { flex-direction: column; text-align: center; gap: 16px; }
  .stats-grid { grid-template-columns: 1fr; }
  .action-grid { grid-template-columns: 1fr; }
}
</style>
