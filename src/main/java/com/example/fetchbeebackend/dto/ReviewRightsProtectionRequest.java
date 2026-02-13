package com.example.fetchbeebackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 审核维权请求DTO
 */
@Data
public class ReviewRightsProtectionRequest {

    @NotNull(message = "维权状态不能为空")
    private Integer rightsStatus;

    private String rightsRemark;

    /**
     * 资金流向：publisher-发布者，receiver-接单者
     * 仅在接单者维权通过时需要填写
     */
    private String rightsFundTo;
}
