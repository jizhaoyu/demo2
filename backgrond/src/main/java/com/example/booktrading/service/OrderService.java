package com.example.booktrading.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.booktrading.entity.dto.OrderDTO;
import com.example.booktrading.entity.vo.OrderVO;

/**
 * 订单服务接口
 */
public interface OrderService {
    
    /**
     * 创建订单
     */
    Long create(OrderDTO dto);
    
    /**
     * 获取订单详情
     */
    OrderVO getById(Long id);
    
    /**
     * 获取订单列表
     */
    IPage<OrderVO> list(Integer page, Integer size, String type);
    
    /**
     * 卖家确认订单
     */
    void confirm(Long id);
    
    /**
     * 买家支付
     */
    void pay(Long id);
    
    /**
     * 卖家发货
     */
    void ship(Long id, String shippingInfo);
    
    /**
     * 买家确认收货
     */
    void receive(Long id);
    
    /**
     * 取消订单
     */
    void cancel(Long id, String reason);
}
