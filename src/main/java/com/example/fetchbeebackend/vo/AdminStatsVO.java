package com.example.fetchbeebackend.vo;

import lombok.Data;

/**
 * 管理员统计数据VO
 */
@Data
public class AdminStatsVO {
    /**
     * 待审核订单数量
     */
    private Integer pendingOrders;

    /**
     * 待审核认证数量
     */
    private Integer pendingVerifications;

    /**
     * 待审核维权数量
     */
    private Integer pendingRights;

    /**
     * 公告总数
     */
    private Integer totalAnnouncements;
}
