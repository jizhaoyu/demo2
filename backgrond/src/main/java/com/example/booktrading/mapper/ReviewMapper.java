package com.example.booktrading.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.booktrading.entity.po.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

/**
 * 评价Mapper
 */
@Mapper
public interface ReviewMapper extends BaseMapper<Review> {
    
    /**
     * 计算用户平均评分
     */
    @Select("SELECT AVG(rating) FROM review WHERE reviewee_id = #{userId}")
    BigDecimal calculateAvgRating(@Param("userId") Long userId);
    
    /**
     * 统计用户评价数量
     */
    @Select("SELECT COUNT(*) FROM review WHERE reviewee_id = #{userId}")
    int countByRevieweeId(@Param("userId") Long userId);
}
