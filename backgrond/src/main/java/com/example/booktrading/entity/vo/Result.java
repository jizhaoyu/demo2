package com.example.booktrading.entity.vo;

import com.example.booktrading.constant.ResultCode;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

/**
 * 统一响应结果类
 */
@Data
public class Result<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 状态码
     */
    private Integer code;
    
    /**
     * 提示信息
     */
    private String msg;
    
    /**
     * 业务数据
     */
    private T data;
    
    /**
     * 时间戳
     */
    private Long timestamp;
    
    /**
     * 请求ID
     */
    private String requestId;
    
    public Result() {
        this.timestamp = System.currentTimeMillis();
        this.requestId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
    
    public Result(Integer code, String msg, T data) {
        this();
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    
    /**
     * 成功响应
     */
    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), null);
    }
    
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), data);
    }
    
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), msg, data);
    }
    
    /**
     * 失败响应
     */
    public static <T> Result<T> error(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMsg(), null);
    }
    
    public static <T> Result<T> error(ResultCode resultCode, String msg) {
        return new Result<>(resultCode.getCode(), msg, null);
    }
    
    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }
}
