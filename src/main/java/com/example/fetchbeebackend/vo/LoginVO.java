package com.example.fetchbeebackend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 登录返回VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginVO {
    
    /**
     * Token
     */
    private String token;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 地址
     */
    private String address;
    
    /**
     * 余额
     */
    private BigDecimal balance;
    
    /**
     * 头像
     */
    private String avatar;
}

