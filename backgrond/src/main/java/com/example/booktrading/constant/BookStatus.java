package com.example.booktrading.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 书籍状态枚举
 */
@Getter
@AllArgsConstructor
public enum BookStatus {
    
    ON_SALE(1, "在售"),
    RESERVED(2, "已预订"),
    SOLD(3, "已售"),
    OFF_SHELF(4, "下架");
    
    private final Integer code;
    private final String desc;
    
    public static BookStatus fromCode(Integer code) {
        for (BookStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
