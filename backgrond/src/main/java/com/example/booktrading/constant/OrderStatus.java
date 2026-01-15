package com.example.booktrading.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单状态枚举
 */
@Getter
@AllArgsConstructor
public enum OrderStatus {
    
    PENDING(1, "待确认"),
    CONFIRMED(2, "已确认"),
    PAID(3, "已支付"),
    SHIPPED(4, "已发货"),
    COMPLETED(5, "已完成"),
    CANCELLED(6, "已取消");
    
    private final Integer code;
    private final String desc;
    
    public static OrderStatus fromCode(Integer code) {
        for (OrderStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
