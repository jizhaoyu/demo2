<template>
  <div class="login-page">
    <!-- 装饰背景 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>
    
    <div class="login-container">
      <!-- 左侧品牌区域 -->
      <div class="brand-section">
        <div class="brand-content">
          <div class="logo-wrapper">
            <n-icon size="64" color="#fff"><BookOutline /></n-icon>
          </div>
          <h1>二手书交易平台</h1>
          <p class="slogan">让闲置书籍流动起来，让知识传递更远</p>
          <div class="features">
            <div class="feature-item">
              <n-icon size="24"><CheckmarkCircleOutline /></n-icon>
              <span>海量二手书籍资源</span>
            </div>
            <div class="feature-item">
              <n-icon size="24"><ShieldCheckmarkOutline /></n-icon>
              <span>安全可靠的交易保障</span>
            </div>
            <div class="feature-item">
              <n-icon size="24"><PeopleOutline /></n-icon>
              <span>活跃的读书爱好者社区</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 右侧登录表单 -->
      <div class="form-section">
        <n-card class="login-card" :bordered="false">
          <div class="form-header">
            <h2>欢迎回来</h2>
            <p>登录您的账号，开始探索书籍世界</p>
          </div>
          
          <n-form ref="loginFormRef" :model="loginForm" :rules="loginRules" size="large">
            <n-form-item path="username">
              <n-input v-model:value="loginForm.username" placeholder="请输入用户名" clearable>
                <template #prefix><n-icon :component="PersonOutline" /></template>
              </n-input>
            </n-form-item>
            <n-form-item path="password">
              <n-input v-model:value="loginForm.password" type="password" show-password-on="click"
                       placeholder="请输入密码" @keyup.enter="handleLogin">
                <template #prefix><n-icon :component="LockClosedOutline" /></template>
              </n-input>
            </n-form-item>
            
            <div class="form-options">
              <n-checkbox v-model:checked="rememberMe">记住我</n-checkbox>
              <n-button text type="primary" size="small">忘记密码？</n-button>
            </div>
            
            <n-button type="primary" block size="large" :loading="loading" @click="handleLogin">
              登 录
            </n-button>
          </n-form>
          
          <n-divider>
            <span class="divider-text">还没有账号？</span>
          </n-divider>
          
          <n-button block size="large" secondary @click="router.push('/register')">
            立即注册
          </n-button>
        </n-card>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useMessage, FormInst } from 'naive-ui'
import { BookOutline, PersonOutline, LockClosedOutline, CheckmarkCircleOutline, 
         ShieldCheckmarkOutline, PeopleOutline } from '@vicons/ionicons5'
import { authApi } from '@/service/auth'
import { useUserStore } from '@/store/user'
import type { LoginForm } from '@/types'

const router = useRouter()
const route = useRoute()
const message = useMessage()
const userStore = useUserStore()

const loading = ref(false)
const rememberMe = ref(false)
const loginFormRef = ref<FormInst | null>(null)

const loginForm = ref<LoginForm>({ username: '', password: '' })

const loginRules = {
  username: { required: true, message: '请输入用户名', trigger: 'blur' },
  password: { required: true, message: '请输入密码', trigger: 'blur' }
}

async function handleLogin() {
  try {
    await loginFormRef.value?.validate()
  } catch { return }
  
  loading.value = true
  try {
    const res = await authApi.login(loginForm.value)
    userStore.login(res.data.token, res.data.user)
    message.success('登录成功，欢迎回来！')
    const redirect = route.query.redirect as string
    router.push(redirect || '/home')
  } catch (e) {
    // 错误已在拦截器中处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  position: relative;
  overflow: hidden;
}

.bg-decoration {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(24, 160, 88, 0.3), rgba(32, 128, 240, 0.2));
  filter: blur(60px);
}

.circle-1 { width: 400px; height: 400px; top: -100px; right: -100px; }
.circle-2 { width: 300px; height: 300px; bottom: -50px; left: -50px; }
.circle-3 { width: 200px; height: 200px; top: 50%; left: 30%; }

.login-container {
  display: flex;
  width: 100%;
  max-width: 1000px;
  min-height: 600px;
  margin: 20px;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
}

.brand-section {
  flex: 1;
  padding: 48px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  background: linear-gradient(135deg, #18a058 0%, #2080f0 100%);
  color: #fff;
}

.brand-content { max-width: 360px; }

.logo-wrapper {
  width: 100px;
  height: 100px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
}

.brand-section h1 {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 12px;
}

.slogan {
  font-size: 16px;
  opacity: 0.9;
  margin: 0 0 32px;
  line-height: 1.6;
}

.features { display: flex; flex-direction: column; gap: 16px; }

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 15px;
  opacity: 0.9;
}

.form-section {
  flex: 1;
  padding: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
}

.login-card {
  width: 100%;
  max-width: 380px;
  background: transparent;
}

.form-header {
  text-align: center;
  margin-bottom: 32px;
}

.form-header h2 {
  font-size: 28px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 8px;
}

.form-header p {
  color: #666;
  margin: 0;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.divider-text {
  color: #999;
  font-size: 13px;
}

@media (max-width: 768px) {
  .login-container { flex-direction: column; margin: 0; border-radius: 0; min-height: 100vh; }
  .brand-section { padding: 32px; min-height: auto; }
  .brand-section h1 { font-size: 24px; }
  .features { display: none; }
  .form-section { padding: 32px 24px; }
}
</style>
