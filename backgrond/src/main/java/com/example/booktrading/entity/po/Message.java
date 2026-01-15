package com.example.booktrading.entity.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息实体类
 */
@Data
@TableName("message")
public class Message implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long senderId;
    
    private Long receiverId;
    
    private String content;
    
    /**
     * 是否已读：0-未读，1-已读
     */
    private Integer isRead;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 发送者是否删除
     */
    private Integer deletedBySender;
    
    /**
     * 接收者是否删除
     */
    private Integer deletedByReceiver;
}
