import { request } from '@/utils/request'
import type { Order, OrderForm, PageResult } from '@/types'

export const orderApi = {
  // 获取订单列表
  getList(params?: { page?: number; size?: number; type?: string; status?: number }) {
    return request.get<PageResult<Order>>('/order/list', { params })
  },
  
  // 获取订单详情
  getDetail(id: number) {
    return request.get<Order>(`/order/${id}`)
  },
  
  // 创建订单
  create(data: OrderForm) {
    return request.post<number>('/order', data)
  },
  
  // 卖家确认订单
  confirm(id: number) {
    return request.put<void>(`/order/${id}/confirm`)
  },
  
  // 买家支付
  pay(id: number) {
    return request.put<void>(`/order/${id}/pay`)
  },
  
  // 卖家发货
  ship(id: number, shippingInfo: string) {
    return request.put<void>(`/order/${id}/ship`, null, { params: { shippingInfo } })
  },
  
  // 买家确认收货
  receive(id: number) {
    return request.put<void>(`/order/${id}/receive`)
  },
  
  // 取消订单
  cancel(id: number, reason?: string) {
    return request.put<void>(`/order/${id}/cancel`, null, { params: { reason } })
  }
}
