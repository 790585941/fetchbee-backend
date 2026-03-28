package com.example.fetchbeebackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 审核订单请求DTO
 */
@Data
public class ReviewOrderRequest {

    @NotNull(message = "审核状态不能为空")
    private Integer reviewStatus;

    private String reviewRemark;
}
