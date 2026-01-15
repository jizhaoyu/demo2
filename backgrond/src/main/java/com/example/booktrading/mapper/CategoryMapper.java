package com.example.booktrading.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.booktrading.entity.po.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分类Mapper
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
    
}
