package com.example.booktrading.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.booktrading.entity.po.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 书籍Mapper
 */
@Mapper
public interface BookMapper extends BaseMapper<Book> {
    
    /**
     * 统计分类下的书籍数量
     */
    @Select("SELECT COUNT(*) FROM book WHERE category_id = #{categoryId} AND deleted = 0")
    int countByCategoryId(@Param("categoryId") Long categoryId);
}
