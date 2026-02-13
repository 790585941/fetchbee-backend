package com.example.fetchbeebackend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
    /**
     * 订单ID
     */
    private Long id;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 发布者ID
     */
    private Long publisherId;
    
    /**
     * 接单者ID
     */
    private Long receiverId;
    
    /**
     * 快递公司
     */
    private String expressCompany;
    
    /**
     * 取件码
     */
    private String pickupCode;
    
    /**
     * 快递描述
     */
    private String description;
    
    /**
     * 取件地址
     */
    private String pickupAddress;
    
    /**
     * 送达地址（发布者地址）
     */
    private String deliveryAddress;
    
    /**
     * 报酬金额
     */
    private BigDecimal reward;
    
    /**
     * 截止时间
     */
    private LocalDateTime deadline;
    
    /**
     * 状态：1-待接单，2-已接单，3-待确认，4-已完成，5-已取消
     */
    private Integer status;
    
    /**
     * 实际支付金额（超时可能打折）
     */
    private BigDecimal actualReward;
    
    /**
     * 送达时间（接单者标记送达的时间）
     */
    private LocalDateTime deliverTime;
    
    /**
     * 完成时间
     */
    private LocalDateTime completeTime;
    
    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 维权状态：0-无维权，1-维权中（待审核），2-维权通过，3-维权不通过
     */
    private Integer rightsStatus;

    /**
     * 维权申请人：publisher-发布者，receiver-接单者
     */
    private String rightsApplicant;

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
     * 资金流向：publisher-发布者，receiver-接单者（仅在接单者维权通过时使用）
     */
    private String rightsFundTo;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

