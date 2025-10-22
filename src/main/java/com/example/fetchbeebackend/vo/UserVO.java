package com.example.fetchbeebackend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户信息VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {
    
    /**
     * 用户ID
     */
    private Long id;
    
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
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

