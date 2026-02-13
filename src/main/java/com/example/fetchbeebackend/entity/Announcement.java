package com.example.fetchbeebackend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 公告实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Announcement {

    /**
     * 公告ID
     */
    private Long id;

    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告内容
     */
    private String content;

    /**
     * 发布者ID（管理员）
     */
    private Long publisherId;

    /**
     * 状态：1-已发布，0-已下架
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
