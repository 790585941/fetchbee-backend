package com.example.fetchbeebackend.controller;

import com.example.fetchbeebackend.common.Result;
import com.example.fetchbeebackend.service.NotificationService;
import com.example.fetchbeebackend.vo.NotificationVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 通知控制器
 */
@Slf4j
@RestController
@RequestMapping("/notification")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * 查询未读通知列表和数量
     */
    @GetMapping("/unread")
    public Result<Map<String, Object>> getUnreadNotifications(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("查询未读通知：userId={}", userId);
        
        Map<String, Object> data = notificationService.getUnreadNotifications(userId);
        
        return Result.success(data);
    }
    
    /**
     * 查询所有通知列表（包括已读和未读）
     */
    @GetMapping("/list")
    public Result<List<NotificationVO>> getAllNotifications(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("查询所有通知：userId={}", userId);
        
        List<NotificationVO> notifications = notificationService.getAllNotifications(userId);
        
        return Result.success(notifications);
    }
    
    /**
     * 标记通知为已读
     */
    @PutMapping("/{notificationId}/read")
    public Result<Void> markAsRead(HttpServletRequest request,
                                   @PathVariable Long notificationId) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("标记通知为已读：userId={}, notificationId={}", userId, notificationId);
        
        notificationService.markAsRead(notificationId, userId);
        
        return Result.success("已标记为已读", null);
    }
    
    /**
     * 标记所有通知为已读
     */
    @PutMapping("/read-all")
    public Result<Void> markAllAsRead(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("标记所有通知为已读：userId={}", userId);
        
        notificationService.markAllAsRead(userId);
        
        return Result.success("所有通知已标记为已读", null);
    }
    
    /**
     * 删除通知
     */
    @DeleteMapping("/{notificationId}")
    public Result<Void> deleteNotification(HttpServletRequest request,
                                           @PathVariable Long notificationId) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("删除通知：userId={}, notificationId={}", userId, notificationId);
        
        notificationService.deleteNotification(notificationId, userId);
        
        return Result.success("通知已删除", null);
    }
}

