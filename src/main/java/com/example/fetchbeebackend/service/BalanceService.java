package com.example.fetchbeebackend.service;

import com.example.fetchbeebackend.common.ResultCode;
import com.example.fetchbeebackend.entity.BalanceRecord;
import com.example.fetchbeebackend.entity.User;
import com.example.fetchbeebackend.enums.BalanceType;
import com.example.fetchbeebackend.exception.BusinessException;
import com.example.fetchbeebackend.mapper.BalanceRecordMapper;
import com.example.fetchbeebackend.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 余额服务类
 */
@Slf4j
@Service
public class BalanceService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private BalanceRecordMapper balanceRecordMapper;
    
    /**
     * 扣款（发布订单时）
     */
    @Transactional(rollbackFor = Exception.class)
    public void deduct(Long userId, BigDecimal amount, Long orderId, String remark) {
        // 1. 查询用户
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND, "用户不存在");
        }
        
        // 2. 检查余额是否充足
        if (user.getBalance().compareTo(amount) < 0) {
            throw new BusinessException(ResultCode.INSUFFICIENT_BALANCE, "余额不足");
        }
        
        // 3. 扣除余额
        BigDecimal balanceBefore = user.getBalance();
        BigDecimal balanceAfter = balanceBefore.subtract(amount);
        
        int result = userMapper.updateBalance(userId, balanceAfter);
        if (result <= 0) {
            throw new BusinessException("扣款失败");
        }
        
        // 4. 记录余额变动
        BalanceRecord record = new BalanceRecord();
        record.setUserId(userId);
        record.setAmount(amount.negate()); // 负数表示支出
        record.setBalanceBefore(balanceBefore);
        record.setBalanceAfter(balanceAfter);
        record.setType(BalanceType.ORDER_DEDUCT.getCode());
        record.setOrderId(orderId);
        record.setRemark(remark);
        
        balanceRecordMapper.insert(record);
        
        log.info("扣款成功：userId={}, amount={}, orderId={}", userId, amount, orderId);
    }
    
    /**
     * 转账（完成订单时给接单者）
     */
    @Transactional(rollbackFor = Exception.class)
    public void transfer(Long userId, BigDecimal amount, Long orderId, String remark) {
        // 1. 查询用户
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND, "用户不存在");
        }
        
        // 2. 增加余额
        BigDecimal balanceBefore = user.getBalance();
        BigDecimal balanceAfter = balanceBefore.add(amount);
        
        int result = userMapper.updateBalance(userId, balanceAfter);
        if (result <= 0) {
            throw new BusinessException("转账失败");
        }
        
        // 3. 记录余额变动
        BalanceRecord record = new BalanceRecord();
        record.setUserId(userId);
        record.setAmount(amount); // 正数表示收入
        record.setBalanceBefore(balanceBefore);
        record.setBalanceAfter(balanceAfter);
        record.setType(BalanceType.ORDER_INCOME.getCode());
        record.setOrderId(orderId);
        record.setRemark(remark);
        
        balanceRecordMapper.insert(record);
        
        log.info("转账成功：userId={}, amount={}, orderId={}", userId, amount, orderId);
    }
    
    /**
     * 退款（取消订单时）
     */
    @Transactional(rollbackFor = Exception.class)
    public void refund(Long userId, BigDecimal amount, Long orderId, String remark) {
        // 1. 查询用户
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND, "用户不存在");
        }
        
        // 2. 增加余额
        BigDecimal balanceBefore = user.getBalance();
        BigDecimal balanceAfter = balanceBefore.add(amount);
        
        int result = userMapper.updateBalance(userId, balanceAfter);
        if (result <= 0) {
            throw new BusinessException("退款失败");
        }
        
        // 3. 记录余额变动
        BalanceRecord record = new BalanceRecord();
        record.setUserId(userId);
        record.setAmount(amount); // 正数表示收入
        record.setBalanceBefore(balanceBefore);
        record.setBalanceAfter(balanceAfter);
        record.setType(BalanceType.ORDER_REFUND.getCode());
        record.setOrderId(orderId);
        record.setRemark(remark);
        
        balanceRecordMapper.insert(record);
        
        log.info("退款成功：userId={}, amount={}, orderId={}", userId, amount, orderId);
    }
}

