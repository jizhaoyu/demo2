import { request } from '@/utils/request'
import type { Category } from '@/types'

// 分类VO（包含书籍数量）
export interface CategoryVO extends Category {
  bookCount: number
}

export const categoryApi = {
  // 获取分类列表
  getList() {
    return request.get<Category[]>('/category/list')
  },
  
  // 获取分类列表（别名）
  list() {
    return request.get<Category[]>('/category/list')
  },
  
  // 获取分类列表（包含书籍数量，管理员用）
  listWithBookCount() {
    return request.get<CategoryVO[]>('/category/list/admin')
  },
  
  // 创建分类（管理员）
  create(data: Partial<Category>) {
    return request.post<void>('/category', data)
  },
  
  // 更新分类（管理员）
  update(id: number, data: Partial<Category>) {
    return request.put<void>(`/category/${id}`, data)
  },
  
  // 删除分类（管理员）
  delete(id: number) {
    return request.delete<void>(`/category/${id}`)
  }
}
