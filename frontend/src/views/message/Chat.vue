<template>
  <div class="chat-page">
    <div class="chat-container">
      <!-- 聊天头部 -->
      <div class="chat-header">
        <n-button text class="back-btn" @click="router.push('/home/messages')">
          <template #icon><n-icon size="24"><ArrowBackOutline /></n-icon></template>
        </n-button>
        <div class="user-info" @click="router.push(`/home/user/${targetUser?.id}`)">
          <n-avatar :size="40" :src="targetUser?.avatar" round>
            {{ targetUser?.username?.charAt(0) }}
          </n-avatar>
          <div class="user-detail">
            <span class="username">{{ targetUser?.username || '加载中...' }}</span>
            <span class="status">在线</span>
          </div>
        </div>
      </div>
      
      <!-- 消息列表 -->
      <div ref="messageListRef" class="message-list">
        <n-spin :show="loading" class="loading-spin">
          <n-empty v-if="!loading && messages.length === 0" description="暂无消息，发送第一条消息开始聊天吧~" />
          
          <template v-for="(msg, index) in messages" :key="msg.id">
            <!-- 时间分割线 -->
            <div v-if="showTimeDivider(index)" class="time-divider">
              <span>{{ formatDate(msg.createTime) }}</span>
            </div>
            
            <!-- 消息项 -->
            <div class="message-item" :class="{ mine: msg.senderId === userStore.userId }">
              <n-avatar 
                v-if="msg.senderId !== userStore.userId"
                :size="36" 
                :src="targetUser?.avatar" 
                round
                class="avatar"
              >
                {{ targetUser?.username?.charAt(0) }}
              </n-avatar>
              
              <div class="message-body">
                <!-- 书籍卡片消息 -->
                <div v-if="isBookMessage(msg.content)" class="book-card-bubble" @click="goToBook(msg.content)">
                  <div class="book-card-content">
                    <img :src="parseBookMessage(msg.content).image" class="book-card-img" />
                    <div class="book-card-info">
                      <span class="book-card-title">{{ parseBookMessage(msg.content).title }}</span>
                      <span class="book-card-price">¥{{ parseBookMessage(msg.content).price }}</span>
                    </div>
                    <n-icon size="16" color="#999"><ChevronForwardOutline /></n-icon>
                  </div>
                </div>
                <!-- 普通文本消息 -->
                <div v-else class="message-bubble">
                  <span class="message-text">{{ msg.content }}</span>
                </div>
                <span class="message-time">{{ formatTime(msg.createTime) }}</span>
              </div>
              
              <n-avatar 
                v-if="msg.senderId === userStore.userId"
                :size="36" 
                :src="userStore.user?.avatar" 
                round
                class="avatar"
              >
                {{ userStore.username?.charAt(0) }}
              </n-avatar>
            </div>
          </template>
        </n-spin>
      </div>
      
      <!-- 书籍卡片预览（从书籍详情页跳转过来时显示） -->
      <div v-if="pendingBook" class="pending-book-card">
        <div class="pending-book-content">
          <img :src="pendingBook.image" class="pending-book-img" />
          <div class="pending-book-info">
            <span class="pending-book-title">{{ pendingBook.title }}</span>
            <span class="pending-book-price">¥{{ pendingBook.price }}</span>
          </div>
          <n-button size="small" type="primary" @click="sendBookCard">
            <template #icon><n-icon><SendOutline /></n-icon></template>
            发送
          </n-button>
          <n-button size="small" quaternary @click="pendingBook = null">
            <template #icon><n-icon><CloseOutline /></n-icon></template>
          </n-button>
        </div>
      </div>
      
      <!-- 输入区域 -->
      <div class="chat-footer">
        <div class="input-wrapper">
          <n-input
            v-model:value="inputMessage"
            type="textarea"
            placeholder="输入消息..."
            :autosize="{ minRows: 1, maxRows: 4 }"
            @keydown.enter.exact.prevent="handleSend"
          />
        </div>
        <n-button 
          type="primary" 
          class="send-btn"
          :loading="sending" 
          :disabled="!inputMessage.trim()" 
          @click="handleSend"
        >
          <template #icon><n-icon><SendOutline /></n-icon></template>
          发送
        </n-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, watch, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useMessage } from 'naive-ui'
import { ArrowBackOutline, SendOutline, CloseOutline, ChevronForwardOutline } from '@vicons/ionicons5'
import { useUserStore } from '@/store/user'
import { userApi } from '@/service/user'
import { messageApi } from '@/service/message'
import type { User, Message } from '@/types'

const route = useRoute()
const router = useRouter()
const message = useMessage()
const userStore = useUserStore()

const loading = ref(false)
const sending = ref(false)
const targetUser = ref<User | null>(null)
const messages = ref<Message[]>([])
const inputMessage = ref('')
const messageListRef = ref<HTMLElement | null>(null)
let refreshTimer: number | null = null

// 待发送的书籍卡片信息
interface PendingBook {
  id: number
  title: string
  price: number
  image: string
}
const pendingBook = ref<PendingBook | null>(null)

// 书籍消息格式：[BOOK:id:title:price:image]
const BOOK_MSG_PREFIX = '[BOOK:'
const BOOK_MSG_SUFFIX = ']'

// 判断是否为书籍卡片消息
function isBookMessage(content: string): boolean {
  return content.startsWith(BOOK_MSG_PREFIX) && content.endsWith(BOOK_MSG_SUFFIX)
}

// 解析书籍卡片消息
function parseBookMessage(content: string): PendingBook {
  try {
    const data = content.slice(BOOK_MSG_PREFIX.length, -BOOK_MSG_SUFFIX.length)
    const [id, title, price, image] = data.split('|')
    return { id: Number(id), title, price: Number(price), image }
  } catch {
    return { id: 0, title: '未知书籍', price: 0, image: '' }
  }
}

// 跳转到书籍详情
function goToBook(content: string) {
  const book = parseBookMessage(content)
  if (book.id) {
    router.push(`/home/book/${book.id}`)
  }
}

// 发送书籍卡片
async function sendBookCard() {
  if (!pendingBook.value) return
  const userId = Number(route.params.userId)
  if (!userId) return
  
  const bookMsg = `${BOOK_MSG_PREFIX}${pendingBook.value.id}|${pendingBook.value.title}|${pendingBook.value.price}|${pendingBook.value.image}${BOOK_MSG_SUFFIX}`
  
  sending.value = true
  try {
    await messageApi.send({ receiverId: userId, content: bookMsg })
    pendingBook.value = null
    await loadMessages(false)
  } catch (e: any) {
    if (e?.message) message.error(e.message)
  } finally {
    sending.value = false
  }
}

// 格式化时间（仅显示时:分）
function formatTime(dateStr?: string) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

// 格式化日期（用于时间分割线）
function formatDate(dateStr?: string) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const today = new Date()
  const yesterday = new Date(today)
  yesterday.setDate(yesterday.getDate() - 1)
  
  if (date.toDateString() === today.toDateString()) {
    return '今天'
  } else if (date.toDateString() === yesterday.toDateString()) {
    return '昨天'
  } else {
    return date.toLocaleDateString('zh-CN', { month: 'long', day: 'numeric' })
  }
}

// 判断是否显示时间分割线（间隔超过5分钟）
function showTimeDivider(index: number) {
  if (index === 0) return true
  const current = new Date(messages.value[index].createTime).getTime()
  const prev = new Date(messages.value[index - 1].createTime).getTime()
  return current - prev > 5 * 60 * 1000
}

// 滚动到底部
function scrollToBottom(smooth = true) {
  nextTick(() => {
    if (messageListRef.value) {
      messageListRef.value.scrollTo({
        top: messageListRef.value.scrollHeight,
        behavior: smooth ? 'smooth' : 'auto'
      })
    }
  })
}

// 加载对方用户信息
async function loadTargetUser() {
  const userId = Number(route.params.userId)
  if (!userId || isNaN(userId)) {
    message.error('用户ID无效')
    return
  }
  try {
    const res = await userApi.getUserById(userId)
    targetUser.value = res.data
  } catch (e) {
    message.error('获取用户信息失败')
  }
}

// 加载消息历史
async function loadMessages(showLoading = true) {
  const userId = Number(route.params.userId)
  if (!userId || isNaN(userId)) return
  
  if (showLoading) loading.value = true
  try {
    const res = await messageApi.getHistory(userId)
    const newMessages = res.data?.records || []
    
    // 检查是否有新消息
    const hasNewMessages = newMessages.length !== messages.value.length
    messages.value = newMessages
    
    if (hasNewMessages) {
      scrollToBottom(!showLoading)
    }
    
    // 标记已读
    await messageApi.markRead(userId)
  } catch (e) {
    // ignore
  } finally {
    loading.value = false
  }
}

// 发送消息
async function handleSend() {
  const userId = Number(route.params.userId)
  if (!userId || !inputMessage.value.trim()) return
  
  sending.value = true
  try {
    await messageApi.send({ receiverId: userId, content: inputMessage.value.trim() })
    inputMessage.value = ''
    await loadMessages(false)
  } catch (e: any) {
    if (e?.message) message.error(e.message)
  } finally {
    sending.value = false
  }
}

// 初始化书籍卡片（从路由参数获取）
function initPendingBook() {
  const { bookId, bookTitle, bookPrice, bookImage } = route.query
  if (bookId && bookTitle) {
    pendingBook.value = {
      id: Number(bookId),
      title: String(bookTitle),
      price: Number(bookPrice) || 0,
      image: String(bookImage || '')
    }
  }
}

// 监听路由变化
watch(() => route.params.userId, (newId) => {
  if (newId) {
    loadTargetUser()
    loadMessages()
    initPendingBook()
  }
})

onMounted(() => {
  loadTargetUser()
  loadMessages()
  initPendingBook()
  // 每5秒刷新一次消息
  refreshTimer = window.setInterval(() => loadMessages(false), 5000)
})

onUnmounted(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
  }
})
</script>

<style scoped>
.chat-page {
  height: calc(100vh - 60px);
  background: #f5f7f9;
  display: flex;
  justify-content: center;
  padding: 16px;
}

.chat-container {
  width: 100%;
  max-width: 800px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 头部样式 */
.chat-header {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
  background: #fff;
}

.back-btn {
  margin-right: 8px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 8px;
  transition: background 0.2s;
}

.user-info:hover {
  background: #f5f5f5;
}

.user-detail {
  display: flex;
  flex-direction: column;
}

.username {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.status {
  font-size: 12px;
  color: #18a058;
}

/* 消息列表 */
.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: #f9fafb;
}

.loading-spin {
  min-height: 200px;
}

/* 时间分割线 */
.time-divider {
  display: flex;
  justify-content: center;
  margin: 16px 0;
}

.time-divider span {
  background: #e8e8e8;
  color: #999;
  font-size: 12px;
  padding: 4px 12px;
  border-radius: 12px;
}

/* 消息项 */
.message-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  margin-bottom: 16px;
}

.message-item.mine {
  flex-direction: row-reverse;
}

.message-item .avatar {
  flex-shrink: 0;
}

.message-body {
  display: flex;
  flex-direction: column;
  max-width: 70%;
}

.message-item.mine .message-body {
  align-items: flex-end;
}

.message-bubble {
  padding: 10px 14px;
  border-radius: 16px;
  background: #fff;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  word-break: break-word;
}

.message-item.mine .message-bubble {
  background: linear-gradient(135deg, #18a058 0%, #36ad6a 100%);
  color: #fff;
  border-bottom-right-radius: 4px;
}

.message-item:not(.mine) .message-bubble {
  border-bottom-left-radius: 4px;
}

.message-text {
  font-size: 14px;
  line-height: 1.5;
  white-space: pre-wrap;
}

.message-time {
  font-size: 11px;
  color: #999;
  margin-top: 4px;
  padding: 0 4px;
}

/* 书籍卡片消息样式 */
.book-card-bubble {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.2s;
  overflow: hidden;
}

.book-card-bubble:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
  transform: translateY(-1px);
}

.message-item.mine .book-card-bubble {
  background: linear-gradient(135deg, #e8f5e9 0%, #f1f8e9 100%);
}

.book-card-content {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
}

.book-card-img {
  width: 50px;
  height: 60px;
  object-fit: cover;
  border-radius: 6px;
  background: #f5f5f5;
}

.book-card-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.book-card-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.book-card-price {
  font-size: 16px;
  font-weight: 600;
  color: #f5222d;
}

/* 待发送书籍卡片样式 */
.pending-book-card {
  padding: 12px 16px;
  background: #f0f9eb;
  border-top: 1px solid #e8f5e9;
}

.pending-book-content {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #fff;
  padding: 10px;
  border-radius: 10px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.pending-book-img {
  width: 48px;
  height: 56px;
  object-fit: cover;
  border-radius: 6px;
  background: #f5f5f5;
}

.pending-book-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.pending-book-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.pending-book-price {
  font-size: 15px;
  font-weight: 600;
  color: #f5222d;
}

/* 输入区域 */
.chat-footer {
  display: flex;
  align-items: flex-end;
  gap: 12px;
  padding: 12px 16px;
  border-top: 1px solid #f0f0f0;
  background: #fff;
}

.input-wrapper {
  flex: 1;
}

.input-wrapper :deep(.n-input) {
  border-radius: 20px;
}

.input-wrapper :deep(.n-input__textarea-el) {
  padding: 8px 16px !important;
}

.send-btn {
  border-radius: 20px;
  padding: 0 20px;
  height: 40px;
}

/* 滚动条美化 */
.message-list::-webkit-scrollbar {
  width: 6px;
}

.message-list::-webkit-scrollbar-track {
  background: transparent;
}

.message-list::-webkit-scrollbar-thumb {
  background: #ddd;
  border-radius: 3px;
}

.message-list::-webkit-scrollbar-thumb:hover {
  background: #ccc;
}

/* 响应式 */
@media (max-width: 768px) {
  .chat-page {
    padding: 0;
  }
  
  .chat-container {
    border-radius: 0;
    height: 100%;
  }
  
  .message-body {
    max-width: 80%;
  }
}
</style>
