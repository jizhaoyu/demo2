<template>
  <div class="book-list-page">
    <div class="container">
      <!-- 页面头部 -->
      <div class="page-header">
        <div class="header-content">
          <h1>发现好书</h1>
          <p>探索海量二手书籍，找到您心仪的那一本</p>
        </div>
        <div class="header-stats">
          <div class="stat-item">
            <span class="stat-value">{{ total }}</span>
            <span class="stat-label">本在售</span>
          </div>
        </div>
      </div>
      
      <!-- 搜索和筛选 -->
      <n-card class="filter-card">
        <div class="search-row">
          <n-input-group>
            <n-input v-model:value="query.keyword" placeholder="搜索书名、作者..." clearable size="large"
                     @keyup.enter="handleSearch">
              <template #prefix><n-icon><SearchOutline /></n-icon></template>
            </n-input>
            <n-button type="primary" size="large" @click="handleSearch">
              <template #icon><n-icon><SearchOutline /></n-icon></template>
              搜索
            </n-button>
          </n-input-group>
        </div>
        
        <n-divider style="margin: 16px 0" />
        
        <div class="filter-row">
          <div class="filter-group">
            <span class="filter-label">分类：</span>
            <n-select v-model:value="query.categoryId" :options="categoryOptions" placeholder="全部分类"
                      clearable style="width: 160px" @update:value="handleCategoryChange" />
          </div>
          
          <div class="filter-group">
            <span class="filter-label">价格：</span>
            <n-input-number v-model:value="query.minPrice" placeholder="最低" :min="0" 
                            style="width: 100px" :show-button="false" />
            <span class="filter-separator">-</span>
            <n-input-number v-model:value="query.maxPrice" placeholder="最高" :min="0" 
                            style="width: 100px" :show-button="false" />
            <n-button size="small" @click="handleSearch">筛选</n-button>
          </div>
          
          <div class="filter-group">
            <span class="filter-label">排序：</span>
            <n-radio-group v-model:value="sortKey" size="small" @update:value="handleSort">
              <n-radio-button value="createTime-desc">最新</n-radio-button>
              <n-radio-button value="price-asc">价格↑</n-radio-button>
              <n-radio-button value="price-desc">价格↓</n-radio-button>
              <n-radio-button value="viewCount-desc">热门</n-radio-button>
            </n-radio-group>
          </div>
          
          <n-button text type="primary" @click="resetFilter">
            <template #icon><n-icon><RefreshOutline /></n-icon></template>
            重置
          </n-button>
        </div>
      </n-card>
      
      <!-- 书籍列表 -->
      <n-spin :show="loading">
        <div v-if="books.length > 0" class="book-grid">
          <div v-for="book in books" :key="book.id" class="book-card"
               @click="router.push(`/home/book/${book.id}`)">
            <div class="book-cover">
              <img v-if="book.images" :src="getFirstImage(book.images)" alt="书籍封面" />
              <div v-else class="no-image">
                <n-icon size="40"><ImageOutline /></n-icon>
              </div>
              <div v-if="book.status !== 1" class="status-badge" :class="getStatusClass(book.status)">
                {{ getStatusText(book.status) }}
              </div>
              <div class="book-condition">{{ getConditionText(book.conditionLevel) }}</div>
            </div>
            <div class="book-info">
              <h3 class="book-title">{{ book.title }}</h3>
              <p class="book-author">{{ book.author }}</p>
              <div class="book-footer">
                <span class="book-price">¥{{ book.price.toFixed(2) }}</span>
                <div class="book-seller">
                  <n-avatar :size="20" round :src="book.sellerAvatar">
                    {{ book.sellerName?.charAt(0) }}
                  </n-avatar>
                  <span>{{ book.sellerName }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <n-empty v-else class="empty-state" description="暂无符合条件的书籍">
          <template #extra>
            <n-button type="primary" @click="resetFilter">清除筛选条件</n-button>
          </template>
        </n-empty>
      </n-spin>
      
      <!-- 分页 -->
      <div v-if="total > 0" class="pagination">
        <n-pagination v-model:page="query.page" :page-size="query.size" :item-count="total"
                      show-size-picker :page-sizes="[12, 24, 48]" @update:page="fetchBooks"
                      @update:page-size="handlePageSizeChange" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { SearchOutline, ImageOutline, RefreshOutline } from '@vicons/ionicons5'
import { bookApi } from '@/service/book'
import { categoryApi } from '@/service/category'
import type { Book, Category, BookQuery } from '@/types'

const router = useRouter()

const loading = ref(false)
const books = ref<Book[]>([])
const total = ref(0)
const categories = ref<Category[]>([])
const sortKey = ref('createTime-desc')

const query = ref<BookQuery>({
  page: 1, size: 12, keyword: '', categoryId: undefined,
  minPrice: undefined, maxPrice: undefined, sortBy: 'createTime', sortOrder: 'desc'
})

const categoryOptions = computed(() => [
  { label: '全部分类', value: undefined },
  ...categories.value.map(c => ({ label: c.name, value: c.id }))
])

function getFirstImage(images: string) {
  try { return JSON.parse(images)[0] || '' } catch { return images }
}

function getStatusClass(status: number) {
  return { 2: 'reserved', 3: 'sold', 4: 'offline' }[status] || ''
}

function getStatusText(status: number) {
  return { 1: '在售', 2: '已预订', 3: '已售', 4: '下架' }[status] || '未知'
}

function getConditionText(level: number) {
  return { 1: '全新', 2: '九成新', 3: '八成新', 4: '七成新', 5: '六成新以下' }[level] || ''
}

async function fetchCategories() {
  try { categories.value = (await categoryApi.list()).data } catch {}
}

async function fetchBooks() {
  loading.value = true
  try {
    const res = await bookApi.list(query.value)
    books.value = res.data.records
    total.value = res.data.total
  } catch {} finally { loading.value = false }
}

function handleSearch() { query.value.page = 1; fetchBooks() }

function handleCategoryChange(value: number | null) {
  // n-select clearable 会返回 null，转换为 undefined
  query.value.categoryId = value ?? undefined
  handleSearch()
}

function handleSort(key: string) {
  const [sortBy, sortOrder] = key.split('-')
  query.value.sortBy = sortBy
  query.value.sortOrder = sortOrder
  handleSearch()
}

function handlePageSizeChange(size: number) {
  query.value.size = size
  query.value.page = 1
  fetchBooks()
}

function resetFilter() {
  query.value = { page: 1, size: 12, keyword: '', categoryId: undefined,
    minPrice: undefined, maxPrice: undefined, sortBy: 'createTime', sortOrder: 'desc' }
  sortKey.value = 'createTime-desc'
  fetchBooks()
}

onMounted(() => { fetchCategories(); fetchBooks() })
</script>

<style scoped>
.book-list-page { padding: 24px 0; background: #f8f9fa; min-height: calc(100vh - 120px); }

.container { max-width: 1200px; margin: 0 auto; padding: 0 16px; }

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 32px;
  background: linear-gradient(135deg, #18a058 0%, #2080f0 100%);
  border-radius: 16px;
  color: #fff;
}

.header-content h1 { margin: 0 0 8px; font-size: 28px; font-weight: 700; }
.header-content p { margin: 0; opacity: 0.9; }

.header-stats { text-align: center; }
.stat-value { display: block; font-size: 36px; font-weight: 700; }
.stat-label { font-size: 14px; opacity: 0.9; }

.filter-card { margin-bottom: 24px; border-radius: 12px; }

.search-row { max-width: 600px; }

.filter-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 24px;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-label { color: #666; font-size: 14px; white-space: nowrap; }
.filter-separator { color: #999; }

.book-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 24px;
}

.book-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.book-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
}

.book-cover {
  height: 200px;
  position: relative;
  background: linear-gradient(135deg, #f5f5f5, #e8e8e8);
  overflow: hidden;
}

.book-cover img { width: 100%; height: 100%; object-fit: cover; }

.no-image {
  width: 100%; height: 100%;
  display: flex; align-items: center; justify-content: center;
  color: #ccc;
}

.status-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  color: #fff;
}

.status-badge.reserved { background: #f0a020; }
.status-badge.sold { background: #909399; }
.status-badge.offline { background: #f56c6c; }

.book-condition {
  position: absolute;
  bottom: 12px;
  left: 12px;
  padding: 4px 10px;
  background: rgba(0, 0, 0, 0.6);
  border-radius: 4px;
  font-size: 12px;
  color: #fff;
}

.book-info { padding: 16px; }

.book-title {
  margin: 0 0 6px;
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.book-author {
  margin: 0 0 12px;
  font-size: 14px;
  color: #666;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.book-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.book-price {
  font-size: 20px;
  font-weight: 700;
  color: #f5222d;
}

.book-seller {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #999;
}

.empty-state { padding: 60px 0; }

.pagination { margin-top: 32px; display: flex; justify-content: center; }

@media (max-width: 768px) {
  .page-header { flex-direction: column; text-align: center; gap: 16px; padding: 24px; }
  .filter-row { flex-direction: column; align-items: flex-start; }
  .book-grid { grid-template-columns: repeat(2, 1fr); gap: 12px; }
  .book-cover { height: 160px; }
}
</style>
