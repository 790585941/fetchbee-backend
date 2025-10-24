package com.example.fetchbeebackend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单信息VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderVO {
    
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
     * 发布者用户名
     */
    private String publisherName;
    
    /**
     * 接单者ID
     */
    private Long receiverId;
    
    /**
     * 接单者用户名
     */
    private String receiverName;
    
    /**
     * 快递公司
     */
    private String expressCompany;
    
    /**
     * 取件码（仅接单者和发布者可见）
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
     * 送达地址
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
     * 状态：1-待接单，2-已接单，3-已完成，4-已取消
     */
    private Integer status;
    
    /**
     * 状态描述
     */
    private String statusDesc;
    
    /**
     * 实际支付金额
     */
    private BigDecimal actualReward;
    
    /**
     * 完成时间
     */
    private LocalDateTime completeTime;
    
    /**
     * 取消原因
     */
    private String cancelReason;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 是否超时
     */
    private Boolean isOvertime;
}

