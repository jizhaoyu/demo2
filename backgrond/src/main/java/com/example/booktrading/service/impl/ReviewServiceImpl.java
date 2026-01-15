package com.example.booktrading.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.booktrading.constant.OrderStatus;
import com.example.booktrading.constant.ResultCode;
import com.example.booktrading.entity.dto.ReviewDTO;
import com.example.booktrading.entity.po.Orders;
import com.example.booktrading.entity.po.Review;
import com.example.booktrading.entity.po.User;
import com.example.booktrading.entity.vo.ReviewVO;
import com.example.booktrading.exception.BusinessException;
import com.example.booktrading.mapper.OrderMapper;
import com.example.booktrading.mapper.ReviewMapper;
import com.example.booktrading.mapper.UserMapper;
import com.example.booktrading.service.ReviewService;
import com.example.booktrading.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 评价服务实现
 */
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    
    private final ReviewMapper reviewMapper;
    private final OrderMapper orderMapper;
    private final UserMapper userMapper;
    
    @Override
    @Transactional
    public void create(ReviewDTO dto) {
        Long reviewerId = UserContext.getUserId();
        
        // 查询订单
        Orders order = orderMapper.selectById(dto.getOrderId());
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        
        // 验证订单状态
        if (!OrderStatus.COMPLETED.getCode().equals(order.getStatus())) {
            throw new BusinessException(ResultCode.REVIEW_ORDER_NOT_COMPLETED);
        }
        
        // 验证评价者是买家或卖家
        Long revieweeId;
        if (order.getBuyerId().equals(reviewerId)) {
            revieweeId = order.getSellerId();
        } else if (order.getSellerId().equals(reviewerId)) {
            revieweeId = order.getBuyerId();
        } else {
            throw new BusinessException(ResultCode.ORDER_NOT_BUYER_OR_SELLER);
        }
        
        // 检查是否已评价
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getOrderId, dto.getOrderId()).eq(Review::getReviewerId, reviewerId);
        if (reviewMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.REVIEW_EXISTS);
        }
        
        // 创建评价
        Review review = new Review();
        review.setOrderId(dto.getOrderId());
        review.setReviewerId(reviewerId);
        review.setRevieweeId(revieweeId);
        review.setRating(dto.getRating());
        review.setContent(dto.getContent());
        
        reviewMapper.insert(review);
        
        // 更新被评价用户的平均分
        updateUserRating(revieweeId);
    }
    
    @Override
    public IPage<ReviewVO> listByUserId(Long userId, Integer page, Integer size) {
        Page<Review> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getRevieweeId, userId).orderByDesc(Review::getCreateTime);
        
        IPage<Review> reviewPage = reviewMapper.selectPage(pageParam, wrapper);
        return reviewPage.convert(this::convertToVO);
    }
    
    @Override
    public List<ReviewVO> listByOrderId(Long orderId) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getOrderId, orderId);
        
        List<Review> reviews = reviewMapper.selectList(wrapper);
        return reviews.stream().map(this::convertToVO).collect(Collectors.toList());
    }
    
    /**
     * 更新用户平均评分
     */
    private void updateUserRating(Long userId) {
        BigDecimal avgRating = reviewMapper.calculateAvgRating(userId);
        int ratingCount = reviewMapper.countByRevieweeId(userId);
        
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setAvgRating(avgRating != null ? avgRating : BigDecimal.ZERO);
            user.setRatingCount(ratingCount);
            userMapper.updateById(user);
        }
    }
    
    private ReviewVO convertToVO(Review review) {
        ReviewVO vo = new ReviewVO();
        BeanUtils.copyProperties(review, vo);
        
        // 评价者信息
        User reviewer = userMapper.selectById(review.getReviewerId());
        if (reviewer != null) {
            vo.setReviewerName(reviewer.getUsername());
            vo.setReviewerAvatar(reviewer.getAvatar());
        }
        
        // 被评价者信息
        User reviewee = userMapper.selectById(review.getRevieweeId());
        if (reviewee != null) {
            vo.setRevieweeName(reviewee.getUsername());
        }
        
        return vo;
    }
}
