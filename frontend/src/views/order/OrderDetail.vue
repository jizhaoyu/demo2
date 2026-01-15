<template>
  <div class="order-detail-page">
    <div class="container">
      <n-spin :show="loading">
        <template v-if="order">
          <!-- 订单状态卡片 -->
          <div class="status-card" :class="getStatusClass(order.status)">
            <div class="status-icon">
              <n-icon size="48"><component :is="getStatusIcon(order.status)" /></n-icon>
            </div>
            <div class="status-info">
              <h2>{{ order.statusDesc }}</h2>
              <p>{{ getStatusTip(order.status) }}</p>
            </div>
          </div>
          
          <!-- 订单信息 -->
          <n-card class="info-card" title="订单信息">
            <template #header-extra>
              <span class="order-no">订单号: {{ order.orderNo }}</span>
            </template>
            <div class="info-grid">
              <div class="info-item">
                <span class="label">创建时间</span>
                <span class="value">{{ formatDate(order.createTime) }}</span>
              </div>
              <div class="info-item">
                <span class="label">更新时间</span>
                <span class="value">{{ formatDate(order.updateTime) }}</span>
              </div>
              <div class="info-item">
                <span class="label">订单金额</span>
                <span class="value price">¥{{ order.price.toFixed(2) }}</span>
              </div>
            </div>
          </n-card>
          
          <!-- 书籍信息 -->
          <n-card class="info-card" title="书籍信息">
            <div class="book-info" @click="router.push(`/home/book/${order.bookId}`)">
              <div class="book-cover">
                <img :src="getBookImage(order.bookImages)" :alt="order.bookTitle" />
              </div>
              <div class="book-detail">
                <h3>{{ order.bookTitle }}</h3>
                <p>作者: {{ order.bookAuthor }}</p>
              </div>
              <n-icon size="20" color="#ccc"><ChevronForwardOutline /></n-icon>
            </div>
          </n-card>
          
          <!-- 交易双方 -->
          <n-card class="info-card" title="交易双方">
            <div class="users-row">
              <div class="user-box" @click="router.push(`/home/user/${order.buyerId}`)">
                <n-avatar :size="48" :src="order.buyerAvatar" round>{{ order.buyerName?.charAt(0) }}</n-avatar>
                <div class="user-info">
                  <span class="user-role">买家</span>
                  <span class="user-name">{{ order.buyerName }}</span>
                </div>
              </div>
              <div class="arrow">
                <n-icon size="24" color="#ccc"><ArrowForwardOutline /></n-icon>
              </div>
              <div class="user-box" @click="router.push(`/home/user/${order.sellerId}`)">
                <n-avatar :size="48" :src="order.sellerAvatar" round>{{ order.sellerName?.charAt(0) }}</n-avatar>
                <div class="user-info">
                  <span class="user-role">卖家</span>
                  <span class="user-name">{{ order.sellerName }}</span>
                </div>
              </div>
            </div>
          </n-card>
          
          <!-- 收货地址 -->
          <n-card v-if="order.shippingAddress" class="info-card" title="收货地址">
            <div class="address-content">
              <n-icon size="20" color="#18a058"><LocationOutline /></n-icon>
              <span>{{ order.shippingAddress }}</span>
            </div>
          </n-card>
          
          <!-- 物流信息 -->
          <n-card v-if="order.shippingInfo" class="info-card" title="物流信息">
            <div class="shipping-content">
              <n-icon size="20" color="#2080f0"><CarOutline /></n-icon>
              <span>{{ order.shippingInfo }}</span>
            </div>
          </n-card>
          
          <!-- 取消原因 -->
          <n-card v-if="order.cancelReason" class="info-card" title="取消原因">
            <p class="cancel-reason">{{ order.cancelReason }}</p>
          </n-card>
          
          <!-- 操作按钮 -->
          <div class="action-bar">
            <n-button @click="router.back()">返回</n-button>
            <n-button @click="router.push(`/home/chat/${isBuyer ? order.sellerId : order.buyerId}`)">
              <template #icon><n-icon><ChatbubbleOutline /></n-icon></template>
              联系{{ isBuyer ? '卖家' : '买家' }}
            </n-button>
            
            <template v-if="isSeller">
              <n-button v-if="order.status === 1" type="primary" @click="handleConfirm">确认订单</n-button>
              <n-button v-if="order.status === 3" type="primary" @click="showShipModal">发货</n-button>
            </template>
            
            <template v-if="isBuyer">
              <n-button v-if="order.status === 2" type="primary" @click="showPayModal">
                <template #icon><n-icon><WalletOutline /></n-icon></template>
                立即付款
              </n-button>
              <n-button v-if="order.status === 4" type="primary" @click="handleReceive">确认收货</n-button>
              <n-button v-if="order.status === 5 && !order.hasReviewed" type="info" @click="showReviewModal">
                评价
              </n-button>
            </template>
            
            <n-button v-if="[1, 2].includes(order.status)" type="error" @click="showCancelModal">
              取消订单
            </n-button>
          </div>
        </template>
      </n-spin>
    </div>
    
    <!-- 发货弹窗 -->
    <n-modal v-model:show="shipModalVisible" preset="card" title="填写物流信息" style="width: 480px">
      <n-input v-model:value="shippingInfo" type="textarea" placeholder="请输入物流信息（如快递公司、单号等）"
               :rows="3" />
      <template #footer>
        <n-space justify="end">
          <n-button @click="shipModalVisible = false">取消</n-button>
          <n-button type="primary" :loading="shipping" @click="handleShip">确认发货</n-button>
        </n-space>
      </template>
    </n-modal>
    
    <!-- 取消订单弹窗 -->
    <n-modal v-model:show="cancelModalVisible" preset="card" title="取消订单" style="width: 480px">
      <n-input v-model:value="cancelReason" type="textarea" placeholder="请输入取消原因（选填）" :rows="3" />
      <template #footer>
        <n-space justify="end">
          <n-button @click="cancelModalVisible = false">返回</n-button>
          <n-button type="error" :loading="cancelling" @click="handleCancel">确认取消</n-button>
        </n-space>
      </template>
    </n-modal>
    
    <!-- 评价弹窗 -->
    <n-modal v-model:show="reviewModalVisible" preset="card" title="评价卖家" style="width: 480px">
      <n-form ref="reviewFormRef" :model="reviewForm" :rules="reviewRules">
        <n-form-item label="评分" path="rating">
          <div class="rating-wrapper">
            <n-rate v-model:value="reviewForm.rating" size="large" />
            <span class="rating-text">{{ ratingTexts[reviewForm.rating - 1] }}</span>
          </div>
        </n-form-item>
        <n-form-item label="评价内容" path="content">
          <n-input v-model:value="reviewForm.content" type="textarea" placeholder="分享您的购买体验（选填）"
                   :rows="4" />
        </n-form-item>
      </n-form>
      <template #footer>
        <n-space justify="end">
          <n-button @click="reviewModalVisible = false">取消</n-button>
          <n-button type="primary" :loading="submittingReview" @click="handleSubmitReview">提交评价</n-button>
        </n-space>
      </template>
    </n-modal>
    
    <!-- 模拟支付弹窗 -->
    <n-modal v-model:show="payModalVisible" preset="card" title="确认支付" style="width: 420px">
      <div class="pay-modal-content">
        <div class="pay-icon">
          <n-icon size="64" color="#18a058"><WalletOutline /></n-icon>
        </div>
        <div class="pay-info">
          <p class="pay-label">支付金额</p>
          <p class="pay-amount">¥{{ order?.price.toFixed(2) }}</p>
        </div>
        <n-alert type="info" :bordered="false">
          <template #icon><n-icon><InformationCircleOutline /></n-icon></template>
          本系统为演示环境，点击"确认支付"将直接完成支付流程，无需真实付款。
        </n-alert>
      </div>
      <template #footer>
        <n-space justify="end">
          <n-button @click="payModalVisible = false">取消</n-button>
          <n-button type="primary" :loading="paying" @click="handlePay">
            <template #icon><n-icon><CheckmarkCircleOutline /></n-icon></template>
            确认支付
          </n-button>
        </n-space>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useMessage, FormInst } from 'naive-ui'
import { ChevronForwardOutline, ArrowForwardOutline, LocationOutline, CarOutline, ChatbubbleOutline,
         TimeOutline, CheckmarkCircleOutline, CloseCircleOutline, HourglassOutline, WalletOutline, InformationCircleOutline } from '@vicons/ionicons5'
import { useUserStore } from '@/store/user'
import { orderApi } from '@/service/order'
import { reviewApi } from '@/service/review'
import type { Order } from '@/types'

const route = useRoute()
const router = useRouter()
const message = useMessage()
const userStore = useUserStore()

const loading = ref(false)
const order = ref<Order | null>(null)

const shipModalVisible = ref(false)
const shippingInfo = ref('')
const shipping = ref(false)

const cancelModalVisible = ref(false)
const cancelReason = ref('')
const cancelling = ref(false)

const reviewModalVisible = ref(false)
const reviewFormRef = ref<FormInst | null>(null)
const submittingReview = ref(false)
const reviewForm = reactive({ rating: 5, content: '' })
const reviewRules = { rating: [{ required: true, type: 'number', min: 1, message: '请选择评分', trigger: 'change' }] }
const ratingTexts = ['很差', '较差', '一般', '满意', '非常满意']

const isBuyer = computed(() => order.value?.buyerId === userStore.user?.id)
const isSeller = computed(() => order.value?.sellerId === userStore.user?.id)

function getStatusClass(status: number) {
  return { 1: 'pending', 2: 'pending', 3: 'pending', 4: 'pending', 5: 'success', 6: 'cancelled' }[status] || ''
}

function getStatusIcon(status: number) {
  return { 1: HourglassOutline, 2: TimeOutline, 3: TimeOutline, 4: TimeOutline, 
           5: CheckmarkCircleOutline, 6: CloseCircleOutline }[status] || TimeOutline
}

function getStatusTip(status: number) {
  const tips: Record<number, string> = {
    1: isBuyer.value ? '等待卖家确认订单，确认后即可付款' : '买家已下单，请尽快确认订单',
    2: isBuyer.value ? '卖家已确认订单，请尽快付款（模拟支付，无需真实付款）' : '等待买家付款',
    3: isBuyer.value ? '已付款成功，等待卖家发货' : '买家已付款，请尽快发货',
    4: isBuyer.value ? '卖家已发货，请注意查收并确认收货' : '已发货，等待买家确认收货',
    5: '交易完成，感谢您的购买',
    6: '订单已取消'
  }
  return tips[status] || ''
}

function formatDate(dateStr?: string) {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

function getBookImage(images?: string) {
  if (!images) return ''
  try { return JSON.parse(images)[0] || images.split(',')[0] } catch { return images.split(',')[0] }
}

async function loadOrder() {
  const orderId = Number(route.params.id)
  if (!orderId) return
  loading.value = true
  try { order.value = (await orderApi.getDetail(orderId)).data } catch {} finally { loading.value = false }
}

async function handleConfirm() {
  if (!order.value) return
  try { await orderApi.confirm(order.value.id); message.success('订单已确认'); loadOrder() } catch {}
}

// 模拟支付弹窗
const payModalVisible = ref(false)
const paying = ref(false)

function showPayModal() {
  payModalVisible.value = true
}

async function handlePay() {
  if (!order.value) return
  paying.value = true
  try { 
    await orderApi.pay(order.value.id)
    message.success('支付成功！订单已进入待发货状态')
    payModalVisible.value = false
    loadOrder() 
  } catch {} finally { paying.value = false }
}

function showShipModal() { shippingInfo.value = ''; shipModalVisible.value = true }

async function handleShip() {
  if (!order.value) return
  shipping.value = true
  try {
    await orderApi.ship(order.value.id, shippingInfo.value)
    message.success('已发货')
    shipModalVisible.value = false
    loadOrder()
  } catch {} finally { shipping.value = false }
}

async function handleReceive() {
  if (!order.value) return
  try { await orderApi.receive(order.value.id); message.success('已确认收货'); loadOrder() } catch {}
}

function showCancelModal() { cancelReason.value = ''; cancelModalVisible.value = true }

async function handleCancel() {
  if (!order.value) return
  cancelling.value = true
  try {
    await orderApi.cancel(order.value.id, cancelReason.value)
    message.success('订单已取消')
    cancelModalVisible.value = false
    loadOrder()
  } catch {} finally { cancelling.value = false }
}

function showReviewModal() { reviewForm.rating = 5; reviewForm.content = ''; reviewModalVisible.value = true }

async function handleSubmitReview() {
  if (!order.value) return
  try {
    await reviewFormRef.value?.validate()
    submittingReview.value = true
    await reviewApi.create({ orderId: order.value.id, rating: reviewForm.rating, content: reviewForm.content })
    message.success('评价成功')
    reviewModalVisible.value = false
    loadOrder()
  } catch {} finally { submittingReview.value = false }
}

onMounted(loadOrder)
</script>

<style scoped>
.order-detail-page { padding: 24px 0; background: #f8f9fa; min-height: calc(100vh - 120px); }

.container { max-width: 800px; margin: 0 auto; padding: 0 16px; }

.status-card {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 32px;
  border-radius: 16px;
  color: #fff;
  margin-bottom: 20px;
}

.status-card.pending { background: linear-gradient(135deg, #f0a020, #fbd38d); }
.status-card.success { background: linear-gradient(135deg, #18a058, #36d399); }
.status-card.cancelled { background: linear-gradient(135deg, #909399, #c0c4cc); }

.status-icon { opacity: 0.9; }
.status-info h2 { margin: 0 0 4px; font-size: 24px; font-weight: 600; }
.status-info p { margin: 0; opacity: 0.9; }

.info-card { border-radius: 16px; margin-bottom: 16px; }

.order-no { font-size: 13px; color: #999; }

.info-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; }

.info-item { display: flex; flex-direction: column; gap: 4px; }
.info-item .label { font-size: 13px; color: #999; }
.info-item .value { font-size: 15px; color: #333; }
.info-item .value.price { font-size: 20px; font-weight: 700; color: #f5222d; }

.book-info {
  display: flex;
  align-items: center;
  gap: 16px;
  cursor: pointer;
  padding: 12px;
  margin: -12px;
  border-radius: 12px;
  transition: background 0.2s;
}

.book-info:hover { background: #f9f9f9; }

.book-cover {
  width: 80px;
  height: 100px;
  border-radius: 8px;
  overflow: hidden;
  background: #f5f5f5;
  flex-shrink: 0;
}

.book-cover img { width: 100%; height: 100%; object-fit: cover; }

.book-detail { flex: 1; }
.book-detail h3 { margin: 0 0 6px; font-size: 16px; font-weight: 600; }
.book-detail p { margin: 0; font-size: 14px; color: #666; }

.users-row { display: flex; align-items: center; justify-content: space-around; }

.user-box {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  padding: 12px;
  border-radius: 12px;
  transition: background 0.2s;
}

.user-box:hover { background: #f9f9f9; }

.user-info { display: flex; flex-direction: column; }
.user-role { font-size: 12px; color: #999; }
.user-name { font-size: 15px; font-weight: 500; color: #333; }

.arrow { padding: 0 20px; }

.address-content, .shipping-content {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  color: #333;
}

.cancel-reason { margin: 0; color: #666; }

.action-bar {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
  padding: 20px;
  background: #fff;
  border-radius: 16px;
}

.rating-wrapper { display: flex; align-items: center; gap: 12px; }
.rating-text { color: #f0a020; font-size: 14px; }

.pay-modal-content { text-align: center; padding: 20px 0; }
.pay-icon { margin-bottom: 16px; }
.pay-info { margin-bottom: 24px; }
.pay-label { margin: 0 0 8px; font-size: 14px; color: #666; }
.pay-amount { margin: 0; font-size: 36px; font-weight: 700; color: #f5222d; }

@media (max-width: 768px) {
  .info-grid { grid-template-columns: 1fr; }
  .users-row { flex-direction: column; gap: 16px; }
  .arrow { transform: rotate(90deg); padding: 10px 0; }
  .action-bar { flex-wrap: wrap; }
}
</style>
