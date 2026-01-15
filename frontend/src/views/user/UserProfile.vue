<template>
  <div class="user-profile-page">
    <div class="page-container">
      <n-spin :show="loading">
        <n-card v-if="user">
          <div class="user-header">
            <n-avatar :size="80" :src="user.avatar" round>
              {{ user.username?.charAt(0).toUpperCase() }}
            </n-avatar>
            <div class="user-info">
              <h2 class="username">{{ user.username }}</h2>
              <div class="user-stats">
                <n-space>
                  <span>
                    <n-icon><StarOutline /></n-icon>
                    信用评分: {{ user.avgRating?.toFixed(1) || '暂无' }}
                  </span>
                  <span>评价次数: {{ user.ratingCount || 0 }}</span>
                  <span>注册时间: {{ formatDate(user.createTime) }}</span>
                </n-space>
              </div>
              <n-button
                v-if="userStore.isLoggedIn && userStore.user?.id !== user.id"
                type="primary"
                size="small"
                @click="router.push(`/home/chat/${user.id}`)"
              >
                发送消息
              </n-button>
            </div>
          </div>
        </n-card>
        
        <n-card title="用户评价" class="reviews-card">
          <n-empty v-if="reviews.length === 0" description="暂无评价" />
          <n-list v-else>
            <n-list-item v-for="review in reviews" :key="review.id">
              <n-thing>
                <template #avatar>
                  <n-avatar :src="review.reviewerAvatar" round>
                    {{ review.reviewerName?.charAt(0).toUpperCase() }}
                  </n-avatar>
                </template>
                <template #header>
                  <span>{{ review.reviewerName }}</span>
                </template>
                <template #header-extra>
                  <n-rate :value="review.rating" readonly size="small" />
                </template>
                <template #description>
                  {{ formatDate(review.createTime) }}
                </template>
                {{ review.content || '该用户未留下评价内容' }}
              </n-thing>
            </n-list-item>
          </n-list>
          
          <div v-if="reviewTotal > reviewPageSize" class="pagination">
            <n-pagination
              v-model:page="reviewPage"
              :page-size="reviewPageSize"
              :item-count="reviewTotal"
              @update:page="loadReviews"
            />
          </div>
        </n-card>
      </n-spin>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { StarOutline } from '@vicons/ionicons5'
import { useUserStore } from '@/store/user'
import { userApi } from '@/service/user'
import { reviewApi } from '@/service/review'
import type { User, Review } from '@/types'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const user = ref<User | null>(null)
const reviews = ref<Review[]>([])
const reviewPage = ref(1)
const reviewPageSize = ref(10)
const reviewTotal = ref(0)

function formatDate(dateStr?: string) {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

async function loadUser() {
  const userId = Number(route.params.id)
  if (!userId) return
  
  loading.value = true
  try {
    const res = await userApi.getUserById(userId)
    user.value = res.data
  } catch (e) {
    // ignore
  } finally {
    loading.value = false
  }
}

async function loadReviews() {
  const userId = Number(route.params.id)
  if (!userId) return
  
  try {
    const res = await reviewApi.getUserReviews(userId, {
      page: reviewPage.value,
      size: reviewPageSize.value
    })
    reviews.value = res.data.records || []
    reviewTotal.value = res.data.total || 0
  } catch (e) {
    // ignore
  }
}

watch(() => route.params.id, () => {
  loadUser()
  loadReviews()
})

onMounted(() => {
  loadUser()
  loadReviews()
})
</script>

<style scoped>
.user-profile-page {
  padding: 24px;
}

.page-container {
  max-width: 800px;
  margin: 0 auto;
}

.user-header {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}

.user-info {
  flex: 1;
}

.username {
  margin: 0 0 8px;
  font-size: 24px;
}

.user-stats {
  color: #666;
  margin-bottom: 12px;
}

.reviews-card {
  margin-top: 16px;
}

.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: center;
}
</style>
