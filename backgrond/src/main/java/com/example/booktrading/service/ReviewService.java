package com.example.booktrading.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.booktrading.entity.dto.ReviewDTO;
import com.example.booktrading.entity.vo.ReviewVO;

import java.util.List;

/**
 * 评价服务接口
 */
public interface ReviewService {
    
    /**
     * 提交评价
     */
    void create(ReviewDTO dto);
    
    /**
     * 获取用户收到的评价列表
     */
    IPage<ReviewVO> listByUserId(Long userId, Integer page, Integer size);
    
    /**
     * 获取订单的评价
     */
    List<ReviewVO> listByOrderId(Long orderId);
}
