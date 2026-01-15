package com.example.booktrading.entity.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 评价DTO
 */
@Data
public class ReviewDTO {
    
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
    
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最低1星")
    @Max(value = 5, message = "评分最高5星")
    private Integer rating;
    
    @Size(max = 500, message = "评价内容最多500字")
    private String content;
}
