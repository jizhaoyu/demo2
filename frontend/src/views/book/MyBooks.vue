<template>
  <div class="my-books-page">
    <div class="container">
      <n-card class="main-card">
        <template #header>
          <div class="card-header">
            <div class="header-left">
              <n-icon size="28" color="#18a058"><LibraryOutline /></n-icon>
              <div>
                <h2>我的书籍</h2>
                <p>管理您发布的所有书籍</p>
              </div>
            </div>
            <n-button type="primary" @click="router.push('/home/book/publish')">
              <template #icon><n-icon><AddOutline /></n-icon></template>
              发布书籍
            </n-button>
          </div>
        </template>
        
        <!-- 统计卡片 -->
        <div class="stats-row">
          <div class="stat-card">
            <span class="stat-value">{{ stats.total }}</span>
            <span class="stat-label">全部</span>
          </div>
          <div class="stat-card on-sale">
            <span class="stat-value">{{ stats.onSale }}</span>
            <span class="stat-label">在售</span>
          </div>
          <div class="stat-card sold">
            <span class="stat-value">{{ stats.sold }}</span>
            <span class="stat-label">已售</span>
          </div>
          <div class="stat-card offline">
            <span class="stat-value">{{ stats.offline }}</span>
            <span class="stat-label">下架</span>
          </div>
        </div>
        
        <n-divider />
        
        <n-spin :show="loading">
          <div v-if="books.length > 0" class="book-list">
            <div v-for="book in books" :key="book.id" class="book-item">
              <div class="book-cover" @click="router.push(`/home/book/${book.id}`)">
                <img v-if="book.images" :src="getFirstImage(book.images)" alt="" />
                <div v-else class="no-image"><n-icon size="32"><ImageOutline /></n-icon></div>
              </div>
              <div class="book-info">
                <div class="book-main">
                  <h3 @click="router.push(`/home/book/${book.id}`)">{{ book.title }}</h3>
                  <p class="book-author">{{ book.author }}</p>
                  <div class="book-meta">
                    <span class="book-price">¥{{ book.price.toFixed(2) }}</span>
                    <n-tag :type="statusMap[book.status]?.type" size="small">
                      {{ statusMap[book.status]?.text }}
                    </n-tag>
                  </div>
                </div>
                <div class="book-stats">
                  <span><n-icon><EyeOutline /></n-icon> {{ book.viewCount }}</span>
                  <span>{{ formatDate(book.createTime) }}</span>
                </div>
              </div>
              <div class="book-actions">
                <n-button size="small" quaternary @click="router.push(`/home/book/${book.id}`)">
                  <template #icon><n-icon><EyeOutline /></n-icon></template>
                </n-button>
                <n-button size="small" quaternary @click="router.push(`/home/book/edit/${book.id}`)">
                  <template #icon><n-icon><CreateOutline /></n-icon></template>
                </n-button>
                <n-button v-if="book.status === 1" size="small" quaternary @click="handleStatusChange(book, 4)">
                  <template #icon><n-icon><ArrowDownOutline /></n-icon></template>
                </n-button>
                <n-button v-if="book.status === 4" size="small" quaternary @click="handleStatusChange(book, 1)">
                  <template #icon><n-icon><ArrowUpOutline /></n-icon></template>
                </n-button>
                <n-popconfirm @positive-click="handleDelete(book.id)">
                  <template #trigger>
                    <n-button size="small" quaternary type="error">
                      <template #icon><n-icon><TrashOutline /></n-icon></template>
                    </n-button>
                  </template>
                  确定删除这本书吗？
                </n-popconfirm>
              </div>
            </div>
          </div>
          
          <n-empty v-else description="您还没有发布任何书籍">
            <template #extra>
              <n-button type="primary" @click="router.push('/home/book/publish')">立即发布</n-button>
            </template>
          </n-empty>
        </n-spin>
        
        <div v-if="total > 0" class="pagination">
          <n-pagination v-model:page="page" :page-size="size" :item-count="total" @update:page="fetchBooks" />
        </div>
      </n-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage } from 'naive-ui'
import { LibraryOutline, AddOutline, ImageOutline, EyeOutline, CreateOutline,
         ArrowDownOutline, ArrowUpOutline, TrashOutline } from '@vicons/ionicons5'
import { bookApi } from '@/service/book'
import type { Book } from '@/types'

const router = useRouter()
const message = useMessage()

const loading = ref(false)
const books = ref<Book[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(10)

const statusMap: Record<number, { text: string; type: 'success' | 'warning' | 'default' | 'error' }> = {
  1: { text: '在售', type: 'success' },
  2: { text: '已预订', type: 'warning' },
  3: { text: '已售', type: 'default' },
  4: { text: '下架', type: 'error' }
}

const stats = computed(() => {
  const all = books.value
  return {
    total: total.value,
    onSale: all.filter(b => b.status === 1).length,
    sold: all.filter(b => b.status === 3).length,
    offline: all.filter(b => b.status === 4).length
  }
})

function getFirstImage(images: string) {
  try { return JSON.parse(images)[0] || '' } catch { return images }
}

function formatDate(time: string) {
  if (!time) return ''
  return time.split('T')[0]
}

async function fetchBooks() {
  loading.value = true
  try {
    const res = await bookApi.myBooks(page.value, size.value)
    if (res.data?.records) {
      books.value = res.data.records
      total.value = res.data.total
    } else if (Array.isArray(res.data)) {
      books.value = res.data
      total.value = res.data.length
    }
  } catch {} finally { loading.value = false }
}

async function handleStatusChange(book: Book, status: number) {
  try {
    await bookApi.updateStatus(book.id, status)
    message.success(status === 1 ? '已上架' : '已下架')
    fetchBooks()
  } catch {}
}

async function handleDelete(id: number) {
  try {
    await bookApi.delete(id)
    message.success('删除成功')
    fetchBooks()
  } catch {}
}

onMounted(fetchBooks)
</script>

<style scoped>
.my-books-page { padding: 24px 0; background: #f8f9fa; min-height: calc(100vh - 120px); }

.container { max-width: 1000px; margin: 0 auto; padding: 0 16px; }

.main-card { border-radius: 16px; }

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-left h2 { margin: 0 0 4px; font-size: 20px; font-weight: 600; }
.header-left p { margin: 0; color: #666; font-size: 14px; }

.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.stat-card {
  text-align: center;
  padding: 16px;
  background: #f9f9f9;
  border-radius: 12px;
}

.stat-card.on-sale { background: #e8f5e9; }
.stat-card.sold { background: #e3f2fd; }
.stat-card.offline { background: #fce4ec; }

.stat-value { display: block; font-size: 28px; font-weight: 700; color: #1a1a1a; }
.stat-label { font-size: 13px; color: #666; }

.book-list { display: flex; flex-direction: column; gap: 16px; }

.book-item {
  display: flex;
  gap: 16px;
  padding: 16px;
  background: #fafafa;
  border-radius: 12px;
  transition: background 0.2s;
}

.book-item:hover { background: #f5f5f5; }

.book-cover {
  width: 80px;
  height: 100px;
  border-radius: 8px;
  overflow: hidden;
  background: #e8e8e8;
  flex-shrink: 0;
  cursor: pointer;
}

.book-cover img { width: 100%; height: 100%; object-fit: cover; }

.no-image {
  width: 100%; height: 100%;
  display: flex; align-items: center; justify-content: center;
  color: #ccc;
}

.book-info { flex: 1; display: flex; flex-direction: column; justify-content: space-between; }

.book-main h3 {
  margin: 0 0 4px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  color: #1a1a1a;
}

.book-main h3:hover { color: #18a058; }

.book-author { margin: 0 0 8px; font-size: 14px; color: #666; }

.book-meta { display: flex; align-items: center; gap: 12px; }

.book-price { font-size: 18px; font-weight: 600; color: #f5222d; }

.book-stats {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #999;
}

.book-stats span { display: flex; align-items: center; gap: 4px; }

.book-actions {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.pagination { margin-top: 24px; display: flex; justify-content: center; }

@media (max-width: 768px) {
  .stats-row { grid-template-columns: repeat(2, 1fr); }
  .book-item { flex-wrap: wrap; }
  .book-actions { flex-direction: row; width: 100%; justify-content: flex-end; }
}
</style>
