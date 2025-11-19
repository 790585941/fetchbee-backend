package com.example.fetchbeebackend.mapper;

import com.example.fetchbeebackend.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通知Mapper接口
 */
@Mapper
public interface NotificationMapper {
    
    /**
     * 插入通知
     */
    int insert(Notification notification);
    
    /**
     * 根据ID查询通知
     */
    Notification findById(@Param("id") Long id);
    
    /**
     * 查询用户的未读通知列表
     */
    List<Notification> findUnreadByUserId(@Param("userId") Long userId);
    
    /**
     * 查询用户的所有通知列表（包括已读和未读）
     */
    List<Notification> findByUserId(@Param("userId") Long userId);
    
    /**
     * 查询用户的未读通知数量
     */
    int countUnreadByUserId(@Param("userId") Long userId);
    
    /**
     * 标记通知为已读
     */
    int markAsRead(@Param("id") Long id);
    
    /**
     * 标记用户的所有通知为已读
     */
    int markAllAsRead(@Param("userId") Long userId);
    
    /**
     * 删除通知
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 删除用户的所有通知
     */
    int deleteByUserId(@Param("userId") Long userId);
}

