<template>
  <div class="register-page">
    <!-- 装饰背景 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
    </div>
    
    <div class="register-container">
      <!-- 左侧品牌区域 -->
      <div class="brand-section">
        <div class="brand-content">
          <div class="logo-wrapper">
            <n-icon size="64" color="#fff"><BookOutline /></n-icon>
          </div>
          <h1>加入我们</h1>
          <p class="slogan">创建账号，开启您的二手书交易之旅</p>
          <div class="stats">
            <div class="stat-item">
              <span class="stat-value">10,000+</span>
              <span class="stat-label">活跃用户</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">50,000+</span>
              <span class="stat-label">在售书籍</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">98%</span>
              <span class="stat-label">好评率</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 右侧注册表单 -->
      <div class="form-section">
        <n-card class="register-card" :bordered="false">
          <div class="form-header">
            <h2>创建账号</h2>
            <p>填写以下信息完成注册</p>
          </div>
          
          <n-form ref="formRef" :model="form" :rules="rules" size="large">
            <n-form-item path="username">
              <n-input v-model:value="form.username" placeholder="用户名（3-20个字符）" clearable>
                <template #prefix><n-icon :component="PersonOutline" /></template>
              </n-input>
            </n-form-item>
            
            <n-form-item path="email">
              <n-input v-model:value="form.email" placeholder="邮箱地址" clearable>
                <template #prefix><n-icon :component="MailOutline" /></template>
              </n-input>
            </n-form-item>
            
            <n-form-item path="phone">
              <n-input v-model:value="form.phone" placeholder="手机号（选填）" clearable>
                <template #prefix><n-icon :component="CallOutline" /></template>
              </n-input>
            </n-form-item>
            
            <n-form-item path="password">
              <n-input v-model:value="form.password" type="password" show-password-on="click"
                       placeholder="密码（6-20个字符）">
                <template #prefix><n-icon :component="LockClosedOutline" /></template>
              </n-input>
            </n-form-item>
            
            <n-form-item path="confirmPassword">
              <n-input v-model:value="form.confirmPassword" type="password" show-password-on="click"
                       placeholder="确认密码">
                <template #prefix><n-icon :component="LockClosedOutline" /></template>
              </n-input>
            </n-form-item>
            
            <n-form-item>
              <n-checkbox v-model:checked="agreeTerms">
                我已阅读并同意 <n-button text type="primary" size="small">用户协议</n-button> 和 
                <n-button text type="primary" size="small">隐私政策</n-button>
              </n-checkbox>
            </n-form-item>
            
            <n-button type="primary" block size="large" :loading="loading" :disabled="!agreeTerms"
                      @click="handleRegister">
              立即注册
            </n-button>
          </n-form>
          
          <n-divider>
            <span class="divider-text">已有账号？</span>
          </n-divider>
          
          <n-button block size="large" secondary @click="router.push('/login')">
            返回登录
          </n-button>
        </n-card>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage, FormInst, FormRules } from 'naive-ui'
import { BookOutline, PersonOutline, MailOutline, CallOutline, LockClosedOutline } from '@vicons/ionicons5'
import { authApi } from '@/service/auth'

const router = useRouter()
const message = useMessage()

const loading = ref(false)
const agreeTerms = ref(false)
const formRef = ref<FormInst | null>(null)

const form = ref({
  username: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: ''
})

const rules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为3-20个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (_rule: any, value: string) => value === form.value.password,
      message: '两次输入的密码不一致',
      trigger: 'blur'
    }
  ]
}

async function handleRegister() {
  try {
    await formRef.value?.validate()
  } catch { return }
  
  if (!agreeTerms.value) {
    message.warning('请先同意用户协议和隐私政策')
    return
  }
  
  loading.value = true
  try {
    await authApi.register({
      username: form.value.username,
      email: form.value.email,
      phone: form.value.phone || undefined,
      password: form.value.password
    })
    message.success('注册成功，请登录')
    router.push('/login')
  } catch (e) {
    // 错误已在拦截器中处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
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

.circle-1 { width: 400px; height: 400px; top: -100px; left: -100px; }
.circle-2 { width: 300px; height: 300px; bottom: -50px; right: -50px; }

.register-container {
  display: flex;
  width: 100%;
  max-width: 1000px;
  min-height: 700px;
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
  background: linear-gradient(135deg, #2080f0 0%, #18a058 100%);
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
  margin: 0 0 40px;
  line-height: 1.6;
}

.stats {
  display: flex;
  gap: 32px;
}

.stat-item {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
}

.stat-label {
  font-size: 14px;
  opacity: 0.8;
}

.form-section {
  flex: 1;
  padding: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  overflow-y: auto;
}

.register-card {
  width: 100%;
  max-width: 400px;
  background: transparent;
}

.form-header {
  text-align: center;
  margin-bottom: 24px;
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

.divider-text {
  color: #999;
  font-size: 13px;
}

@media (max-width: 768px) {
  .register-container { flex-direction: column; margin: 0; border-radius: 0; min-height: 100vh; }
  .brand-section { padding: 32px; min-height: auto; }
  .brand-section h1 { font-size: 24px; }
  .stats { gap: 20px; }
  .stat-value { font-size: 20px; }
  .form-section { padding: 32px 24px; }
}
</style>
