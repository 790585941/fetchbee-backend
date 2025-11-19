package com.example.fetchbeebackend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 通知实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    
    /**
     * 通知ID
     */
    private Long id;
    
    /**
     * 接收通知的用户ID
     */
    private Long userId;
    
    /**
     * 通知类型
     */
    private String type;
    
    /**
     * 通知标题
     */
    private String title;
    
    /**
     * 通知内容
     */
    private String content;
    
    /**
     * 关联订单ID
     */
    private Long orderId;
    
    /**
     * 是否已读：0-未读，1-已读
     */
    private Integer isRead;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

