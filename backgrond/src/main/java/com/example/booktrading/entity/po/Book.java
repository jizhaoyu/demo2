package com.example.booktrading.entity.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 书籍实体类
 */
@Data
@TableName("book")
public class Book implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String title;
    
    private String author;
    
    private BigDecimal price;
    
    private BigDecimal originalPrice;
    
    /**
     * 成色：1-全新，2-九成新，3-八成新，4-七成新，5-六成新及以下
     */
    private Integer conditionLevel;
    
    private String description;
    
    /**
     * 图片URL（JSON数组）
     */
    private String images;
    
    /**
     * 状态：1-在售，2-已预订，3-已售，4-下架
     */
    private Integer status;
    
    private Long userId;
    
    private Long categoryId;
    
    private Integer viewCount;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
