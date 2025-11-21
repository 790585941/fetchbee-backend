package com.example.fetchbeebackend.task;

import com.example.fetchbeebackend.entity.Order;
import com.example.fetchbeebackend.enums.NotificationType;
import com.example.fetchbeebackend.enums.OrderStatus;
import com.example.fetchbeebackend.mapper.OrderMapper;
import com.example.fetchbeebackend.service.BalanceService;
import com.example.fetchbeebackend.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单过期定时任务
 * 每小时执行一次，自动取消过期未接单的订单
 */
@Slf4j
@Component
public class OrderExpireTask {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private NotificationService notificationService;

    /**
     * 自动取消过期订单
     * 每10分钟执行一次
     */
    @Scheduled(cron = "0 */10 * * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void expireOrders() {
        log.info("开始执行订单过期任务");

        try {
            LocalDateTime now = LocalDateTime.now();
            List<Order> orders = orderMapper.findExpiredPendingOrders(now);

            if (orders == null || orders.isEmpty()) {
                log.info("没有需要过期的订单");
                return;
            }

            log.info("找到{}个过期订单", orders.size());

            int successCount = 0;
            int failCount = 0;

            for (Order order : orders) {
                try {
                    int result = orderMapper.cancelOrder(order.getId(), "订单已过期，无人接单");
                    if (result <= 0) {
                        log.error("取消过期订单失败：orderId={}", order.getId());
                        failCount++;
                        continue;
                    }

                    balanceService.refund(order.getPublisherId(), order.getReward(), order.getId(),
                            "订单过期退款：" + order.getOrderNo());

                    notificationService.createNotification(
                        order.getPublisherId(),
                        NotificationType.ORDER_EXPIRED,
                        "订单已过期",
                        "订单【" + order.getOrderNo() + "】已过期，报酬 ¥" + order.getReward() + " 已退回",
                        order.getId()
                    );

                    log.info("取消过期订单成功：orderId={}, orderNo={}, publisherId={}, reward={}",
                            order.getId(), order.getOrderNo(), order.getPublisherId(), order.getReward());

                    successCount++;

                } catch (Exception e) {
                    log.error("取消过期订单异常：orderId={}, error={}", order.getId(), e.getMessage(), e);
                    failCount++;
                }
            }

            log.info("订单过期任务完成：总数={}, 成功={}, 失败={}", orders.size(), successCount, failCount);

        } catch (Exception e) {
            log.error("订单过期任务执行异常", e);
        }
    }
}
