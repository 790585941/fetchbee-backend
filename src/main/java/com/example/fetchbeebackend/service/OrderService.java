package com.example.fetchbeebackend.service;

import com.example.fetchbeebackend.common.ResultCode;
import com.example.fetchbeebackend.dto.CreateOrderRequest;
import com.example.fetchbeebackend.entity.Order;
import com.example.fetchbeebackend.entity.User;
import com.example.fetchbeebackend.enums.OrderStatus;
import com.example.fetchbeebackend.exception.BusinessException;
import com.example.fetchbeebackend.mapper.OrderMapper;
import com.example.fetchbeebackend.mapper.UserMapper;
import com.example.fetchbeebackend.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 订单服务类
 */
@Slf4j
@Service
public class OrderService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private BalanceService balanceService;
    
    /**
     * 发布订单
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(Long publisherId, CreateOrderRequest request) {
        // 1. 查询发布者信息
        User publisher = userMapper.findById(publisherId);
        if (publisher == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND, "用户不存在");
        }
        
        // 2. 检查余额是否充足
        if (publisher.getBalance().compareTo(request.getReward()) < 0) {
            throw new BusinessException(ResultCode.INSUFFICIENT_BALANCE, 
                    "余额不足，当前余额：" + publisher.getBalance() + "元");
        }
        
        // 3. 检查截止时间是否合理
        if (request.getDeadline().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "截止时间不能早于当前时间");
        }
        
        // 4. 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setPublisherId(publisherId);
        order.setExpressCompany(request.getExpressCompany());
        order.setPickupCode(request.getPickupCode());
        order.setDescription(request.getDescription());
        order.setPickupAddress(request.getPickupAddress());
        order.setDeliveryAddress(publisher.getAddress()); // 使用发布者的地址
        order.setReward(request.getReward());
        order.setDeadline(request.getDeadline());
        order.setStatus(OrderStatus.PENDING.getCode());
        
        int result = orderMapper.insert(order);
        if (result <= 0) {
            throw new BusinessException("发布订单失败");
        }
        
        // 5. 扣除发布者余额
        balanceService.deduct(publisherId, request.getReward(), order.getId(), 
                "发布订单：" + order.getOrderNo());
        
        log.info("发布订单成功：orderId={}, publisherId={}, reward={}", 
                order.getId(), publisherId, request.getReward());
        
        return order.getId();
    }
    
    /**
     * 接单
     */
    @Transactional(rollbackFor = Exception.class)
    public void acceptOrder(Long orderId, Long receiverId) {
        // 1. 查询订单
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "订单不存在");
        }
        
        // 2. 检查订单状态
        if (!order.getStatus().equals(OrderStatus.PENDING.getCode())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "该订单已被接单或已完成");
        }
        
        // 3. 不能接自己发布的订单
        if (order.getPublisherId().equals(receiverId)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "不能接自己发布的订单");
        }
        
        // 4. 接单（更新订单状态和接单者ID）
        int result = orderMapper.acceptOrder(orderId, receiverId);
        if (result <= 0) {
            throw new BusinessException("接单失败，订单可能已被他人接单");
        }
        
        log.info("接单成功：orderId={}, receiverId={}", orderId, receiverId);
    }
    
    /**
     * 完成订单
     */
    @Transactional(rollbackFor = Exception.class)
    public void completeOrder(Long orderId, Long receiverId) {
        // 1. 查询订单
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "订单不存在");
        }
        
        // 2. 检查订单状态
        if (!order.getStatus().equals(OrderStatus.ACCEPTED.getCode())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "订单状态不正确");
        }
        
        // 3. 检查是否是接单者本人
        if (!order.getReceiverId().equals(receiverId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "只有接单者才能完成订单");
        }
        
        // 4. 判断是否超时，计算实际支付金额
        LocalDateTime now = LocalDateTime.now();
        BigDecimal actualReward;
        boolean isOvertime = now.isAfter(order.getDeadline());
        
        if (isOvertime) {
            // 超时完成，按80%支付
            actualReward = order.getReward().multiply(new BigDecimal("0.8"));
            log.warn("订单超时完成：orderId={}, 原报酬={}, 实际支付={}", 
                    orderId, order.getReward(), actualReward);
        } else {
            // 按时完成，全额支付
            actualReward = order.getReward();
        }
        
        // 5. 更新订单状态为已完成
        int result = orderMapper.completeOrder(orderId, actualReward, now);
        if (result <= 0) {
            throw new BusinessException("完成订单失败");
        }
        
        // 6. 给接单者转账
        balanceService.transfer(receiverId, actualReward, orderId, 
                "完成订单收入：" + order.getOrderNo() + (isOvertime ? "（超时）" : ""));
        
        log.info("完成订单成功：orderId={}, receiverId={}, actualReward={}, isOvertime={}", 
                orderId, receiverId, actualReward, isOvertime);
    }
    
    /**
     * 取消订单
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long orderId, Long publisherId, String reason) {
        // 1. 查询订单
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "订单不存在");
        }
        
        // 2. 检查是否是发布者本人
        if (!order.getPublisherId().equals(publisherId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "只有发布者才能取消订单");
        }
        
        // 3. 只有待接单状态的订单才能取消
        if (!order.getStatus().equals(OrderStatus.PENDING.getCode())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "该订单无法取消");
        }
        
        // 4. 更新订单状态为已取消
        int result = orderMapper.cancelOrder(orderId, reason);
        if (result <= 0) {
            throw new BusinessException("取消订单失败");
        }
        
        // 5. 退款给发布者
        balanceService.refund(publisherId, order.getReward(), orderId, 
                "取消订单退款：" + order.getOrderNo());
        
        log.info("取消订单成功：orderId={}, publisherId={}, reason={}", 
                orderId, publisherId, reason);
    }
    
    /**
     * 查询订单详情
     */
    public OrderVO getOrderDetail(Long orderId, Long userId) {
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "订单不存在");
        }
        
        return convertToVO(order, userId);
    }
    
    /**
     * 查询待接单订单列表
     */
    public List<OrderVO> getPendingOrders(Long userId) {
        List<Order> orders = orderMapper.findPendingOrders();
        return convertToVOList(orders, userId);
    }
    
    /**
     * 查询我发布的订单列表
     */
    public List<OrderVO> getMyPublishedOrders(Long publisherId) {
        List<Order> orders = orderMapper.findByPublisherId(publisherId);
        return convertToVOList(orders, publisherId);
    }
    
    /**
     * 查询我接的订单列表
     */
    public List<OrderVO> getMyAcceptedOrders(Long receiverId) {
        List<Order> orders = orderMapper.findByReceiverId(receiverId);
        return convertToVOList(orders, receiverId);
    }
    
    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
        return "FO" + timestamp + uuid;
    }
    
    /**
     * 转换为VO对象
     */
    private OrderVO convertToVO(Order order, Long userId) {
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);
        
        // 查询发布者信息
        User publisher = userMapper.findById(order.getPublisherId());
        if (publisher != null) {
            vo.setPublisherName(publisher.getUsername());
        }
        
        // 查询接单者信息
        if (order.getReceiverId() != null) {
            User receiver = userMapper.findById(order.getReceiverId());
            if (receiver != null) {
                vo.setReceiverName(receiver.getUsername());
            }
        }
        
        // 设置状态描述
        vo.setStatusDesc(getStatusDesc(order.getStatus()));
        
        // 判断是否超时
        vo.setIsOvertime(LocalDateTime.now().isAfter(order.getDeadline()) 
                && order.getStatus().equals(OrderStatus.ACCEPTED.getCode()));
        
        // 取件码只对发布者和接单者可见
        if (!order.getPublisherId().equals(userId) && 
            (order.getReceiverId() == null || !order.getReceiverId().equals(userId))) {
            vo.setPickupCode("***"); // 隐藏取件码
        }
        
        return vo;
    }
    
    /**
     * 批量转换为VO列表
     */
    private List<OrderVO> convertToVOList(List<Order> orders, Long userId) {
        List<OrderVO> voList = new ArrayList<>();
        for (Order order : orders) {
            voList.add(convertToVO(order, userId));
        }
        return voList;
    }
    
    /**
     * 获取状态描述
     */
    private String getStatusDesc(Integer status) {
        for (OrderStatus orderStatus : OrderStatus.values()) {
            if (orderStatus.getCode().equals(status)) {
                return orderStatus.getDesc();
            }
        }
        return "未知";
    }
}

