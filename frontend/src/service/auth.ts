import { request } from '@/utils/request'
import type { LoginForm, RegisterForm, LoginResult } from '@/types'

export const authApi = {
  // 用户注册
  register(data: RegisterForm) {
    return request.post<void>('/auth/register', data)
  },
  
  // 用户登录
  login(data: LoginForm) {
    return request.post<LoginResult>('/auth/login', data)
  },
  
  // 用户登出
  logout() {
    return request.post<void>('/auth/logout')
  }
}
