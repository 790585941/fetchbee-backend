package com.example.fetchbeebackend.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 通知视图对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationVO {
    
    /**
     * 通知ID
     */
    private Long id;
    
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
     * 关联订单号
     */
    private String orderNo;
    
    /**
     * 是否已读：0-未读，1-已读
     */
    private Integer isRead;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}

