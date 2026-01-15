package com.example.booktrading.entity.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 书籍VO
 */
@Data
public class BookVO {
    
    private Long id;
    private String title;
    private String author;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer conditionLevel;
    private String conditionDesc;
    private String description;
    private String images;
    private Integer status;
    private String statusDesc;
    private Long categoryId;
    private String categoryName;
    private Integer viewCount;
    private LocalDateTime createTime;
    
    // 卖家信息
    private Long sellerId;
    private String sellerName;
    private String sellerAvatar;
    private BigDecimal sellerRating;
    private Integer sellerRatingCount;
}
