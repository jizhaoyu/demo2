package com.example.booktrading.service;

import com.example.booktrading.entity.dto.AiChatDTO;
import com.example.booktrading.entity.vo.AiChatVO;

/**
 * AI智能助手服务接口
 */
public interface AiService {
    
    /**
     * AI对话
     * @param dto 对话请求
     * @return AI回复
     */
    AiChatVO chat(AiChatDTO dto);
    
    /**
     * 初始化知识库（从数据库加载数据）
     */
    void initKnowledgeBase();
}
