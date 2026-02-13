package com.example.fetchbeebackend.enums;

/**
 * 通知类型枚举
 */
public enum NotificationType {
    
    /**
     * 订单已被接单（通知发布者）
     */
    ORDER_ACCEPTED("ORDER_ACCEPTED", "订单已被接单"),
    
    /**
     * 快递已送达（通知发布者）
     */
    ORDER_DELIVERED("ORDER_DELIVERED", "快递已送达"),
    
    /**
     * 订单已完成（通知接单者）
     */
    ORDER_COMPLETED("ORDER_COMPLETED", "订单已完成"),
    
    /**
     * 订单已自动确认（通知发布者）
     */
    ORDER_AUTO_CONFIRMED("ORDER_AUTO_CONFIRMED", "订单已自动确认"),

    /**
     * 订单已过期（通知发布者）
     */
    ORDER_EXPIRED("ORDER_EXPIRED", "订单已过期"),

    /**
     * 维权申请已提交（通知对方）
     */
    RIGHTS_APPLIED("RIGHTS_APPLIED", "维权申请已提交"),

    /**
     * 维权审核完成（通知双方）
     */
    RIGHTS_REVIEWED("RIGHTS_REVIEWED", "维权审核完成"),

    /**
     * 系统通知
     */
    SYSTEM("SYSTEM", "系统通知");
    
    private final String code;
    private final String desc;
    
    NotificationType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDesc() {
        return desc;
    }
}

