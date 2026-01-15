import { request } from '@/utils/request'
import type { Review, ReviewForm, PageResult } from '@/types'

export const reviewApi = {
  // 提交评价
  create(data: ReviewForm) {
    return request.post<void>('/review', data)
  },
  
  // 获取用户收到的评价列表
  getUserReviews(userId: number, params?: { page?: number; size?: number }) {
    return request.get<PageResult<Review>>(`/review/user/${userId}`, { params })
  },
  
  // 获取订单的评价
  listByOrderId(orderId: number) {
    return request.get<Review[]>(`/review/order/${orderId}`)
  }
}
