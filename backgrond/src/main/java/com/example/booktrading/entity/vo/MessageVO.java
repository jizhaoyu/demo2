package com.example.booktrading.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息VO
 */
@Data
public class MessageVO {
    
    private Long id;
    private Long senderId;
    private String senderName;
    private String senderAvatar;
    private Long receiverId;
    private String receiverName;
    private String receiverAvatar;
    private String content;
    private Integer isRead;
    private LocalDateTime createTime;
}
