package com.example.fetchbeebackend.service;

import com.example.fetchbeebackend.common.ResultCode;
import com.example.fetchbeebackend.entity.Notification;
import com.example.fetchbeebackend.entity.Order;
import com.example.fetchbeebackend.enums.NotificationType;
import com.example.fetchbeebackend.exception.BusinessException;
import com.example.fetchbeebackend.mapper.NotificationMapper;
import com.example.fetchbeebackend.mapper.OrderMapper;
import com.example.fetchbeebackend.vo.NotificationVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通知服务类
 */
@Slf4j
@Service
public class NotificationService {
    
    @Autowired
    private NotificationMapper notificationMapper;
    
    @Autowired
    private OrderMapper orderMapper;
    
    /**
     * 创建通知
     */
    @Transactional(rollbackFor = Exception.class)
    public void createNotification(Long userId, NotificationType type, String title, 
                                   String content, Long orderId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type.getCode());
        notification.setTitle(title);
        notification.setContent(content);
        notification.setOrderId(orderId);
        notification.setIsRead(0); // 默认未读
        
        int result = notificationMapper.insert(notification);
        if (result <= 0) {
            log.error("创建通知失败：userId={}, type={}", userId, type.getCode());
            throw new BusinessException("创建通知失败");
        }
        
        log.info("创建通知成功：userId={}, type={}, orderId={}", userId, type.getCode(), orderId);
    }
    
    /**
     * 查询未读通知列表和数量
     */
    public Map<String, Object> getUnreadNotifications(Long userId) {
        List<Notification> notifications = notificationMapper.findUnreadByUserId(userId);
        int unreadCount = notificationMapper.countUnreadByUserId(userId);
        
        List<NotificationVO> voList = convertToVOList(notifications);
        
        Map<String, Object> result = new HashMap<>();
        result.put("unreadCount", unreadCount);
        result.put("notifications", voList);
        
        return result;
    }
    
    /**
     * 查询所有通知列表
     */
    public List<NotificationVO> getAllNotifications(Long userId) {
        List<Notification> notifications = notificationMapper.findByUserId(userId);
        return convertToVOList(notifications);
    }
    
    /**
     * 标记通知为已读
     */
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Long notificationId, Long userId) {
        // 1. 查询通知
        Notification notification = notificationMapper.findById(notificationId);
        if (notification == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "通知不存在");
        }
        
        // 2. 检查是否是本人的通知
        if (!notification.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权操作此通知");
        }
        
        // 3. 标记为已读
        int result = notificationMapper.markAsRead(notificationId);
        if (result <= 0) {
            throw new BusinessException("标记已读失败");
        }
        
        log.info("标记通知为已读：notificationId={}, userId={}", notificationId, userId);
    }
    
    /**
     * 标记所有通知为已读
     */
    @Transactional(rollbackFor = Exception.class)
    public void markAllAsRead(Long userId) {
        int result = notificationMapper.markAllAsRead(userId);
        log.info("标记所有通知为已读：userId={}, count={}", userId, result);
    }
    
    /**
     * 删除通知
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteNotification(Long notificationId, Long userId) {
        // 1. 查询通知
        Notification notification = notificationMapper.findById(notificationId);
        if (notification == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "通知不存在");
        }
        
        // 2. 检查是否是本人的通知
        if (!notification.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权删除此通知");
        }
        
        // 3. 删除通知
        int result = notificationMapper.deleteById(notificationId);
        if (result <= 0) {
            throw new BusinessException("删除通知失败");
        }
        
        log.info("删除通知成功：notificationId={}, userId={}", notificationId, userId);
    }
    
    /**
     * 转换为VO对象
     */
    private NotificationVO convertToVO(Notification notification) {
        NotificationVO vo = new NotificationVO();
        BeanUtils.copyProperties(notification, vo);
        
        // 如果有关联订单，查询订单号
        if (notification.getOrderId() != null) {
            Order order = orderMapper.findById(notification.getOrderId());
            if (order != null) {
                vo.setOrderNo(order.getOrderNo());
            }
        }
        
        return vo;
    }
    
    /**
     * 批量转换为VO列表
     */
    private List<NotificationVO> convertToVOList(List<Notification> notifications) {
        List<NotificationVO> voList = new ArrayList<>();
        for (Notification notification : notifications) {
            voList.add(convertToVO(notification));
        }
        return voList;
    }
}

