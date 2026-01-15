<template>
  <div class="order-list-page">
    <div class="container">
      <n-card class="main-card">
        <template #header>
          <div class="card-header">
            <div class="header-left">
              <n-icon size="28" color="#2080f0"><ReceiptOutline /></n-icon>
              <div>
                <h2>我的订单</h2>
                <p>查看和管理您的所有订单</p>
              </div>
            </div>
            <n-radio-group v-model:value="orderType" @update:value="handleTypeChange">
              <n-radio-button value="buy">我买到的</n-radio-button>
              <n-radio-button value="sell">我卖出的</n-radio-button>
            </n-radio-group>
          </div>
        </template>
        
        <n-tabs v-model:value="statusFilter" type="line" animated @update:value="handleStatusChange">
          <n-tab-pane name="all" tab="全部" />
          <n-tab-pane name="pending" tab="待确认" />
          <n-tab-pane name="confirmed" tab="待付款" />
          <n-tab-pane name="paid" tab="待发货" />
          <n-tab-pane name="shipped" tab="待收货" />
          <n-tab-pane name="completed" tab="已完成" />
          <n-tab-pane name="cancelled" tab="已取消" />
        </n-tabs>
        
        <n-spin :show="loading">
          <n-empty v-if="!loading && orders.length === 0" class="empty-state" description="暂无订单">
            <template #extra>
              <n-button type="primary" @click="router.push('/home')">去逛逛</n-button>
            </template>
          </n-empty>
          
          <div v-else class="order-list">
            <div v-for="order in orders" :key="order.id" class="order-item">
              <div class="order-header">
                <div class="order-info">
                  <span class="order-no">订单号: {{ order.orderNo }}</span>
                  <span class="order-time">{{ formatDate(order.createTime) }}</span>
                </div>
                <n-tag :type="getStatusType(order.status)" size="small">{{ order.statusDesc }}</n-tag>
              </div>
              
              <div class="order-content" @click="router.push(`/home/order/${order.id}`)">
                <div class="book-cover">
                  <img :src="getBookImage(order.bookImages)" :alt="order.bookTitle" />
                </div>
                <div class="book-info">
                  <h3>{{ order.bookTitle }}</h3>
                  <p class="book-author">{{ order.bookAuthor }}</p>
                </div>
                <div class="order-price">
                  <span class="price-label">订单金额</span>
                  <span class="price-value">¥{{ order.price.toFixed(2) }}</span>
                </div>
                <div class="order-user">
                  <n-avatar :size="36" round :src="orderType === 'buy' ? order.sellerAvatar : order.buyerAvatar">
                    {{ (orderType === 'buy' ? order.sellerName : order.buyerName)?.charAt(0) }}
                  </n-avatar>
                  <div class="user-info">
                    <span class="user-label">{{ orderType === 'buy' ? '卖家' : '买家' }}</span>
                    <span class="user-name">{{ orderType === 'buy' ? order.sellerName : order.buyerName }}</span>
                  </div>
                </div>
              </div>
              
              <div class="order-actions">
                <template v-if="orderType === 'sell'">
                  <n-button v-if="order.status === 1" type="primary" size="small" @click="handleConfirm(order)">
                    确认订单
                  </n-button>
                  <n-button v-if="order.status === 3" type="primary" size="small" @click="handleShip(order)">
                    发货
                  </n-button>
                </template>
                <template v-else>
                  <n-button v-if="order.status === 2" type="primary" size="small" @click="showPayModal(order)">
                    <template #icon><n-icon><WalletOutline /></n-icon></template>
                    付款
                  </n-button>
                  <n-button v-if="order.status === 4" type="primary" size="small" @click="handleReceive(order)">
                    确认收货
                  </n-button>
                  <n-button v-if="order.status === 5 && !order.hasReviewed" type="info" size="small"
                            @click="showReviewModal(order)">
                    评价
                  </n-button>
                </template>
                <n-button v-if="[1, 2].includes(order.status)" size="small" @click="handleCancel(order)">
                  取消
                </n-button>
                <n-button size="small" quaternary @click="router.push(`/home/order/${order.id}`)">
                  详情
                </n-button>
              </div>
            </div>
          </div>
          
          <div v-if="total > pageSize" class="pagination">
            <n-pagination v-model:page="currentPage" :page-size="pageSize" :item-count="total"
                          @update:page="loadOrders" />
          </div>
        </n-spin>
      </n-card>
    </div>
    
    <!-- 评价弹窗 -->
    <n-modal v-model:show="reviewModalVisible" preset="card" title="评价订单" style="width: 480px">
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
          <n-button type="primary" :loading="submittingReview" @click="handleSubmitReview">
            提交评价
          </n-button>
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
          <p class="pay-amount">¥{{ payingOrder?.price.toFixed(2) }}</p>
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
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage, FormInst } from 'naive-ui'
import { ReceiptOutline, WalletOutline, InformationCircleOutline, CheckmarkCircleOutline } from '@vicons/ionicons5'
import { orderApi } from '@/service/order'
import { reviewApi } from '@/service/review'
import type { Order } from '@/types'

const router = useRouter()
const message = useMessage()

const loading = ref(false)
const orders = ref<Order[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const orderType = ref<'buy' | 'sell'>('buy')
const statusFilter = ref('all')

const reviewModalVisible = ref(false)
const reviewFormRef = ref<FormInst | null>(null)
const submittingReview = ref(false)
const currentOrder = ref<Order | null>(null)
const reviewForm = reactive({ rating: 5, content: '' })
const reviewRules = {
  rating: [{ required: true, type: 'number', min: 1, message: '请选择评分', trigger: 'change' }]
}
const ratingTexts = ['很差', '较差', '一般', '满意', '非常满意']

const statusMap: Record<string, number | undefined> = {
  all: undefined, pending: 1, confirmed: 2, paid: 3, shipped: 4, completed: 5, cancelled: 6
}

function getStatusType(status: number) {
  return { 1: 'warning', 2: 'info', 3: 'info', 4: 'info', 5: 'success', 6: 'error' }[status] as any || 'default'
}

function formatDate(dateStr?: string) {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

function getBookImage(images?: string) {
  if (!images) return ''
  try { return JSON.parse(images)[0] || images.split(',')[0] } catch { return images.split(',')[0] }
}

async function loadOrders() {
  loading.value = true
  try {
    const res = await orderApi.getList({
      page: currentPage.value, size: pageSize.value,
      type: orderType.value, status: statusMap[statusFilter.value]
    })
    orders.value = res.data.records || []
    total.value = res.data.total || 0
  } catch {} finally { loading.value = false }
}

function handleTypeChange() { currentPage.value = 1; loadOrders() }
function handleStatusChange() { currentPage.value = 1; loadOrders() }

async function handleConfirm(order: Order) {
  try { await orderApi.confirm(order.id); message.success('订单已确认'); loadOrders() } catch {}
}

// 模拟支付弹窗
const payModalVisible = ref(false)
const payingOrder = ref<Order | null>(null)
const paying = ref(false)

function showPayModal(order: Order) {
  payingOrder.value = order
  payModalVisible.value = true
}

async function handlePay() {
  if (!payingOrder.value) return
  paying.value = true
  try { 
    await orderApi.pay(payingOrder.value.id)
    message.success('支付成功！订单已进入待发货状态')
    payModalVisible.value = false
    loadOrders() 
  } catch {} finally { paying.value = false }
}

async function handleShip(order: Order) {
  try { await orderApi.ship(order.id); message.success('已发货'); loadOrders() } catch {}
}

async function handleReceive(order: Order) {
  try { await orderApi.receive(order.id); message.success('已确认收货'); loadOrders() } catch {}
}

async function handleCancel(order: Order) {
  try { await orderApi.cancel(order.id); message.success('订单已取消'); loadOrders() } catch {}
}

function showReviewModal(order: Order) {
  currentOrder.value = order
  reviewForm.rating = 5
  reviewForm.content = ''
  reviewModalVisible.value = true
}

async function handleSubmitReview() {
  if (!currentOrder.value) return
  try {
    await reviewFormRef.value?.validate()
    submittingReview.value = true
    await reviewApi.create({ orderId: currentOrder.value.id, rating: reviewForm.rating, content: reviewForm.content })
    message.success('评价成功')
    reviewModalVisible.value = false
    loadOrders()
  } catch {} finally { submittingReview.value = false }
}

onMounted(loadOrders)
</script>

<style scoped>
.order-list-page { padding: 24px 0; background: #f8f9fa; min-height: calc(100vh - 120px); }

.container { max-width: 1000px; margin: 0 auto; padding: 0 16px; }

.main-card { border-radius: 16px; }

.card-header { display: flex; justify-content: space-between; align-items: center; }

.header-left { display: flex; align-items: center; gap: 16px; }
.header-left h2 { margin: 0 0 4px; font-size: 20px; font-weight: 600; }
.header-left p { margin: 0; color: #666; font-size: 14px; }

.empty-state { padding: 60px 0; }

.order-list { display: flex; flex-direction: column; gap: 16px; margin-top: 16px; }

.order-item {
  background: #fafafa;
  border-radius: 12px;
  overflow: hidden;
  transition: background 0.2s;
}

.order-item:hover { background: #f5f5f5; }

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #eee;
}

.order-info { display: flex; gap: 16px; }
.order-no { font-size: 14px; color: #666; }
.order-time { font-size: 13px; color: #999; }

.order-content {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  cursor: pointer;
}

.book-cover {
  width: 80px;
  height: 100px;
  border-radius: 8px;
  overflow: hidden;
  background: #e8e8e8;
  flex-shrink: 0;
}

.book-cover img { width: 100%; height: 100%; object-fit: cover; }

.book-info { flex: 1; min-width: 0; }
.book-info h3 {
  margin: 0 0 6px;
  font-size: 16px;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.book-author { margin: 0; font-size: 14px; color: #666; }

.order-price { text-align: right; min-width: 100px; }
.price-label { display: block; font-size: 12px; color: #999; margin-bottom: 4px; }
.price-value { font-size: 20px; font-weight: 700; color: #f5222d; }

.order-user { display: flex; align-items: center; gap: 10px; min-width: 140px; }
.user-info { display: flex; flex-direction: column; }
.user-label { font-size: 12px; color: #999; }
.user-name { font-size: 14px; color: #333; }

.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding: 12px 16px;
  border-top: 1px solid #eee;
  background: #fff;
}

.pagination { margin-top: 24px; display: flex; justify-content: center; }

.rating-wrapper { display: flex; align-items: center; gap: 12px; }
.rating-text { color: #f0a020; font-size: 14px; }

.pay-modal-content { text-align: center; padding: 20px 0; }
.pay-icon { margin-bottom: 16px; }
.pay-info { margin-bottom: 24px; }
.pay-label { margin: 0 0 8px; font-size: 14px; color: #666; }
.pay-amount { margin: 0; font-size: 36px; font-weight: 700; color: #f5222d; }

@media (max-width: 768px) {
  .card-header { flex-direction: column; gap: 16px; align-items: flex-start; }
  .order-content { flex-wrap: wrap; }
  .order-price, .order-user { width: 100%; text-align: left; }
}
</style>
