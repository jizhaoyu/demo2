package com.example.booktrading.entity.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@Data
@TableName("orders")
public class Orders implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 订单编号
     */
    private String orderNo;
    
    private Long bookId;
    
    private Long buyerId;
    
    private Long sellerId;
    
    private BigDecimal price;
    
    /**
     * 状态：1-待确认，2-已确认，3-已支付，4-已发货，5-已完成，6-已取消
     */
    private Integer status;
    
    private String shippingAddress;
    
    private String shippingInfo;
    
    private String cancelReason;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
