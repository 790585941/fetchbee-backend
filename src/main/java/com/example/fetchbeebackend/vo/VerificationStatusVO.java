package com.example.fetchbeebackend.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学生认证状态VO
 */
@Data
public class VerificationStatusVO {

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
    private LocalDateTime verificationTime;

    /**
     * 审核备注（拒绝原因等）
     */
    private String verificationRemark;
}
