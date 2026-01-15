package com.example.booktrading.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评价VO
 */
@Data
public class ReviewVO {
    
    private Long id;
    private Long orderId;
    private Integer rating;
    private String content;
    private LocalDateTime createTime;
    
    // 评价者信息
    private Long reviewerId;
    private String reviewerName;
    private String reviewerAvatar;
    
    // 被评价者信息
    private Long revieweeId;
    private String revieweeName;
}
