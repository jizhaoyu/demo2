package com.example.booktrading.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.booktrading.entity.po.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单Mapper
 */
@Mapper
public interface OrderMapper extends BaseMapper<Orders> {
    
}
