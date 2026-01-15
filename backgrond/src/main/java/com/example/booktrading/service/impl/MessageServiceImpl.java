package com.example.booktrading.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.booktrading.constant.ResultCode;
import com.example.booktrading.entity.dto.MessageDTO;
import com.example.booktrading.entity.po.Message;
import com.example.booktrading.entity.po.User;
import com.example.booktrading.entity.vo.ConversationVO;
import com.example.booktrading.entity.vo.MessageVO;
import com.example.booktrading.exception.BusinessException;
import com.example.booktrading.mapper.MessageMapper;
import com.example.booktrading.mapper.UserMapper;
import com.example.booktrading.service.MessageService;
import com.example.booktrading.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 消息服务实现
 */
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    
    private final MessageMapper messageMapper;
    private final UserMapper userMapper;
    
    @Override
    public void send(MessageDTO dto) {
        Long senderId = UserContext.getUserId();
        
        // 检查发送者状态
        User sender = userMapper.selectById(senderId);
        if (sender == null || sender.getStatus() == 0) {
            throw new BusinessException(ResultCode.MESSAGE_CANNOT_SEND, "您的账户已被禁用，无法发送消息");
        }
        
        // 检查接收者是否存在
        User receiver = userMapper.selectById(dto.getReceiverId());
        if (receiver == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        Message message = new Message();
        message.setSenderId(senderId);
        message.setReceiverId(dto.getReceiverId());
        message.setContent(dto.getContent());
        message.setIsRead(0);
        message.setDeletedBySender(0);
        message.setDeletedByReceiver(0);
        
        messageMapper.insert(message);
    }
    
    @Override
    public List<ConversationVO> getConversations() {
        Long userId = UserContext.getUserId();
        
        // 查询所有相关消息
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(Message::getSenderId, userId).eq(Message::getDeletedBySender, 0)
                .or(ow -> ow.eq(Message::getReceiverId, userId).eq(Message::getDeletedByReceiver, 0)));
        wrapper.orderByDesc(Message::getCreateTime);
        
        List<Message> messages = messageMapper.selectList(wrapper);
        
        // 按对方用户分组
        Map<Long, List<Message>> conversationMap = new LinkedHashMap<>();
        for (Message msg : messages) {
            Long otherUserId = msg.getSenderId().equals(userId) ? msg.getReceiverId() : msg.getSenderId();
            conversationMap.computeIfAbsent(otherUserId, k -> new ArrayList<>()).add(msg);
        }
        
        // 构建会话列表
        List<ConversationVO> conversations = new ArrayList<>();
        for (Map.Entry<Long, List<Message>> entry : conversationMap.entrySet()) {
            Long otherUserId = entry.getKey();
            List<Message> msgs = entry.getValue();
            
            User otherUser = userMapper.selectById(otherUserId);
            if (otherUser == null) continue;
            
            ConversationVO vo = new ConversationVO();
            vo.setUserId(otherUserId);
            vo.setUsername(otherUser.getUsername());
            vo.setAvatar(otherUser.getAvatar());
            
            // 最后一条消息
            Message lastMsg = msgs.get(0);
            vo.setLastMessage(lastMsg.getContent());
            vo.setLastTime(lastMsg.getCreateTime());
            
            // 未读数
            long unreadCount = msgs.stream()
                    .filter(m -> m.getReceiverId().equals(userId) && m.getIsRead() == 0)
                    .count();
            vo.setUnreadCount((int) unreadCount);
            
            conversations.add(vo);
        }
        
        return conversations;
    }
    
    @Override
    public IPage<MessageVO> getHistory(Long otherUserId, Integer page, Integer size) {
        Long userId = UserContext.getUserId();
        
        Page<Message> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(Message::getSenderId, userId).eq(Message::getReceiverId, otherUserId).eq(Message::getDeletedBySender, 0)
                .or(ow -> ow.eq(Message::getSenderId, otherUserId).eq(Message::getReceiverId, userId).eq(Message::getDeletedByReceiver, 0)));
        wrapper.orderByAsc(Message::getCreateTime);
        
        IPage<Message> messagePage = messageMapper.selectPage(pageParam, wrapper);
        return messagePage.convert(this::convertToVO);
    }
    
    @Override
    public void markAsRead(Long otherUserId) {
        Long userId = UserContext.getUserId();
        messageMapper.markAsRead(userId, otherUserId);
    }
    
    @Override
    public void deleteConversation(Long otherUserId) {
        Long userId = UserContext.getUserId();
        
        // 标记发送的消息为已删除
        LambdaUpdateWrapper<Message> senderWrapper = new LambdaUpdateWrapper<>();
        senderWrapper.eq(Message::getSenderId, userId).eq(Message::getReceiverId, otherUserId);
        senderWrapper.set(Message::getDeletedBySender, 1);
        messageMapper.update(null, senderWrapper);
        
        // 标记接收的消息为已删除
        LambdaUpdateWrapper<Message> receiverWrapper = new LambdaUpdateWrapper<>();
        receiverWrapper.eq(Message::getSenderId, otherUserId).eq(Message::getReceiverId, userId);
        receiverWrapper.set(Message::getDeletedByReceiver, 1);
        messageMapper.update(null, receiverWrapper);
    }
    
    @Override
    public int getUnreadCount() {
        Long userId = UserContext.getUserId();
        return messageMapper.countUnread(userId);
    }
    
    private MessageVO convertToVO(Message message) {
        MessageVO vo = new MessageVO();
        BeanUtils.copyProperties(message, vo);
        
        User sender = userMapper.selectById(message.getSenderId());
        if (sender != null) {
            vo.setSenderName(sender.getUsername());
            vo.setSenderAvatar(sender.getAvatar());
        }
        
        User receiver = userMapper.selectById(message.getReceiverId());
        if (receiver != null) {
            vo.setReceiverName(receiver.getUsername());
            vo.setReceiverAvatar(receiver.getAvatar());
        }
        
        return vo;
    }
}
