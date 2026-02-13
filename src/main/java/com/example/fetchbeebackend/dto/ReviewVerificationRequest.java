package com.example.fetchbeebackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 审核学生认证请求DTO
 */
@Data
public class ReviewVerificationRequest {

    @NotNull(message = "认证状态不能为空")
    private Integer verificationStatus;

    private String verificationRemark;
}
