package com.example.fetchbeebackend.controller;

import com.example.fetchbeebackend.common.Result;
import com.example.fetchbeebackend.dto.CreateOrderRequest;
import com.example.fetchbeebackend.service.OrderService;
import com.example.fetchbeebackend.vo.OrderVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单控制器
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 发布订单
     */
    @PostMapping("/create")
    public Result<Map<String, Object>> createOrder(HttpServletRequest request,
                                                    @Valid @RequestBody CreateOrderRequest createRequest) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("发布订单请求：userId={}", userId);
        
        Long orderId = orderService.createOrder(userId, createRequest);
        
        Map<String, Object> data = new HashMap<>();
        data.put("orderId", orderId);
        
        return Result.success("发布成功", data);
    }
    
    /**
     * 接单
     */
    @PostMapping("/{orderId}/accept")
    public Result<Void> acceptOrder(HttpServletRequest request,
                                    @PathVariable Long orderId) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("接单请求：userId={}, orderId={}", userId, orderId);
        
        orderService.acceptOrder(orderId, userId);
        
        return Result.success("接单成功", null);
    }
    
    /**
     * 完成订单
     */
    @PostMapping("/{orderId}/complete")
    public Result<Void> completeOrder(HttpServletRequest request,
                                      @PathVariable Long orderId) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("完成订单请求：userId={}, orderId={}", userId, orderId);
        
        orderService.completeOrder(orderId, userId);
        
        return Result.success("订单已完成", null);
    }
    
    /**
     * 取消订单
     */
    @PostMapping("/{orderId}/cancel")
    public Result<Void> cancelOrder(HttpServletRequest request,
                                    @PathVariable Long orderId,
                                    @RequestParam(required = false) String reason) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("取消订单请求：userId={}, orderId={}, reason={}", userId, orderId, reason);
        
        orderService.cancelOrder(orderId, userId, reason);
        
        return Result.success("订单已取消", null);
    }
    
    /**
     * 查询订单详情
     */
    @GetMapping("/{orderId}")
    public Result<OrderVO> getOrderDetail(HttpServletRequest request,
                                         @PathVariable Long orderId) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("查询订单详情：userId={}, orderId={}", userId, orderId);
        
        OrderVO orderVO = orderService.getOrderDetail(orderId, userId);
        
        return Result.success(orderVO);
    }
    
    /**
     * 查询待接单订单列表
     */
    @GetMapping("/pending")
    public Result<List<OrderVO>> getPendingOrders(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("查询待接单订单列表：userId={}", userId);
        
        List<OrderVO> orders = orderService.getPendingOrders(userId);
        
        return Result.success(orders);
    }
    
    /**
     * 查询我发布的订单列表
     */
    @GetMapping("/my-published")
    public Result<List<OrderVO>> getMyPublishedOrders(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("查询我发布的订单列表：userId={}", userId);
        
        List<OrderVO> orders = orderService.getMyPublishedOrders(userId);
        
        return Result.success(orders);
    }
    
    /**
     * 查询我接的订单列表
     */
    @GetMapping("/my-accepted")
    public Result<List<OrderVO>> getMyAcceptedOrders(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("查询我接的订单列表：userId={}", userId);
        
        List<OrderVO> orders = orderService.getMyAcceptedOrders(userId);
        
        return Result.success(orders);
    }
}

