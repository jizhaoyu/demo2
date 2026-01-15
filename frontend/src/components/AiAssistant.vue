<template>
  <div class="ai-assistant">
    <!-- 悬浮按钮 -->
    <div class="ai-fab" :class="{ active: isOpen }" @click="toggleChat">
      <n-icon v-if="!isOpen" size="28"><SparklesOutline /></n-icon>
      <n-icon v-else size="24"><CloseOutline /></n-icon>
    </div>
    
    <!-- 聊天窗口 -->
    <Transition name="slide-up">
      <div v-if="isOpen" class="ai-chat-window">
        <div class="ai-header">
          <div class="ai-title">
            <n-icon size="20" color="#fff"><SparklesOutline /></n-icon>
            <span>智能小书</span>
          </div>
          <div class="header-actions">
            <n-tooltip trigger="hover">
              <template #trigger>
                <n-button text size="small" class="header-btn" @click="clearHistory">
                  <template #icon><n-icon><TrashOutline /></n-icon></template>
                </n-button>
              </template>
              清空对话
            </n-tooltip>
          </div>
        </div>
        
        <div ref="messagesRef" class="ai-messages">
          <div v-for="(msg, index) in messages" :key="index" class="ai-message" :class="msg.role">
            <div class="message-avatar">
              <n-icon v-if="msg.role === 'assistant'" size="18" color="#18a058"><SparklesOutline /></n-icon>
              <n-icon v-else size="18" color="#666"><PersonOutline /></n-icon>
            </div>
            <div class="message-content">
              <div class="message-text" v-html="formatMessage(msg.content)"></div>
            </div>
          </div>
          
          <div v-if="isTyping" class="ai-message assistant">
            <div class="message-avatar">
              <n-icon size="18" color="#18a058"><SparklesOutline /></n-icon>
            </div>
            <div class="message-content">
              <div class="typing-indicator">
                <span></span><span></span><span></span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="ai-quick-actions">
          <n-button v-for="q in quickQuestions" :key="q" size="tiny" quaternary @click="askQuestion(q)">
            {{ q }}
          </n-button>
        </div>
        
        <div class="ai-input">
          <n-input
            v-model:value="inputText"
            placeholder="问我任何问题..."
            :disabled="isTyping"
            @keydown.enter.prevent="sendMessage"
          />
          <n-button type="primary" :disabled="!inputText.trim() || isTyping" @click="sendMessage">
            <template #icon><n-icon><SendOutline /></n-icon></template>
          </n-button>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { SparklesOutline, CloseOutline, SendOutline, PersonOutline, TrashOutline } from '@vicons/ionicons5'
import { aiApi, type ChatMessage } from '@/service/ai'

const route = useRoute()
const isOpen = ref(false)
const inputText = ref('')
const isTyping = ref(false)
const messagesRef = ref<HTMLElement | null>(null)
const sessionId = ref<string>('')

const messages = ref<ChatMessage[]>([
  { 
    role: 'assistant', 
    content: '你好！我是小书，二手书交易平台的智能助手 📚\n\n我可以帮你：\n• 了解如何购买/出售书籍\n• 解答平台使用问题\n• 推荐适合的书籍\n\n有什么可以帮助你的吗？' 
  }
])

const quickQuestions = [
  '如何发布书籍？',
  '怎么联系卖家？',
  '如何下单购买？',
  '平台收费吗？'
]

// 从localStorage恢复会话
onMounted(() => {
  const savedSession = localStorage.getItem('ai_session')
  if (savedSession) {
    try {
      const data = JSON.parse(savedSession)
      if (data.sessionId) sessionId.value = data.sessionId
      if (data.messages && data.messages.length > 0) {
        messages.value = data.messages
      }
    } catch (e) {
      // ignore
    }
  }
})

// 保存会话到localStorage
function saveSession() {
  localStorage.setItem('ai_session', JSON.stringify({
    sessionId: sessionId.value,
    messages: messages.value.slice(-20) // 只保存最近20条
  }))
}

function toggleChat() {
  isOpen.value = !isOpen.value
  if (isOpen.value) {
    scrollToBottom()
  }
}

function scrollToBottom() {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight
    }
  })
}

function formatMessage(text: string) {
  return text
    .replace(/\n/g, '<br>')
    .replace(/•/g, '&bull;')
}

async function sendMessage() {
  if (!inputText.value.trim() || isTyping.value) return
  
  const userMessage = inputText.value.trim()
  
  // 添加用户消息
  messages.value.push({ role: 'user', content: userMessage })
  inputText.value = ''
  scrollToBottom()
  
  // 显示打字动画
  isTyping.value = true
  
  try {
    // 调用后端AI API
    const res = await aiApi.chat({
      message: userMessage,
      sessionId: sessionId.value || undefined,
      history: messages.value.slice(-10), // 发送最近10条作为上下文
      currentPage: route.path
    })
    
    if (res.data.success) {
      // 更新sessionId
      if (res.data.sessionId) {
        sessionId.value = res.data.sessionId
      }
      
      // 添加AI回复
      messages.value.push({ role: 'assistant', content: res.data.reply })
    } else {
      messages.value.push({ 
        role: 'assistant', 
        content: res.data.error || '抱歉，我暂时无法回答，请稍后再试。' 
      })
    }
  } catch (e) {
    // 网络错误时使用本地回复
    messages.value.push({ 
      role: 'assistant', 
      content: getLocalResponse(userMessage)
    })
  } finally {
    isTyping.value = false
    scrollToBottom()
    saveSession()
  }
}

// 本地降级回复
function getLocalResponse(message: string): string {
  const msg = message.toLowerCase()
  
  if (msg.includes('发布') || msg.includes('卖书')) {
    return '发布书籍很简单！📚\n\n1. 点击顶部导航的「发布书籍」\n2. 填写书名、作者、价格等信息\n3. 上传书籍图片\n4. 选择分类和成色\n5. 点击发布即可\n\n💡 详细的描述和清晰的图片能让书更快卖出去哦！'
  } else if (msg.includes('购买') || msg.includes('买书') || msg.includes('下单')) {
    return '购买书籍的步骤：📖\n\n1. 浏览或搜索想要的书籍\n2. 点击查看详情\n3. 确认后点击「立即购买」\n4. 填写收货地址\n5. 确认订单\n\n📦 卖家发货后记得及时确认收货哦！'
  } else if (msg.includes('联系') || msg.includes('卖家') || msg.includes('聊天')) {
    return '联系卖家的方法：💬\n\n1. 进入书籍详情页\n2. 点击「联系卖家」按钮\n3. 在聊天窗口发送消息\n\n你也可以在「消息中心」查看所有对话记录。'
  } else if (msg.includes('收费') || msg.includes('费用') || msg.includes('免费')) {
    return '平台目前完全免费！🎉\n\n• 发布书籍：免费\n• 浏览购买：免费\n• 消息沟通：免费\n\n我们致力于让闲置书籍流动起来！'
  } else {
    return '我是小书，平台的智能助手 😊\n\n我可以帮你：\n• 了解如何买卖书籍\n• 解答平台使用问题\n• 推荐适合的书籍\n\n请问有什么可以帮助你的？'
  }
}

function askQuestion(question: string) {
  inputText.value = question
  sendMessage()
}

function clearHistory() {
  messages.value = [
    { role: 'assistant', content: '对话已清空，有什么可以帮助你的吗？ 😊' }
  ]
  sessionId.value = ''
  localStorage.removeItem('ai_session')
}

// 监听路由变化，可以给AI提供上下文
watch(() => route.path, () => {
  // 路由变化时不做特殊处理，但API调用时会带上当前路径
})
</script>

<style scoped>
.ai-assistant {
  position: fixed;
  bottom: 24px;
  right: 24px;
  z-index: 1000;
}

/* 悬浮按钮 */
.ai-fab {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #18a058 0%, #36ad6a 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(24, 160, 88, 0.4);
  transition: all 0.3s ease;
}

.ai-fab:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 16px rgba(24, 160, 88, 0.5);
}

.ai-fab.active {
  background: #666;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

/* 聊天窗口 */
.ai-chat-window {
  position: absolute;
  bottom: 70px;
  right: 0;
  width: 380px;
  height: 520px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.ai-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  background: linear-gradient(135deg, #18a058 0%, #36ad6a 100%);
  color: #fff;
}

.ai-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  font-size: 15px;
}

.header-actions {
  display: flex;
  gap: 4px;
}

.header-btn {
  color: rgba(255, 255, 255, 0.8) !important;
}

.header-btn:hover {
  color: #fff !important;
}

/* 消息区域 */
.ai-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: #f9fafb;
}

.ai-message {
  display: flex;
  gap: 10px;
  margin-bottom: 14px;
}

.ai-message.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.message-content {
  max-width: 75%;
}

.message-text {
  padding: 10px 14px;
  border-radius: 16px;
  font-size: 13px;
  line-height: 1.6;
  background: #fff;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.ai-message.user .message-text {
  background: linear-gradient(135deg, #18a058 0%, #36ad6a 100%);
  color: #fff;
  border-bottom-right-radius: 4px;
}

.ai-message.assistant .message-text {
  border-bottom-left-radius: 4px;
}

/* 打字指示器 */
.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 14px 18px;
  background: #fff;
  border-radius: 16px;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  background: #ccc;
  border-radius: 50%;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-indicator span:nth-child(2) { animation-delay: 0.2s; }
.typing-indicator span:nth-child(3) { animation-delay: 0.4s; }

@keyframes typing {
  0%, 60%, 100% { transform: translateY(0); background: #ccc; }
  30% { transform: translateY(-4px); background: #18a058; }
}

/* 快捷问题 */
.ai-quick-actions {
  padding: 10px 12px;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  border-top: 1px solid #f0f0f0;
  background: #fff;
}

.ai-quick-actions .n-button {
  font-size: 12px;
  border-radius: 14px;
  background: #f0f7f4;
  color: #18a058;
}

.ai-quick-actions .n-button:hover {
  background: #e0f0e8;
}

/* 输入区域 */
.ai-input {
  display: flex;
  gap: 8px;
  padding: 12px;
  border-top: 1px solid #f0f0f0;
  background: #fff;
}

.ai-input .n-input {
  flex: 1;
}

.ai-input .n-button {
  border-radius: 10px;
}

/* 动画 */
.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.3s ease;
}

.slide-up-enter-from,
.slide-up-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

/* 滚动条 */
.ai-messages::-webkit-scrollbar {
  width: 4px;
}

.ai-messages::-webkit-scrollbar-thumb {
  background: #ddd;
  border-radius: 2px;
}

/* 响应式 */
@media (max-width: 480px) {
  .ai-assistant {
    bottom: 16px;
    right: 16px;
  }
  
  .ai-chat-window {
    width: calc(100vw - 32px);
    height: 65vh;
    right: -8px;
  }
}
</style>
