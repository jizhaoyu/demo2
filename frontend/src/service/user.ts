import { request } from '@/utils/request'
import type { User } from '@/types'

export const userApi = {
  // 获取个人信息
  getProfile() {
    return request.get<User>('/user/profile')
  },
  
  // 更新个人信息
  updateProfile(data: Partial<User>) {
    return request.put<void>('/user/profile', data)
  },
  
  // 修改密码
  changePassword(data: { oldPassword: string; newPassword: string }) {
    return request.put<void>('/user/password', data)
  },
  
  // 获取用户公开信息
  getUserById(id: number) {
    return request.get<User>(`/user/${id}`)
  }
}
