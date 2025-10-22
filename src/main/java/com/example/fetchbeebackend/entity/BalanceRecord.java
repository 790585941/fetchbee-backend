package com.example.fetchbeebackend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 余额变动记录实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalanceRecord {
    
    /**
     * 记录ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 变动金额（正数为收入，负数为支出）
     */
    private BigDecimal amount;
    
    /**
     * 变动前余额
     */
    private BigDecimal balanceBefore;
    
    /**
     * 变动后余额
     */
    private BigDecimal balanceAfter;
    
    /**
     * 类型：1-充值，2-发布订单扣款，3-完成订单收入，4-订单退款
     */
    private Integer type;
    
    /**
     * 关联订单ID（如果有）
     */
    private Long orderId;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

