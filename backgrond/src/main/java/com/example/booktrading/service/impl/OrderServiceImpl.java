package com.example.booktrading.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.booktrading.constant.BookStatus;
import com.example.booktrading.constant.OrderStatus;
import com.example.booktrading.constant.ResultCode;
import com.example.booktrading.entity.dto.OrderDTO;
import com.example.booktrading.entity.po.Book;
import com.example.booktrading.entity.po.Orders;
import com.example.booktrading.entity.po.Review;
import com.example.booktrading.entity.po.User;
import com.example.booktrading.entity.vo.OrderVO;
import com.example.booktrading.exception.BusinessException;
import com.example.booktrading.mapper.BookMapper;
import com.example.booktrading.mapper.OrderMapper;
import com.example.booktrading.mapper.ReviewMapper;
import com.example.booktrading.mapper.UserMapper;
import com.example.booktrading.service.OrderService;
import com.example.booktrading.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 订单服务实现
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    
    private final OrderMapper orderMapper;
    private final BookMapper bookMapper;
    private final UserMapper userMapper;
    private final ReviewMapper reviewMapper;
    
    @Override
    @Transactional
    public Long create(OrderDTO dto) {
        Long buyerId = UserContext.getUserId();
        
        // 查询书籍
        Book book = bookMapper.selectById(dto.getBookId());
        if (book == null) {
            throw new BusinessException(ResultCode.BOOK_NOT_FOUND);
        }
        
        // 不能购买自己的书
        if (book.getUserId().equals(buyerId)) {
            throw new BusinessException(ResultCode.ORDER_CANNOT_BUY_OWN);
        }
        
        // 只能购买在售书籍
        if (!BookStatus.ON_SALE.getCode().equals(book.getStatus())) {
            throw new BusinessException(ResultCode.BOOK_NOT_ON_SALE);
        }
        
        // 创建订单
        Orders order = new Orders();
        order.setOrderNo(IdUtil.getSnowflakeNextIdStr());
        order.setBookId(book.getId());
        order.setBuyerId(buyerId);
        order.setSellerId(book.getUserId());
        order.setPrice(book.getPrice());
        order.setStatus(OrderStatus.PENDING.getCode());
        order.setShippingAddress(dto.getShippingAddress());
        
        orderMapper.insert(order);
        
        // 更新书籍状态为已预订
        book.setStatus(BookStatus.RESERVED.getCode());
        bookMapper.updateById(book);
        
        return order.getId();
    }
    
    @Override
    public OrderVO getById(Long id) {
        Orders order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        
        // 验证权限
        Long userId = UserContext.getUserId();
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId) && !UserContext.isAdmin()) {
            throw new BusinessException(ResultCode.ORDER_NOT_BUYER_OR_SELLER);
        }
        
        return convertToVO(order);
    }
    
    @Override
    public IPage<OrderVO> list(Integer page, Integer size, String type) {
        Long userId = UserContext.getUserId();
        
        Page<Orders> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        
        // 根据类型筛选
        if ("buy".equals(type)) {
            wrapper.eq(Orders::getBuyerId, userId);
        } else if ("sell".equals(type)) {
            wrapper.eq(Orders::getSellerId, userId);
        } else {
            wrapper.and(w -> w.eq(Orders::getBuyerId, userId).or().eq(Orders::getSellerId, userId));
        }
        
        wrapper.orderByDesc(Orders::getCreateTime);
        
        IPage<Orders> orderPage = orderMapper.selectPage(pageParam, wrapper);
        return orderPage.convert(this::convertToVO);
    }
    
    @Override
    @Transactional
    public void confirm(Long id) {
        Orders order = getOrderAndCheckSeller(id);
        
        if (!OrderStatus.PENDING.getCode().equals(order.getStatus())) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR);
        }
        
        order.setStatus(OrderStatus.CONFIRMED.getCode());
        orderMapper.updateById(order);
    }
    
    @Override
    @Transactional
    public void pay(Long id) {
        Orders order = getOrderAndCheckBuyer(id);
        
        if (!OrderStatus.CONFIRMED.getCode().equals(order.getStatus())) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR);
        }
        
        order.setStatus(OrderStatus.PAID.getCode());
        orderMapper.updateById(order);
    }
    
    @Override
    @Transactional
    public void ship(Long id, String shippingInfo) {
        Orders order = getOrderAndCheckSeller(id);
        
        if (!OrderStatus.PAID.getCode().equals(order.getStatus())) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR);
        }
        
        order.setStatus(OrderStatus.SHIPPED.getCode());
        order.setShippingInfo(shippingInfo);
        orderMapper.updateById(order);
    }
    
    @Override
    @Transactional
    public void receive(Long id) {
        Orders order = getOrderAndCheckBuyer(id);
        
        if (!OrderStatus.SHIPPED.getCode().equals(order.getStatus())) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR);
        }
        
        order.setStatus(OrderStatus.COMPLETED.getCode());
        orderMapper.updateById(order);
        
        // 更新书籍状态为已售
        Book book = bookMapper.selectById(order.getBookId());
        if (book != null) {
            book.setStatus(BookStatus.SOLD.getCode());
            bookMapper.updateById(book);
        }
    }
    
    @Override
    @Transactional
    public void cancel(Long id, String reason) {
        Orders order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        
        Long userId = UserContext.getUserId();
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            throw new BusinessException(ResultCode.ORDER_NOT_BUYER_OR_SELLER);
        }
        
        // 只能在发货前取消
        if (order.getStatus() >= OrderStatus.SHIPPED.getCode()) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR, "已发货订单无法取消");
        }
        
        order.setStatus(OrderStatus.CANCELLED.getCode());
        order.setCancelReason(reason);
        orderMapper.updateById(order);
        
        // 恢复书籍状态为在售
        Book book = bookMapper.selectById(order.getBookId());
        if (book != null) {
            book.setStatus(BookStatus.ON_SALE.getCode());
            bookMapper.updateById(book);
        }
    }
    
    private Orders getOrderAndCheckBuyer(Long id) {
        Orders order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        if (!order.getBuyerId().equals(UserContext.getUserId())) {
            throw new BusinessException(ResultCode.ORDER_NOT_BUYER_OR_SELLER, "只有买家才能执行此操作");
        }
        return order;
    }
    
    private Orders getOrderAndCheckSeller(Long id) {
        Orders order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        if (!order.getSellerId().equals(UserContext.getUserId())) {
            throw new BusinessException(ResultCode.ORDER_NOT_BUYER_OR_SELLER, "只有卖家才能执行此操作");
        }
        return order;
    }
    
    private OrderVO convertToVO(Orders order) {
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);
        
        OrderStatus status = OrderStatus.fromCode(order.getStatus());
        vo.setStatusDesc(status != null ? status.getDesc() : "未知");
        
        // 书籍信息
        Book book = bookMapper.selectById(order.getBookId());
        if (book != null) {
            vo.setBookTitle(book.getTitle());
            vo.setBookAuthor(book.getAuthor());
            vo.setBookImages(book.getImages());
        }
        
        // 买家信息
        User buyer = userMapper.selectById(order.getBuyerId());
        if (buyer != null) {
            vo.setBuyerName(buyer.getUsername());
            vo.setBuyerAvatar(buyer.getAvatar());
        }
        
        // 卖家信息
        User seller = userMapper.selectById(order.getSellerId());
        if (seller != null) {
            vo.setSellerName(seller.getUsername());
            vo.setSellerAvatar(seller.getAvatar());
        }
        
        // 检查当前用户是否已评价
        Long userId = UserContext.getUserId();
        if (userId != null && OrderStatus.COMPLETED.getCode().equals(order.getStatus())) {
            LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Review::getOrderId, order.getId()).eq(Review::getReviewerId, userId);
            vo.setHasReviewed(reviewMapper.selectCount(wrapper) > 0);
        } else {
            vo.setHasReviewed(false);
        }
        
        return vo;
    }
}
