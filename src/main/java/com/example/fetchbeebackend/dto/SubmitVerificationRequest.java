package com.example.fetchbeebackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 提交学生认证请求DTO
 */
@Data
public class SubmitVerificationRequest {

    @NotBlank(message = "学生证照片不能为空")
    private String verificationImage;
}
