package com.example.fetchbeebackend.mapper;

import com.example.fetchbeebackend.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单Mapper接口
 */
@Mapper
public interface OrderMapper {
    
    /**
     * 插入订单
     */
    int insert(Order order);
    
    /**
     * 根据ID查询订单
     */
    Order findById(@Param("id") Long id);
    
    /**
     * 根据订单号查询订单
     */
    Order findByOrderNo(@Param("orderNo") String orderNo);
    
    /**
     * 查询待接单订单列表
     */
    List<Order> findPendingOrders();
    
    /**
     * 查询用户发布的订单列表
     */
    List<Order> findByPublisherId(@Param("publisherId") Long publisherId);
    
    /**
     * 查询用户接的订单列表
     */
    List<Order> findByReceiverId(@Param("receiverId") Long receiverId);
    
    /**
     * 查询待确认且超过指定时间的订单列表（用于自动确认）
     */
    List<Order> findDeliveredOrdersBeforeTime(@Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 查询过期未接单的订单列表
     */
    List<Order> findExpiredPendingOrders(@Param("now") LocalDateTime now);

    /**
     * 更新订单状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /**
     * 接单（更新接单者ID和状态）
     */
    int acceptOrder(@Param("id") Long id, @Param("receiverId") Long receiverId);
    
    /**
     * 标记送达（更新状态为待确认）
     */
    int deliverOrder(@Param("id") Long id, @Param("deliverTime") LocalDateTime deliverTime);
    
    /**
     * 完成订单
     */
    int completeOrder(@Param("id") Long id, 
                     @Param("actualReward") BigDecimal actualReward,
                     @Param("completeTime") LocalDateTime completeTime);
    
    /**
     * 取消订单
     */
    int cancelOrder(@Param("id") Long id, @Param("cancelReason") String cancelReason);

    /**
     * 提交维权申请
     */
    int submitRightsProtection(@Param("id") Long id,
                               @Param("rightsApplicant") String rightsApplicant,
                               @Param("rightsDescription") String rightsDescription,
                               @Param("rightsImage") String rightsImage,
                               @Param("rightsApplyTime") LocalDateTime rightsApplyTime);

    /**
     * 审核维权申请
     */
    int reviewRightsProtection(@Param("id") Long id,
                               @Param("rightsStatus") Integer rightsStatus,
                               @Param("rightsRemark") String rightsRemark,
                               @Param("rightsFundTo") String rightsFundTo,
                               @Param("rightsReviewTime") LocalDateTime rightsReviewTime);

    /**
     * 查询待审核的维权订单列表
     */
    List<Order> findPendingRightsProtectionOrders();
}

