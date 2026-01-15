package com.example.booktrading.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * AI对话请求DTO
 */
@Data
public class AiChatDTO {
    
    /**
     * 用户消息
     */
    @NotBlank(message = "消息不能为空")
    private String message;
    
    /**
     * 会话ID（用于记忆功能）
     */
    private String sessionId;
    
    /**
     * 历史消息（用于上下文）
     */
    private List<ChatMessage> history;
    
    /**
     * 当前页面路径（用于上下文感知）
     */
    private String currentPage;
    
    @Data
    public static class ChatMessage {
        private String role; // user 或 assistant
        private String content;
    }
}
