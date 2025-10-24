package com.example.fetchbeebackend.enums;

/**
 * 订单状态枚举
 */
public enum OrderStatus {
    
    /**
     * 待接单
     */
    PENDING(1, "待接单"),
    
    /**
     * 已接单
     */
    ACCEPTED(2, "已接单"),
    
    /**
     * 已完成
     */
    COMPLETED(3, "已完成"),
    
    /**
     * 已取消
     */
    CANCELLED(4, "已取消");
    
    private final Integer code;
    private final String desc;
    
    OrderStatus(Integer code, String desc) {
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

