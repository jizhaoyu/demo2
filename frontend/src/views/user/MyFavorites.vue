<template>
  <div class="favorites-page">
    <div class="container">
      <n-card class="main-card">
        <template #header>
          <div class="card-header">
            <n-icon size="28" color="#f5222d"><HeartOutline /></n-icon>
            <div>
              <h2>我的收藏</h2>
              <p>您收藏的书籍都在这里</p>
            </div>
          </div>
        </template>
        
        <n-spin :show="loading">
          <n-empty v-if="!loading && books.length === 0" class="empty-state" description="暂无收藏的书籍">
            <template #extra>
              <n-button type="primary" @click="router.push('/home')">去发现好书</n-button>
            </template>
          </n-empty>
          
          <div v-else class="book-grid">
            <div v-for="book in books" :key="book.id" class="book-card">
              <div class="book-cover" @click="router.push(`/home/book/${book.id}`)">
                <img :src="getBookImage(book.images)" :alt="book.title" />
                <div v-if="book.status !== 1" class="status-overlay">
                  <span>{{ book.status === 3 ? '已售出' : '已下架' }}</span>
                </div>
              </div>
              <div class="book-info">
                <h3 @click="router.push(`/home/book/${book.id}`)">{{ book.title }}</h3>
                <p class="book-author">{{ book.author }}</p>
                <div class="book-footer">
                  <span class="book-price">¥{{ book.price.toFixed(2) }}</span>
                  <n-button text type="error" size="small" @click.stop="handleRemoveFavorite(book.id)">
                    <template #icon><n-icon><HeartDislikeOutline /></n-icon></template>
                    取消
                  </n-button>
                </div>
              </div>
            </div>
          </div>
          
          <div v-if="total > pageSize" class="pagination">
            <n-pagination v-model:page="currentPage" :page-size="pageSize" :item-count="total"
                          @update:page="loadFavorites" />
          </div>
        </n-spin>
      </n-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage } from 'naive-ui'
import { HeartOutline, HeartDislikeOutline } from '@vicons/ionicons5'
import { favoriteApi } from '@/service/favorite'
import type { Book } from '@/types'

const router = useRouter()
const message = useMessage()

const loading = ref(false)
const books = ref<Book[]>([])
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

function getBookImage(images?: string) {
  if (!images) return ''
  try { return JSON.parse(images)[0] || images.split(',')[0] } catch { return images.split(',')[0] }
}

async function loadFavorites() {
  loading.value = true
  try {
    const res = await favoriteApi.getList({ page: currentPage.value, size: pageSize.value })
    books.value = res.data.records || []
    total.value = res.data.total || 0
  } catch {} finally { loading.value = false }
}

async function handleRemoveFavorite(bookId: number) {
  try {
    await favoriteApi.remove(bookId)
    message.success('已取消收藏')
    loadFavorites()
  } catch {}
}

onMounted(loadFavorites)
</script>

<style scoped>
.favorites-page { padding: 24px 0; background: #f8f9fa; min-height: calc(100vh - 120px); }

.container { max-width: 1200px; margin: 0 auto; padding: 0 16px; }

.main-card { border-radius: 16px; }

.card-header { display: flex; align-items: center; gap: 16px; }
.card-header h2 { margin: 0 0 4px; font-size: 20px; font-weight: 600; }
.card-header p { margin: 0; color: #666; font-size: 14px; }

.empty-state { padding: 60px 0; }

.book-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.book-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
}

.book-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.book-cover {
  position: relative;
  height: 180px;
  background: #f5f5f5;
  cursor: pointer;
  overflow: hidden;
}

.book-cover img { width: 100%; height: 100%; object-fit: cover; }

.status-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 14px;
  font-weight: 500;
}

.book-info { padding: 12px; }

.book-info h3 {
  margin: 0 0 6px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #1a1a1a;
}

.book-info h3:hover { color: #18a058; }

.book-author {
  margin: 0 0 10px;
  font-size: 13px;
  color: #666;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.book-footer { display: flex; justify-content: space-between; align-items: center; }

.book-price { font-size: 18px; font-weight: 700; color: #f5222d; }

.pagination { margin-top: 24px; display: flex; justify-content: center; }

@media (max-width: 768px) {
  .book-grid { grid-template-columns: repeat(2, 1fr); gap: 12px; }
  .book-cover { height: 140px; }
}
</style>
