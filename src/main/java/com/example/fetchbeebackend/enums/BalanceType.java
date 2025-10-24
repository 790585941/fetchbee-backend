package com.example.fetchbeebackend.enums;

/**
 * 余额变动类型枚举
 */
public enum BalanceType {
    
    /**
     * 充值
     */
    RECHARGE(1, "充值"),
    
    /**
     * 发布订单扣款
     */
    ORDER_DEDUCT(2, "发布订单扣款"),
    
    /**
     * 完成订单收入
     */
    ORDER_INCOME(3, "完成订单收入"),
    
    /**
     * 订单退款
     */
    ORDER_REFUND(4, "订单退款");
    
    private final Integer code;
    private final String desc;
    
    BalanceType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getDesc() {
        return desc;
    }
}

