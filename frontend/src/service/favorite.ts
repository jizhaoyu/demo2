import { request } from '@/utils/request'
import type { Book, PageResult } from '@/types'

export const favoriteApi = {
  // 获取收藏列表
  list(page: number = 1, size: number = 10) {
    return request.get<PageResult<Book>>('/favorite/list', { params: { page, size } })
  },
  
  // 添加收藏
  add(bookId: number) {
    return request.post<void>(`/favorite/${bookId}`)
  },
  
  // 取消收藏
  remove(bookId: number) {
    return request.delete<void>(`/favorite/${bookId}`)
  },
  
  // 检查是否已收藏
  check(bookId: number) {
    return request.get<boolean>(`/favorite/check/${bookId}`)
  }
}
