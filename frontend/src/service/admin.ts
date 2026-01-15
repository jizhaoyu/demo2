import { request } from '@/utils/request'
import type { User, Book, Statistics, PageResult } from '@/types'

export const adminApi = {
  // 获取用户列表
  getUsers(params?: { page?: number; size?: number; keyword?: string; status?: number }) {
    return request.get<PageResult<User>>('/admin/users', { params })
  },
  
  // 更新用户状态
  updateUserStatus(id: number, status: number) {
    return request.put<void>(`/admin/user/${id}/status`, null, { params: { status } })
  },
  
  // 获取系统统计数据
  getStatistics() {
    return request.get<Statistics>('/admin/statistics')
  },
  
  // 获取书籍列表（管理员）
  getBookList(page: number = 1, size: number = 10, keyword?: string, status?: number) {
    return request.get<PageResult<Book>>('/admin/books', { params: { page, size, keyword, status } })
  },
  
  // 删除书籍（管理员）
  deleteBook(id: number) {
    return request.delete<void>(`/admin/book/${id}`)
  }
}
