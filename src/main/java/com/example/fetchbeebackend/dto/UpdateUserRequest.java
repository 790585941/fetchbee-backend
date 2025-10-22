package com.example.fetchbeebackend.dto;

import lombok.Data;

/**
 * 更新用户信息请求DTO
 */
@Data
public class UpdateUserRequest {
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 地址
     */
    private String address;
    
    /**
     * 头像
     */
    private String avatar;
}

