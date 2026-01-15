<template>
  <div class="message-center-page">
    <div class="container">
      <n-card class="main-card">
        <template #header>
          <div class="card-header">
            <n-icon size="28" color="#2080f0"><ChatbubblesOutline /></n-icon>
            <div>
              <h2>消息中心</h2>
              <p>与卖家/买家的所有对话</p>
            </div>
          </div>
        </template>
        
        <n-spin :show="loading">
          <n-empty v-if="!loading && conversations.length === 0" class="empty-state" description="暂无消息">
            <template #extra>
              <n-button type="primary" @click="router.push('/home')">去发现好书</n-button>
            </template>
          </n-empty>
          
          <div v-else class="conversation-list">
            <div v-for="conv in conversations" :key="conv.userId" class="conversation-item"
                 @click="router.push(`/home/chat/${conv.userId}`)">
              <div class="conv-avatar">
                <n-badge :value="conv.unreadCount" :max="99" :show="conv.unreadCount > 0">
                  <n-avatar :size="52" :src="conv.avatar" round>
                    {{ conv.username?.charAt(0).toUpperCase() }}
                  </n-avatar>
                </n-badge>
              </div>
              <div class="conv-content">
                <div class="conv-header">
                  <span class="conv-name">{{ conv.username }}</span>
                  <span class="conv-time">{{ formatTime(conv.lastTime) }}</span>
                </div>
                <div class="conv-message">
                  <n-ellipsis :line-clamp="1">{{ conv.lastMessage || '暂无消息' }}</n-ellipsis>
                </div>
              </div>
              <n-icon size="20" color="#ccc"><ChevronForwardOutline /></n-icon>
            </div>
          </div>
        </n-spin>
      </n-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ChatbubblesOutline, ChevronForwardOutline } from '@vicons/ionicons5'
import { messageApi } from '@/service/message'
import type { Conversation } from '@/types'

const router = useRouter()

const loading = ref(false)
const conversations = ref<Conversation[]>([])

function formatTime(dateStr?: string) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate())
  const yesterday = new Date(today.getTime() - 86400000)
  
  if (date >= today) return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  if (date >= yesterday) return '昨天'
  return date.toLocaleDateString('zh-CN', { month: 'numeric', day: 'numeric' })
}

async function loadConversations() {
  loading.value = true
  try {
    const res = await messageApi.getConversations()
    conversations.value = res.data || []
  } catch {} finally { loading.value = false }
}

onMounted(loadConversations)
</script>

<style scoped>
.message-center-page { padding: 24px 0; background: #f8f9fa; min-height: calc(100vh - 120px); }

.container { max-width: 700px; margin: 0 auto; padding: 0 16px; }

.main-card { border-radius: 16px; }

.card-header { display: flex; align-items: center; gap: 16px; }
.card-header h2 { margin: 0 0 4px; font-size: 20px; font-weight: 600; }
.card-header p { margin: 0; color: #666; font-size: 14px; }

.empty-state { padding: 60px 0; }

.conversation-list { display: flex; flex-direction: column; }

.conversation-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  cursor: pointer;
  transition: background 0.2s;
  border-bottom: 1px solid #f0f0f0;
}

.conversation-item:last-child { border-bottom: none; }
.conversation-item:hover { background: #fafafa; }

.conv-content { flex: 1; min-width: 0; }

.conv-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.conv-name { font-size: 16px; font-weight: 600; color: #1a1a1a; }
.conv-time { font-size: 12px; color: #999; }

.conv-message { font-size: 14px; color: #666; }
</style>
