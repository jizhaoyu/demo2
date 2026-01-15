package com.example.booktrading.entity.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单VO
 */
@Data
public class OrderVO {
    
    private Long id;
    private String orderNo;
    private BigDecimal price;
    private Integer status;
    private String statusDesc;
    private String shippingAddress;
    private String shippingInfo;
    private String cancelReason;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 书籍信息
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookImages;
    
    // 买家信息
    private Long buyerId;
    private String buyerName;
    private String buyerAvatar;
    
    // 卖家信息
    private Long sellerId;
    private String sellerName;
    private String sellerAvatar;
    
    // 是否已评价
    private Boolean hasReviewed;
}
