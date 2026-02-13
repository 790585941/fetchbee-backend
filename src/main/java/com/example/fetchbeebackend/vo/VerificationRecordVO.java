package com.example.fetchbeebackend.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 认证记录VO（管理员查看）
 */
@Data
public class VerificationRecordVO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

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
