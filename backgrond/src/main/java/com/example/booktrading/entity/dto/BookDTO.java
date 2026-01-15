package com.example.booktrading.entity.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 书籍创建/更新DTO
 */
@Data
public class BookDTO {
    
    @NotBlank(message = "书名不能为空")
    @Size(max = 200, message = "书名最多200个字符")
    private String title;
    
    @NotBlank(message = "作者不能为空")
    @Size(max = 100, message = "作者最多100个字符")
    private String author;
    
    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private BigDecimal price;
    
    @DecimalMin(value = "0.01", message = "原价必须大于0")
    private BigDecimal originalPrice;
    
    @NotNull(message = "成色不能为空")
    @Min(value = 1, message = "成色值无效")
    @Max(value = 5, message = "成色值无效")
    private Integer conditionLevel;
    
    @NotBlank(message = "描述不能为空")
    private String description;
    
    private String images;
    
    @NotNull(message = "分类不能为空")
    private Long categoryId;
}
