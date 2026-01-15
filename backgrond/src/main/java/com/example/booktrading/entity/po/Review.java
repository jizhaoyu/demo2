package com.example.booktrading.entity.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评价实体类
 */
@Data
@TableName("review")
public class Review implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long orderId;
    
    private Long reviewerId;
    
    private Long revieweeId;
    
    /**
     * 评分：1-5星
     */
    private Integer rating;
    
    private String content;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
