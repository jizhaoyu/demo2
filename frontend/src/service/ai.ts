import { request } from '@/utils/request'

export interface ChatMessage {
  role: 'user' | 'assistant'
  content: string
}

export interface AiChatRequest {
  message: string
  sessionId?: string
  history?: ChatMessage[]
  currentPage?: string
}

export interface AiChatResponse {
  reply: string
  sessionId: string
  success: boolean
  error?: string
}

export const aiApi = {
  /**
   * AI对话
   */
  chat(data: AiChatRequest) {
    return request.post<AiChatResponse>('/ai/chat', data)
  }
}
