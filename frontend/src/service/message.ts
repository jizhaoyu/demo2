import { request } from '@/utils/request'
import type { Message, Conversation, MessageForm, PageResult } from '@/types'

export const messageApi = {
  // 获取会话列表
  getConversations() {
    return request.get<Conversation[]>('/message/conversations')
  },
  
  // 获取与某用户的聊天记录
  getHistory(userId: number, page: number = 1, size: number = 50) {
    return request.get<PageResult<Message>>(`/message/history/${userId}`, {
      params: { page, size }
    })
  },
  
  // 发送消息
  send(data: MessageForm) {
    return request.post<void>('/message', data)
  },
  
  // 标记消息为已读
  markRead(userId: number) {
    return request.put<void>(`/message/read/${userId}`)
  },
  
  // 删除会话
  deleteConversation(userId: number) {
    return request.delete<void>(`/message/conversation/${userId}`)
  },
  
  // 获取未读消息数
  getUnreadCount() {
    return request.get<number>('/message/unread-count')
  }
}
