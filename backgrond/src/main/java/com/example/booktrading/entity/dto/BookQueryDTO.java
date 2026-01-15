package com.example.booktrading.entity.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 书籍查询DTO
 */
@Data
public class BookQueryDTO {
    
    /**
     * 搜索关键词（书名/作者）
     */
    private String keyword;
    
    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 最低价格
     */
    private BigDecimal minPrice;
    
    /**
     * 最高价格
     */
    private BigDecimal maxPrice;
    
    /**
     * 排序字段：price, createTime
     */
    private String sortBy;
    
    /**
     * 排序方式：asc, desc
     */
    private String sortOrder;
    
    /**
     * 页码
     */
    private Integer page = 1;
    
    /**
     * 每页条数
     */
    private Integer size = 10;
}
