package com.example.fetchbeebackend.task;

import com.example.fetchbeebackend.entity.Order;
import com.example.fetchbeebackend.enums.NotificationType;
import com.example.fetchbeebackend.mapper.OrderMapper;
import com.example.fetchbeebackend.service.BalanceService;
import com.example.fetchbeebackend.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单自动确认定时任务
 * 每小时执行一次，自动确认超过24小时未确认的订单
 */
@Slf4j
@Component
public class OrderAutoConfirmTask {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private BalanceService balanceService;
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * 自动确认超时订单
     * 每小时执行一次
     */
    @Scheduled(cron = "0 0 * * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void autoConfirmOrders() {
        log.info("开始执行订单自动确认任务");
        
        try {
            // 查询24小时前送达但未确认的订单
            LocalDateTime beforeTime = LocalDateTime.now().minusHours(24);
            List<Order> orders = orderMapper.findDeliveredOrdersBeforeTime(beforeTime);
            
            if (orders == null || orders.isEmpty()) {
                log.info("没有需要自动确认的订单");
                return;
            }
            
            log.info("找到{}个需要自动确认的订单", orders.size());
            
            int successCount = 0;
            int failCount = 0;
            
            for (Order order : orders) {
                try {
                    // 判断是否超时，计算实际支付金额
                    LocalDateTime now = LocalDateTime.now();
                    BigDecimal actualReward;
                    boolean isOvertime = now.isAfter(order.getDeadline());
                    
                    if (isOvertime) {
                        // 超时完成，按80%支付
                        actualReward = order.getReward().multiply(new BigDecimal("0.8"));
                        log.warn("订单超时完成（自动确认）：orderId={}, 原报酬={}, 实际支付={}", 
                                order.getId(), order.getReward(), actualReward);
                    } else {
                        // 按时完成，全额支付
                        actualReward = order.getReward();
                    }
                    
                    // 更新订单状态为已完成
                    int result = orderMapper.completeOrder(order.getId(), actualReward, now);
                    if (result <= 0) {
                        log.error("自动确认订单失败：orderId={}", order.getId());
                        failCount++;
                        continue;
                    }
                    
                    // 给接单者转账
                    balanceService.transfer(order.getReceiverId(), actualReward, order.getId(), 
                            "完成订单收入（自动确认）：" + order.getOrderNo() + (isOvertime ? "（超时）" : ""));
                    
                    // 通知发布者：订单已自动确认
                    notificationService.createNotification(
                        order.getPublisherId(),
                        NotificationType.ORDER_AUTO_CONFIRMED,
                        "订单已自动确认",
                        "订单【" + order.getOrderNo() + "】已自动确认完成",
                        order.getId()
                    );
                    
                    // 通知接单者：订单已完成，报酬已到账
                    notificationService.createNotification(
                        order.getReceiverId(),
                        NotificationType.ORDER_COMPLETED,
                        "订单已完成",
                        "订单【" + order.getOrderNo() + "】已自动确认完成，报酬 ¥" + actualReward + " 已到账" + (isOvertime ? "（超时）" : ""),
                        order.getId()
                    );
                    
                    log.info("自动确认订单成功：orderId={}, orderNo={}, receiverId={}, actualReward={}, isOvertime={}", 
                            order.getId(), order.getOrderNo(), order.getReceiverId(), actualReward, isOvertime);
                    
                    successCount++;
                    
                } catch (Exception e) {
                    log.error("自动确认订单异常：orderId={}, error={}", order.getId(), e.getMessage(), e);
                    failCount++;
                }
            }
            
            log.info("订单自动确认任务完成：总数={}, 成功={}, 失败={}", orders.size(), successCount, failCount);
            
        } catch (Exception e) {
            log.error("订单自动确认任务执行异常", e);
        }
    }
}
