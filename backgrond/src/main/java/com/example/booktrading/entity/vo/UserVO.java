package com.example.booktrading.entity.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户信息VO
 */
@Data
public class UserVO {
    
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String avatar;
    private Integer role;
    private Integer status;
    private BigDecimal avgRating;
    private Integer ratingCount;
    private LocalDateTime createTime;
}
