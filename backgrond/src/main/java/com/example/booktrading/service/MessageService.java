package com.example.booktrading.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.booktrading.entity.dto.MessageDTO;
import com.example.booktrading.entity.vo.ConversationVO;
import com.example.booktrading.entity.vo.MessageVO;

import java.util.List;

/**
 * 消息服务接口
 */
public interface MessageService {
    
    /**
     * 发送消息
     */
    void send(MessageDTO dto);
    
    /**
     * 获取会话列表
     */
    List<ConversationVO> getConversations();
    
    /**
     * 获取与某用户的聊天记录
     */
    IPage<MessageVO> getHistory(Long userId, Integer page, Integer size);
    
    /**
     * 标记消息为已读
     */
    void markAsRead(Long userId);
    
    /**
     * 删除会话
     */
    void deleteConversation(Long userId);
    
    /**
     * 获取未读消息数
     */
    int getUnreadCount();
}
