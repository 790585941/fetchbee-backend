package com.example.fetchbeebackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 提交维权申请请求DTO
 */
@Data
public class SubmitRightsProtectionRequest {

    @NotBlank(message = "维权描述不能为空")
    private String rightsDescription;

    @NotBlank(message = "维权照片凭证不能为空")
    private String rightsImage;
}
