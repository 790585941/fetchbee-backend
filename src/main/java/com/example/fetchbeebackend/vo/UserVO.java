package com.example.fetchbeebackend.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
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
     * 角色：0-普通用户，1-管理员
     */
    private Integer role;

    /**
     * 认证状态：0-未认证，1-待审核，2-已认证，3-审核不通过
     */
    private Integer verificationStatus;

    /**
     * 学生证照片URL
     */
    private String verificationImage;

    /**
     * 认证审核时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime verificationTime;

    /**
     * 审核备注（拒绝原因等）
     */
    private String verificationRemark;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}

