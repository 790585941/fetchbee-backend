package com.example.fetchbeebackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    /**
     * 用户ID
     */
    private Long id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码（加密后）
     */
    @JsonIgnore
    private String password;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 地址（宿舍/教学楼）
     */
    private String address;
    
    /**
     * 余额
     */
    private BigDecimal balance;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 状态：1-正常，0-禁用
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

