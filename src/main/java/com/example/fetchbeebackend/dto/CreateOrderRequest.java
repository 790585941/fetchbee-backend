package com.example.fetchbeebackend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 创建订单请求DTO
 */
@Data
public class CreateOrderRequest {
    
    /**
     * 快递公司
     */
    private String expressCompany;
    
    /**
     * 取件码
     */
    @NotBlank(message = "取件码不能为空")
    private String pickupCode;
    
    /**
     * 快递描述
     */
    private String description;
    
    /**
     * 取件地址
     */
    @NotBlank(message = "取件地址不能为空")
    private String pickupAddress;
    
    /**
     * 报酬金额
     */
    @NotNull(message = "报酬金额不能为空")
    @DecimalMin(value = "0.01", message = "报酬金额必须大于0")
    private BigDecimal reward;
    
    /**
     * 截止时间
     */
    @NotNull(message = "截止时间不能为空")
    private LocalDateTime deadline;
}

