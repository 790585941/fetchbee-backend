package com.example.fetchbeebackend.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 维权信息VO
 */
@Data
public class RightsProtectionVO {

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 维权状态：0-无维权，1-维权中（待审核），2-维权通过，3-维权不通过
     */
    private Integer rightsStatus;

    /**
     * 维权申请人：publisher-发布者，receiver-接单者
     */
    private String rightsApplicant;

    /**
     * 维权申请人姓名
     */
    private String applicantName;

    /**
     * 维权描述
     */
    private String rightsDescription;

    /**
     * 维权照片凭证
     */
    private String rightsImage;

    /**
     * 维权申请时间
     */
    private LocalDateTime rightsApplyTime;

    /**
     * 维权审核时间
     */
    private LocalDateTime rightsReviewTime;

    /**
     * 审核备注
     */
    private String rightsRemark;

    /**
     * 资金流向：publisher-发布者，receiver-接单者
     */
    private String rightsFundTo;

    /**
     * 发布者姓名
     */
    private String publisherName;

    /**
     * 接单者姓名
     */
    private String receiverName;

    /**
     * 报酬金额
     */
    private java.math.BigDecimal reward;
}
