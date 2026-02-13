package com.example.fetchbeebackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建公告请求DTO
 */
@Data
public class CreateAnnouncementRequest {

    /**
     * 公告标题
     */
    @NotBlank(message = "公告标题不能为空")
    private String title;

    /**
     * 公告内容
     */
    @NotBlank(message = "公告内容不能为空")
    private String content;
}
