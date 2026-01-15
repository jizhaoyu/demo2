// 通用响应类型
export interface Result<T = any> {
  code: number
  msg: string
  data: T
  timestamp: number
  requestId: string
}

// 分页响应
export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

// 用户相关
export interface User {
  id: number
  username: string
  email: string
  phone?: string
  avatar?: string
  role: number
  status: number
  avgRating: number
  ratingCount: number
  createTime: string
}

export interface LoginForm {
  username: string
  password: string
}

export interface RegisterForm {
  username: string
  password: string
  email: string
  phone?: string
}

export interface LoginResult {
  token: string
  user: User
}

// 书籍相关
export interface Book {
  id: number
  title: string
  author: string
  price: number
  originalPrice?: number
  conditionLevel: number
  conditionDesc?: string
  description?: string
  images?: string
  status: number
  statusDesc?: string
  userId?: number
  categoryId: number
  viewCount: number
  createTime: string
  updateTime?: string
  // 关联信息
  categoryName?: string
  sellerId?: number
  sellerName?: string
  sellerAvatar?: string
  sellerRating?: number
  sellerRatingCount?: number
}

export interface BookForm {
  title: string
  author: string
  price: number
  originalPrice?: number
  conditionLevel: number
  description?: string
  images?: string
  categoryId: number
}

export interface BookQuery {
  page?: number
  size?: number
  keyword?: string
  categoryId?: number
  minPrice?: number
  maxPrice?: number
  status?: number
  sortBy?: string
  sortOrder?: string
}

// 分类
export interface Category {
  id: number
  name: string
  sort: number
}

// 订单相关
export interface Order {
  id: number
  orderNo: string
  bookId: number
  buyerId: number
  sellerId: number
  price: number
  status: number
  statusDesc: string
  shippingAddress?: string
  shippingInfo?: string
  cancelReason?: string
  createTime: string
  updateTime: string
  // 关联信息
  bookTitle?: string
  bookAuthor?: string
  bookImages?: string
  buyerName?: string
  buyerAvatar?: string
  sellerName?: string
  sellerAvatar?: string
  hasReviewed?: boolean
}

export interface OrderForm {
  bookId: number
  shippingAddress: string
}

// 收藏
export interface Favorite {
  id: number
  userId: number
  bookId: number
  createTime: string
}

// 评价
export interface Review {
  id: number
  orderId: number
  reviewerId: number
  revieweeId: number
  rating: number
  content?: string
  createTime: string
  // 关联信息
  reviewerName?: string
  reviewerAvatar?: string
}

export interface ReviewForm {
  orderId: number
  rating: number
  content?: string
}

// 消息
export interface Message {
  id: number
  senderId: number
  receiverId: number
  content: string
  isRead: number
  createTime: string
}

export interface Conversation {
  userId: number
  username: string
  avatar?: string
  lastMessage: string
  lastTime: string
  unreadCount: number
}

export interface MessageForm {
  receiverId: number
  content: string
}

// 统计数据
export interface Statistics {
  userCount: number
  bookCount: number
  orderCount: number
  completedOrderCount: number
}
