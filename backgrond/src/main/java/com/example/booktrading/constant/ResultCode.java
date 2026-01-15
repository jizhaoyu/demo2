package com.example.booktrading.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应状态码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {
    
    // 成功
    SUCCESS(200, "操作成功"),
    
    // 客户端错误 4xx
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或Token已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    
    // 业务错误
    USER_EXISTS(1001, "用户名或邮箱已存在"),
    USER_NOT_FOUND(1002, "用户不存在"),
    PASSWORD_ERROR(1003, "密码错误"),
    USER_DISABLED(1004, "用户已被禁用"),
    
    BOOK_NOT_FOUND(2001, "书籍不存在"),
    BOOK_NOT_ON_SALE(2002, "书籍不在售"),
    BOOK_OWNER_ONLY(2003, "只有书籍所有者才能操作"),
    
    CATEGORY_NOT_FOUND(3001, "分类不存在"),
    CATEGORY_HAS_BOOKS(3002, "分类下存在书籍，无法删除"),
    
    FAVORITE_EXISTS(4001, "已收藏该书籍"),
    FAVORITE_NOT_FOUND(4002, "收藏记录不存在"),
    
    ORDER_NOT_FOUND(5001, "订单不存在"),
    ORDER_CANNOT_BUY_OWN(5002, "不能购买自己的书籍"),
    ORDER_STATUS_ERROR(5003, "订单状态不允许此操作"),
    ORDER_NOT_BUYER_OR_SELLER(5004, "只有买家或卖家才能操作"),
    
    REVIEW_EXISTS(6001, "已评价过该订单"),
    REVIEW_ORDER_NOT_COMPLETED(6002, "订单未完成，无法评价"),
    
    MESSAGE_CANNOT_SEND(7001, "无法发送消息"),
    
    // 服务器错误 5xx
    SERVER_ERROR(500, "服务器内部错误");
    
    private final Integer code;
    private final String msg;
}
