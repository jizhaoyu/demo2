<template>
  <div class="order-create-page">
    <div class="container">
      <n-spin :show="loading">
        <template v-if="book">
          <!-- 页面标题 -->
          <div class="page-header">
            <n-icon size="28" color="#18a058"><CartOutline /></n-icon>
            <div>
              <h1>确认订单</h1>
              <p>请核对订单信息并填写收货地址</p>
            </div>
          </div>
          
          <!-- 书籍信息 -->
          <n-card class="info-card" title="商品信息">
            <div class="book-info">
              <div class="book-cover">
                <img :src="getBookImage(book.images)" :alt="book.title" />
              </div>
              <div class="book-detail">
                <h3>{{ book.title }}</h3>
                <p class="book-author">作者: {{ book.author }}</p>
                <p class="book-condition">成色: {{ getConditionText(book.conditionLevel) }}</p>
              </div>
              <div class="book-price">
                <span class="price-label">单价</span>
                <span class="price-value">¥{{ book.price.toFixed(2) }}</span>
              </div>
            </div>
          </n-card>
          
          <!-- 卖家信息 -->
          <n-card class="info-card" title="卖家信息">
            <div class="seller-info">
              <n-avatar :size="48" :src="book.sellerAvatar" round>{{ book.sellerName?.charAt(0) }}</n-avatar>
              <div class="seller-detail">
                <span class="seller-name">{{ book.sellerName }}</span>
                <div class="seller-rating">
                  <n-rate :value="book.sellerRating || 0" readonly size="small" />
                  <span>{{ book.sellerRating?.toFixed(1) || '暂无评分' }}</span>
                </div>
              </div>
            </div>
          </n-card>
          
          <!-- 收货地址 -->
          <n-card class="info-card" title="收货地址">
            <n-form ref="formRef" :model="form" :rules="rules">
              <n-form-item path="shippingAddress" :show-label="false">
                <n-input v-model:value="form.shippingAddress" type="textarea"
                         placeholder="请输入详细收货地址（包含省市区、街道、门牌号等）"
                         :autosize="{ minRows: 3, maxRows: 5 }" />
              </n-form-item>
            </n-form>
          </n-card>
          
          <!-- 订单金额 -->
          <n-card class="info-card summary-card">
            <div class="summary-row">
              <span>商品金额</span>
              <span>¥{{ book.price.toFixed(2) }}</span>
            </div>
            <div class="summary-row">
              <span>运费</span>
              <span class="free">包邮</span>
            </div>
            <n-divider style="margin: 16px 0" />
            <div class="summary-row total">
              <span>应付金额</span>
              <span class="total-price">¥{{ book.price.toFixed(2) }}</span>
            </div>
          </n-card>
          
          <!-- 提交按钮 -->
          <div class="action-bar">
            <div class="action-left">
              <span class="total-label">合计:</span>
              <span class="total-amount">¥{{ book.price.toFixed(2) }}</span>
            </div>
            <div class="action-right">
              <n-button size="large" @click="router.back()">返回</n-button>
              <n-button type="primary" size="large" :loading="submitting" @click="handleSubmit">
                <template #icon><n-icon><CheckmarkOutline /></n-icon></template>
                提交订单
              </n-button>
            </div>
          </div>
        </template>
        
        <n-empty v-else-if="!loading" description="书籍信息加载失败">
          <template #extra>
            <n-button type="primary" @click="router.back()">返回</n-button>
          </template>
        </n-empty>
      </n-spin>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useMessage, FormInst } from 'naive-ui'
import { CartOutline, CheckmarkOutline } from '@vicons/ionicons5'
import { bookApi } from '@/service/book'
import { orderApi } from '@/service/order'
import type { Book } from '@/types'

const route = useRoute()
const router = useRouter()
const message = useMessage()

const loading = ref(false)
const submitting = ref(false)
const book = ref<Book | null>(null)
const formRef = ref<FormInst | null>(null)

const form = reactive({ shippingAddress: '' })
const rules = { shippingAddress: [{ required: true, message: '请输入收货地址', trigger: 'blur' }] }

function getBookImage(images?: string) {
  if (!images) return ''
  try { return JSON.parse(images)[0] || images.split(',')[0] } catch { return images.split(',')[0] }
}

function getConditionText(level: number) {
  return { 1: '全新', 2: '九成新', 3: '八成新', 4: '七成新', 5: '六成新以下' }[level] || '未知'
}

async function loadBook() {
  const bookId = Number(route.params.bookId)
  if (!bookId) return
  
  loading.value = true
  try {
    const res = await bookApi.getById(bookId)
    book.value = res.data
  } catch { message.error('获取书籍信息失败') }
  finally { loading.value = false }
}

async function handleSubmit() {
  if (!book.value) return
  try {
    await formRef.value?.validate()
    submitting.value = true
    const res = await orderApi.create({ bookId: book.value.id, shippingAddress: form.shippingAddress })
    message.success('订单创建成功！等待卖家确认后即可付款')
    router.replace(`/home/order/${res.data}`)
  } catch {} finally { submitting.value = false }
}

onMounted(loadBook)
</script>

<style scoped>
.order-create-page { padding: 24px 0; background: #f8f9fa; min-height: calc(100vh - 120px); }

.container { max-width: 700px; margin: 0 auto; padding: 0 16px; }

.page-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.page-header h1 { margin: 0 0 4px; font-size: 24px; font-weight: 600; }
.page-header p { margin: 0; color: #666; font-size: 14px; }

.info-card { border-radius: 16px; margin-bottom: 16px; }

.book-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.book-cover {
  width: 100px;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
  background: #f5f5f5;
  flex-shrink: 0;
}

.book-cover img { width: 100%; height: 100%; object-fit: cover; }

.book-detail { flex: 1; }
.book-detail h3 { margin: 0 0 8px; font-size: 18px; font-weight: 600; }
.book-author, .book-condition { margin: 0 0 4px; font-size: 14px; color: #666; }

.book-price { text-align: right; }
.price-label { display: block; font-size: 12px; color: #999; margin-bottom: 4px; }
.price-value { font-size: 24px; font-weight: 700; color: #f5222d; }

.seller-info { display: flex; align-items: center; gap: 16px; }
.seller-detail { flex: 1; }
.seller-name { display: block; font-size: 16px; font-weight: 500; margin-bottom: 4px; }
.seller-rating { display: flex; align-items: center; gap: 8px; font-size: 13px; color: #999; }

.summary-card { background: #fafafa; }

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  font-size: 14px;
  color: #666;
}

.summary-row:last-child { margin-bottom: 0; }
.summary-row .free { color: #18a058; }

.summary-row.total {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.total-price { font-size: 24px; font-weight: 700; color: #f5222d; }

.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background: #fff;
  border-radius: 16px;
  margin-top: 24px;
}

.action-left { display: flex; align-items: baseline; gap: 8px; }
.total-label { font-size: 14px; color: #666; }
.total-amount { font-size: 28px; font-weight: 700; color: #f5222d; }

.action-right { display: flex; gap: 12px; }

@media (max-width: 768px) {
  .book-info { flex-wrap: wrap; }
  .book-price { width: 100%; text-align: left; margin-top: 12px; }
  .action-bar { flex-direction: column; gap: 16px; }
  .action-right { width: 100%; }
  .action-right .n-button { flex: 1; }
}
</style>
