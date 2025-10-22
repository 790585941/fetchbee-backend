package com.example.fetchbeebackend.service;

import com.example.fetchbeebackend.common.ResultCode;
import com.example.fetchbeebackend.dto.LoginRequest;
import com.example.fetchbeebackend.dto.RegisterRequest;
import com.example.fetchbeebackend.dto.UpdatePasswordRequest;
import com.example.fetchbeebackend.dto.UpdateUserRequest;
import com.example.fetchbeebackend.entity.User;
import com.example.fetchbeebackend.exception.BusinessException;
import com.example.fetchbeebackend.mapper.UserMapper;
import com.example.fetchbeebackend.utils.JwtUtil;
import com.example.fetchbeebackend.utils.RedisUtil;
import com.example.fetchbeebackend.vo.LoginVO;
import com.example.fetchbeebackend.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务类
 */
@Slf4j
@Service
public class UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private RedisUtil redisUtil;
    
    private static final String TOKEN_PREFIX = "token:";
    
    /**
     * 用户注册
     */
    @Transactional
    public void register(RegisterRequest request) {
        // 1. 检查用户名是否已存在
        User existUser = userMapper.findByUsername(request.getUsername());
        if (existUser != null) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS, "用户名已存在");
        }
        
        // 2. 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        // 使用MD5加密密码（实际项目建议使用BCrypt）
        user.setPassword(encryptPassword(request.getPassword()));
        user.setBalance(BigDecimal.ZERO);
        user.setStatus(1);
        
        // 3. 插入数据库
        int result = userMapper.insert(user);
        if (result <= 0) {
            throw new BusinessException("注册失败");
        }
        
        log.info("用户注册成功：{}", request.getUsername());
    }
    
    /**
     * 用户登录
     */
    public LoginVO login(LoginRequest request) {
        // 1. 查询用户
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND, "用户不存在");
        }
        
        // 2. 验证密码
        String encryptedPassword = encryptPassword(request.getPassword());
        if (!user.getPassword().equals(encryptedPassword)) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR, "密码错误");
        }
        
        // 3. 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.FORBIDDEN, "账号已被禁用");
        }
        
        // 4. 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        
        // 5. 将Token存入Redis（7天过期）
        redisUtil.set(TOKEN_PREFIX + token, user.getId(), 7, TimeUnit.DAYS);
        
        // 6. 构造返回对象
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserId(user.getId());
        loginVO.setUsername(user.getUsername());
        loginVO.setPhone(user.getPhone());
        loginVO.setAddress(user.getAddress());
        loginVO.setBalance(user.getBalance());
        loginVO.setAvatar(user.getAvatar());
        
        log.info("用户登录成功：{}", request.getUsername());
        return loginVO;
    }
    
    /**
     * 获取用户信息
     */
    public UserVO getUserInfo(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND, "用户不存在");
        }
        
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }
    
    /**
     * 更新用户信息
     */
    @Transactional
    public void updateUserInfo(Long userId, UpdateUserRequest request) {
        // 1. 检查用户是否存在
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND, "用户不存在");
        }
        
        // 2. 更新信息
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setAvatar(request.getAvatar());
        
        int result = userMapper.update(user);
        if (result <= 0) {
            throw new BusinessException("更新失败");
        }
        
        log.info("用户信息更新成功：userId={}", userId);
    }
    
    /**
     * 修改密码
     */
    @Transactional
    public void updatePassword(Long userId, UpdatePasswordRequest request) {
        // 1. 查询用户
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND, "用户不存在");
        }
        
        // 2. 验证旧密码
        String encryptedOldPassword = encryptPassword(request.getOldPassword());
        if (!user.getPassword().equals(encryptedOldPassword)) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR, "旧密码错误");
        }
        
        // 3. 更新密码
        String encryptedNewPassword = encryptPassword(request.getNewPassword());
        int result = userMapper.updatePassword(userId, encryptedNewPassword);
        if (result <= 0) {
            throw new BusinessException("修改密码失败");
        }
        
        log.info("用户密码修改成功：userId={}", userId);
    }
    
    /**
     * 用户登出
     */
    public void logout(String token) {
        // 从Redis中删除Token
        redisUtil.delete(TOKEN_PREFIX + token);
        log.info("用户登出成功");
    }
    
    /**
     * 密码加密（使用MD5）
     */
    private String encryptPassword(String password) {
        return DigestUtils.md5DigestAsHex(password.getBytes());
    }
}

