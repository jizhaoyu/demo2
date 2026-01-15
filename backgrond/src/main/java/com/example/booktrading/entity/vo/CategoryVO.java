package com.example.booktrading.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分类VO（包含书籍数量）
 */
@Data
public class CategoryVO {
    
    private Long id;
    private String name;
    private Integer sort;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    /** 该分类下的书籍数量 */
    private Integer bookCount;
}
