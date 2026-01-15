package com.example.booktrading.service;

import com.example.booktrading.entity.po.Category;
import com.example.booktrading.entity.vo.CategoryVO;

import java.util.List;

/**
 * 分类服务接口
 */
public interface CategoryService {
    
    /**
     * 获取分类列表
     */
    List<Category> list();
    
    /**
     * 获取分类列表（包含书籍数量）
     */
    List<CategoryVO> listWithBookCount();
    
    /**
     * 创建分类
     */
    void create(Category category);
    
    /**
     * 更新分类
     */
    void update(Long id, Category category);
    
    /**
     * 删除分类
     */
    void delete(Long id);
}
