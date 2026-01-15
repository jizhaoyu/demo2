import { request } from '@/utils/request'
import type { Book, BookForm, BookQuery, PageResult } from '@/types'

// 清理对象中的 undefined 和 null 值
function cleanParams(obj: Record<string, any>): Record<string, any> {
  const result: Record<string, any> = {}
  for (const key in obj) {
    if (obj[key] !== undefined && obj[key] !== null && obj[key] !== '') {
      result[key] = obj[key]
    }
  }
  return result
}

export const bookApi = {
  // 获取书籍列表
  list(params: BookQuery) {
    return request.get<PageResult<Book>>('/book/list', { params: cleanParams(params) })
  },
  
  // 获取书籍详情
  getById(id: number) {
    return request.get<Book>(`/book/${id}`)
  },
  
  // 发布书籍
  create(data: BookForm) {
    return request.post<number>('/book', data)
  },
  
  // 编辑书籍
  update(id: number, data: BookForm) {
    return request.put<void>(`/book/${id}`, data)
  },
  
  // 删除书籍
  delete(id: number) {
    return request.delete<void>(`/book/${id}`)
  },
  
  // 更新书籍状态
  updateStatus(id: number, status: number) {
    return request.put<void>(`/book/${id}/status`, null, { params: { status } })
  },
  
  // 获取我的书籍列表
  myBooks(page: number = 1, size: number = 10) {
    return request.get<PageResult<Book>>('/book/my', { params: { page, size } })
  }
}
