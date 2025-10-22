package com.example.fetchbeebackend.common;

/**
 * 响应状态码
 */
public class ResultCode {
    
    // 成功
    public static final int SUCCESS = 200;
    
    // 失败
    public static final int ERROR = 500;
    
    // 参数错误
    public static final int PARAM_ERROR = 400;
    
    // 未授权
    public static final int UNAUTHORIZED = 401;
    
    // 禁止访问
    public static final int FORBIDDEN = 403;
    
    // 资源不存在
    public static final int NOT_FOUND = 404;
    
    // 用户名已存在
    public static final int USERNAME_EXISTS = 1001;
    
    // 用户不存在
    public static final int USER_NOT_FOUND = 1002;
    
    // 密码错误
    public static final int PASSWORD_ERROR = 1003;
    
    // 余额不足
    public static final int INSUFFICIENT_BALANCE = 1004;
    
    // Token无效
    public static final int TOKEN_INVALID = 1005;
    
    // Token过期
    public static final int TOKEN_EXPIRED = 1006;
}

