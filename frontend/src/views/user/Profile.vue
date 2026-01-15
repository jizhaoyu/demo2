<template>
  <div class="profile-page">
    <div class="container">
      <!-- 用户信息卡片 -->
      <div class="profile-header">
        <div class="user-card">
          <n-avatar :size="80" :src="userStore.user?.avatar" round class="user-avatar">
            {{ userStore.username?.charAt(0).toUpperCase() }}
          </n-avatar>
          <div class="user-info">
            <h2>{{ userStore.username }}</h2>
            <p>{{ userStore.user?.email }}</p>
            <div class="user-stats">
              <div class="stat-item">
                <n-icon color="#f0a020"><StarOutline /></n-icon>
                <span>{{ userStore.user?.avgRating?.toFixed(1) || '暂无' }} 评分</span>
              </div>
              <div class="stat-item">
                <n-icon color="#18a058"><TimeOutline /></n-icon>
                <span>{{ formatDate(userStore.user?.createTime) }} 加入</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <n-card class="main-card">
        <n-tabs type="line" animated>
          <n-tab-pane name="info" tab="基本信息">
            <div class="tab-content">
              <n-form ref="profileFormRef" :model="profileForm" :rules="profileRules"
                      label-placement="left" label-width="100" class="profile-form">
                <n-form-item label="用户名">
                  <n-input :value="userStore.username" disabled>
                    <template #suffix>
                      <n-tag size="small" type="info">不可修改</n-tag>
                    </template>
                  </n-input>
                </n-form-item>
                <n-form-item label="邮箱" path="email">
                  <n-input v-model:value="profileForm.email" placeholder="请输入邮箱">
                    <template #prefix><n-icon><MailOutline /></n-icon></template>
                  </n-input>
                </n-form-item>
                <n-form-item label="手机号" path="phone">
                  <n-input v-model:value="profileForm.phone" placeholder="请输入手机号">
                    <template #prefix><n-icon><CallOutline /></n-icon></template>
                  </n-input>
                </n-form-item>
                <n-form-item label="头像URL">
                  <n-input v-model:value="profileForm.avatar" placeholder="请输入头像图片URL">
                    <template #prefix><n-icon><ImageOutline /></n-icon></template>
                  </n-input>
                </n-form-item>
                <n-form-item>
                  <n-button type="primary" :loading="saving" @click="handleSaveProfile">
                    <template #icon><n-icon><SaveOutline /></n-icon></template>
                    保存修改
                  </n-button>
                </n-form-item>
              </n-form>
            </div>
          </n-tab-pane>
          
          <n-tab-pane name="password" tab="修改密码">
            <div class="tab-content">
              <n-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules"
                      label-placement="left" label-width="100" class="profile-form">
                <n-form-item label="原密码" path="oldPassword">
                  <n-input v-model:value="passwordForm.oldPassword" type="password" show-password-on="click"
                           placeholder="请输入原密码">
                    <template #prefix><n-icon><LockClosedOutline /></n-icon></template>
                  </n-input>
                </n-form-item>
                <n-form-item label="新密码" path="newPassword">
                  <n-input v-model:value="passwordForm.newPassword" type="password" show-password-on="click"
                           placeholder="请输入新密码（至少6位）">
                    <template #prefix><n-icon><LockClosedOutline /></n-icon></template>
                  </n-input>
                </n-form-item>
                <n-form-item label="确认密码" path="confirmPassword">
                  <n-input v-model:value="passwordForm.confirmPassword" type="password" show-password-on="click"
                           placeholder="请再次输入新密码">
                    <template #prefix><n-icon><LockClosedOutline /></n-icon></template>
                  </n-input>
                </n-form-item>
                <n-form-item>
                  <n-button type="primary" :loading="changingPassword" @click="handleChangePassword">
                    <template #icon><n-icon><KeyOutline /></n-icon></template>
                    修改密码
                  </n-button>
                </n-form-item>
              </n-form>
            </div>
          </n-tab-pane>
          
          <n-tab-pane name="stats" tab="账号统计">
            <div class="tab-content">
              <n-grid :cols="4" :x-gap="16" :y-gap="16" class="stats-grid">
                <n-gi>
                  <div class="stat-card">
                    <n-icon size="32" color="#f0a020"><StarOutline /></n-icon>
                    <span class="stat-value">{{ userStore.user?.avgRating?.toFixed(1) || '-' }}</span>
                    <span class="stat-label">信用评分</span>
                  </div>
                </n-gi>
                <n-gi>
                  <div class="stat-card">
                    <n-icon size="32" color="#2080f0"><ChatbubbleOutline /></n-icon>
                    <span class="stat-value">{{ userStore.user?.ratingCount || 0 }}</span>
                    <span class="stat-label">收到评价</span>
                  </div>
                </n-gi>
                <n-gi>
                  <div class="stat-card">
                    <n-icon size="32" color="#18a058"><TimeOutline /></n-icon>
                    <span class="stat-value">{{ getDays() }}</span>
                    <span class="stat-label">注册天数</span>
                  </div>
                </n-gi>
                <n-gi>
                  <div class="stat-card">
                    <n-icon size="32" :color="userStore.user?.status === 1 ? '#18a058' : '#f5222d'">
                      <ShieldCheckmarkOutline />
                    </n-icon>
                    <span class="stat-value">{{ userStore.user?.status === 1 ? '正常' : '禁用' }}</span>
                    <span class="stat-label">账号状态</span>
                  </div>
                </n-gi>
              </n-grid>
            </div>
          </n-tab-pane>
        </n-tabs>
      </n-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useMessage, FormInst } from 'naive-ui'
import { StarOutline, TimeOutline, MailOutline, CallOutline, ImageOutline, SaveOutline,
         LockClosedOutline, KeyOutline, ChatbubbleOutline, ShieldCheckmarkOutline } from '@vicons/ionicons5'
import { useUserStore } from '@/store/user'
import { userApi } from '@/service/user'

const message = useMessage()
const userStore = useUserStore()

const profileFormRef = ref<FormInst | null>(null)
const passwordFormRef = ref<FormInst | null>(null)
const saving = ref(false)
const changingPassword = ref(false)

const profileForm = reactive({ email: '', phone: '', avatar: '' })
const passwordForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

const profileRules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
  ]
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: (_: any, v: string) => v === passwordForm.newPassword, message: '两次密码不一致', trigger: 'blur' }
  ]
}

function formatDate(dateStr?: string) {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

function getDays() {
  if (!userStore.user?.createTime) return 0
  const diff = Date.now() - new Date(userStore.user.createTime).getTime()
  return Math.floor(diff / (1000 * 60 * 60 * 24))
}

async function loadProfile() {
  try {
    const res = await userApi.getProfile()
    profileForm.email = res.data.email || ''
    profileForm.phone = res.data.phone || ''
    profileForm.avatar = res.data.avatar || ''
  } catch {}
}

async function handleSaveProfile() {
  try {
    await profileFormRef.value?.validate()
    saving.value = true
    await userApi.updateProfile(profileForm)
    message.success('保存成功')
    await userStore.fetchUserInfo()
  } catch {} finally { saving.value = false }
}

async function handleChangePassword() {
  try {
    await passwordFormRef.value?.validate()
    changingPassword.value = true
    await userApi.changePassword({ oldPassword: passwordForm.oldPassword, newPassword: passwordForm.newPassword })
    message.success('密码修改成功')
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } catch {} finally { changingPassword.value = false }
}

onMounted(loadProfile)
</script>

<style scoped>
.profile-page { padding: 24px 0; background: #f8f9fa; min-height: calc(100vh - 120px); }

.container { max-width: 900px; margin: 0 auto; padding: 0 16px; }

.profile-header {
  background: linear-gradient(135deg, #18a058 0%, #2080f0 100%);
  border-radius: 16px;
  padding: 32px;
  margin-bottom: 24px;
}

.user-card { display: flex; align-items: center; gap: 24px; color: #fff; }

.user-avatar { border: 4px solid rgba(255, 255, 255, 0.3); font-size: 32px; font-weight: 700; }

.user-info h2 { margin: 0 0 4px; font-size: 24px; font-weight: 600; }
.user-info p { margin: 0 0 12px; opacity: 0.9; }

.user-stats { display: flex; gap: 24px; }
.user-stats .stat-item { display: flex; align-items: center; gap: 6px; font-size: 14px; opacity: 0.9; }

.main-card { border-radius: 16px; }

.tab-content { padding: 24px 0; }

.profile-form { max-width: 500px; }

.stats-grid { margin-top: 8px; }

.stat-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 24px;
  background: #f9f9f9;
  border-radius: 12px;
  text-align: center;
}

.stat-value { font-size: 28px; font-weight: 700; color: #1a1a1a; }
.stat-label { font-size: 14px; color: #666; }

@media (max-width: 768px) {
  .user-card { flex-direction: column; text-align: center; }
  .user-stats { justify-content: center; }
  .stats-grid { grid-template-columns: repeat(2, 1fr) !important; }
}
</style>
