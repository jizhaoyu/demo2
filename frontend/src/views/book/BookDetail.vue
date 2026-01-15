<template>
  <div class="book-detail-page">
    <div class="container">
      <n-spin :show="loading">
        <template v-if="book">
          <!-- 面包屑导航 -->
          <n-breadcrumb class="breadcrumb">
            <n-breadcrumb-item @click="router.push('/home')">首页</n-breadcrumb-item>
            <n-breadcrumb-item @click="router.push('/home')">书籍列表</n-breadcrumb-item>
            <n-breadcrumb-item>{{ book.title }}</n-breadcrumb-item>
          </n-breadcrumb>
          
          <div class="detail-content">
            <!-- 左侧图片区 -->
            <div class="image-section">
              <div class="main-image">
                <n-carousel v-if="images.length > 0" show-arrow dot-type="line">
                  <img v-for="(img, index) in images" :key="index" :src="img" class="carousel-img" />
                </n-carousel>
                <div v-else class="no-image">
                  <n-icon size="80"><ImageOutline /></n-icon>
                  <p>暂无图片</p>
                </div>
              </div>
              <div v-if="images.length > 1" class="image-thumbs">
                <div v-for="(img, index) in images" :key="index" class="thumb-item">
                  <img :src="img" alt="" />
                </div>
              </div>
            </div>
            
            <!-- 右侧信息区 -->
            <div class="info-section">
              <div class="book-header">
                <n-tag :type="getStatusType(book.status)" size="small">{{ getStatusText(book.status) }}</n-tag>
                <h1>{{ book.title }}</h1>
                <p class="book-author">作者：{{ book.author }}</p>
              </div>
              
              <div class="price-box">
                <div class="price-row">
                  <span class="label">售价</span>
                  <span class="current-price">¥{{ book.price.toFixed(2) }}</span>
                  <span v-if="book.originalPrice" class="original-price">
                    原价 ¥{{ book.originalPrice.toFixed(2) }}
                  </span>
                  <n-tag v-if="book.originalPrice && book.price < book.originalPrice" type="error" size="small">
                    省 ¥{{ (book.originalPrice - book.price).toFixed(2) }}
                  </n-tag>
                </div>
              </div>
              
              <div class="info-list">
                <div class="info-item">
                  <span class="label">分类</span>
                  <span class="value">{{ book.categoryName || '未分类' }}</span>
                </div>
                <div class="info-item">
                  <span class="label">成色</span>
                  <n-tag :type="getConditionType(book.conditionLevel)" size="small">
                    {{ getConditionText(book.conditionLevel) }}
                  </n-tag>
                </div>
                <div class="info-item">
                  <span class="label">浏览</span>
                  <span class="value">{{ book.viewCount }} 次</span>
                </div>
                <div class="info-item">
                  <span class="label">发布</span>
                  <span class="value">{{ formatTime(book.createTime) }}</span>
                </div>
              </div>
              
              <!-- 卖家信息 -->
              <div class="seller-box" @click="router.push(`/home/user/${book.sellerId}`)">
                <n-avatar :size="48" :src="book.sellerAvatar" round>
                  {{ book.sellerName?.charAt(0) }}
                </n-avatar>
                <div class="seller-info">
                  <span class="seller-name">{{ book.sellerName }}</span>
                  <div class="seller-rating">
                    <n-rate :value="book.sellerRating || 0" readonly size="small" />
                    <span class="rating-text">{{ book.sellerRating?.toFixed(1) || '暂无评分' }}</span>
                  </div>
                </div>
                <n-icon size="20" color="#999"><ChevronForwardOutline /></n-icon>
              </div>
              
              <!-- 操作按钮 -->
              <div class="action-buttons">
                <template v-if="userStore.isLoggedIn">
                  <template v-if="book.sellerId !== userStore.userId">
                    <n-button size="large" :type="isFavorited ? 'default' : 'tertiary'" @click="toggleFavorite">
                      <template #icon>
                        <n-icon :color="isFavorited ? '#f5222d' : ''">
                          <component :is="isFavorited ? Heart : HeartOutline" />
                        </n-icon>
                      </template>
                      {{ isFavorited ? '已收藏' : '收藏' }}
                    </n-button>
                    <n-button size="large" secondary @click="contactSeller">
                      <template #icon><n-icon><ChatbubbleOutline /></n-icon></template>
                      联系卖家
                    </n-button>
                    <n-button v-if="book.status === 1" type="primary" size="large" @click="handleBuy">
                      <template #icon><n-icon><CartOutline /></n-icon></template>
                      立即购买
                    </n-button>
                  </template>
                  <template v-else>
                    <n-button size="large" @click="router.push(`/home/book/edit/${book.id}`)">
                      <template #icon><n-icon><CreateOutline /></n-icon></template>
                      编辑书籍
                    </n-button>
                  </template>
                </template>
                <template v-else>
                  <n-button type="primary" size="large" block @click="router.push('/login')">
                    登录后购买
                  </n-button>
                </template>
              </div>
            </div>
          </div>
          
          <!-- 书籍描述 -->
          <n-card class="description-card" title="书籍描述">
            <template #header-extra>
              <n-icon color="#18a058"><InformationCircleOutline /></n-icon>
            </template>
            <p class="description-text">{{ book.description || '卖家暂未添加描述' }}</p>
          </n-card>
        </template>
      </n-spin>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useMessage } from 'naive-ui'
import { ImageOutline, HeartOutline, Heart, ChatbubbleOutline, CartOutline, 
         ChevronForwardOutline, CreateOutline, InformationCircleOutline } from '@vicons/ionicons5'
import { bookApi } from '@/service/book'
import { favoriteApi } from '@/service/favorite'
import { useUserStore } from '@/store/user'
import type { Book } from '@/types'

const router = useRouter()
const route = useRoute()
const message = useMessage()
const userStore = useUserStore()

const loading = ref(false)
const book = ref<Book | null>(null)
const isFavorited = ref(false)

const images = computed(() => {
  if (!book.value?.images) return []
  try { return JSON.parse(book.value.images) } catch { return [book.value.images] }
})

function getStatusType(status: number) {
  return { 1: 'success', 2: 'warning', 3: 'default', 4: 'error' }[status] as any || 'default'
}

function getStatusText(status: number) {
  return { 1: '在售', 2: '已预订', 3: '已售', 4: '下架' }[status] || '未知'
}

function getConditionType(level: number) {
  return { 1: 'success', 2: 'info', 3: 'info', 4: 'warning', 5: 'warning' }[level] as any || 'default'
}

function getConditionText(level: number) {
  return { 1: '全新', 2: '九成新', 3: '八成新', 4: '七成新', 5: '六成新以下' }[level] || '未知'
}

function formatTime(time: string) {
  if (!time) return ''
  return time.split('T')[0]
}

async function fetchBook() {
  const id = Number(route.params.id)
  if (!id) return
  
  loading.value = true
  try {
    book.value = (await bookApi.getById(id)).data
    if (userStore.isLoggedIn) {
      isFavorited.value = (await favoriteApi.check(id)).data
    }
  } catch { message.error('获取书籍信息失败') }
  finally { loading.value = false }
}

async function toggleFavorite() {
  if (!book.value) return
  try {
    if (isFavorited.value) {
      await favoriteApi.remove(book.value.id)
      message.success('已取消收藏')
    } else {
      await favoriteApi.add(book.value.id)
      message.success('收藏成功')
    }
    isFavorited.value = !isFavorited.value
  } catch {}
}

function handleBuy() {
  if (book.value) router.push(`/home/order/create/${book.value.id}`)
}

// 联系卖家，携带书籍信息
function contactSeller() {
  if (!book.value) return
  const bookInfo = {
    id: book.value.id,
    title: book.value.title,
    price: book.value.price,
    image: images.value[0] || ''
  }
  router.push({
    path: `/home/chat/${book.value.sellerId}`,
    query: { bookId: bookInfo.id, bookTitle: bookInfo.title, bookPrice: bookInfo.price, bookImage: bookInfo.image }
  })
}

onMounted(fetchBook)
</script>

<style scoped>
.book-detail-page { padding: 24px 0; background: #f8f9fa; min-height: calc(100vh - 120px); }

.container { max-width: 1200px; margin: 0 auto; padding: 0 16px; }

.breadcrumb { margin-bottom: 20px; }

.detail-content {
  display: grid;
  grid-template-columns: 480px 1fr;
  gap: 32px;
  background: #fff;
  border-radius: 16px;
  padding: 32px;
  margin-bottom: 24px;
}

.main-image {
  border-radius: 12px;
  overflow: hidden;
  background: #f5f5f5;
}

.carousel-img { width: 100%; height: 400px; object-fit: contain; background: #f5f5f5; }

.no-image {
  height: 400px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #ccc;
}

.image-thumbs {
  display: flex;
  gap: 8px;
  margin-top: 12px;
}

.thumb-item {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
}

.thumb-item:hover { border-color: #18a058; }
.thumb-item img { width: 100%; height: 100%; object-fit: cover; }

.book-header { margin-bottom: 20px; }
.book-header h1 { margin: 12px 0 8px; font-size: 24px; font-weight: 600; color: #1a1a1a; }
.book-author { margin: 0; color: #666; font-size: 15px; }

.price-box {
  background: linear-gradient(135deg, #fff5f5, #fff0f0);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
}

.price-row { display: flex; align-items: baseline; gap: 12px; }
.price-row .label { color: #999; font-size: 14px; }
.current-price { font-size: 32px; font-weight: 700; color: #f5222d; }
.original-price { font-size: 14px; color: #999; text-decoration: line-through; }

.info-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.info-item { display: flex; align-items: center; gap: 12px; }
.info-item .label { color: #999; font-size: 14px; min-width: 40px; }
.info-item .value { color: #333; font-size: 14px; }

.seller-box {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #f9f9f9;
  border-radius: 12px;
  cursor: pointer;
  transition: background 0.2s;
  margin-bottom: 24px;
}

.seller-box:hover { background: #f0f0f0; }

.seller-info { flex: 1; }
.seller-name { font-weight: 600; color: #1a1a1a; display: block; margin-bottom: 4px; }
.seller-rating { display: flex; align-items: center; gap: 8px; }
.rating-text { font-size: 12px; color: #999; }

.action-buttons { display: flex; gap: 12px; }
.action-buttons .n-button { flex: 1; }

.description-card { border-radius: 16px; }
.description-text {
  margin: 0;
  color: #666;
  line-height: 1.8;
  white-space: pre-wrap;
  min-height: 100px;
}

@media (max-width: 900px) {
  .detail-content { grid-template-columns: 1fr; padding: 20px; }
  .carousel-img { height: 300px; }
  .action-buttons { flex-wrap: wrap; }
}
</style>
