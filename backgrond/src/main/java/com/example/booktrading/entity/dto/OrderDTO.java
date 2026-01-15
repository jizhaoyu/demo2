package com.example.booktrading.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 订单创建DTO
 */
@Data
public class OrderDTO {
    
    @NotNull(message = "书籍ID不能为空")
    private Long bookId;
    
    @NotBlank(message = "收货地址不能为空")
    private String shippingAddress;
}
