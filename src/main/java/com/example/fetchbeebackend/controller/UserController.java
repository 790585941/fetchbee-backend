package com.example.fetchbeebackend.controller;

import com.example.fetchbeebackend.common.Result;
import com.example.fetchbeebackend.dto.LoginRequest;
import com.example.fetchbeebackend.dto.RegisterRequest;
import com.example.fetchbeebackend.dto.UpdatePasswordRequest;
import com.example.fetchbeebackend.dto.UpdateUserRequest;
import com.example.fetchbeebackend.service.UserService;
import com.example.fetchbeebackend.vo.LoginVO;
import com.example.fetchbeebackend.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        log.info("用户注册请求：{}", request.getUsername());
        userService.register(request);
        return Result.success("注册成功", null);
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        log.info("用户登录请求：{}", request.getUsername());
        LoginVO loginVO = userService.login(request);
        return Result.success("登录成功", loginVO);
    }
    
    /**
     * 获取用户信息
     */
    @GetMapping("/info")
    public Result<UserVO> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("获取用户信息：userId={}", userId);
        UserVO userVO = userService.getUserInfo(userId);
        return Result.success(userVO);
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/info")
    public Result<Void> updateUserInfo(HttpServletRequest request, 
                                       @RequestBody UpdateUserRequest updateRequest) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("更新用户信息：userId={}", userId);
        userService.updateUserInfo(userId, updateRequest);
        return Result.success("更新成功", null);
    }
    
    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Void> updatePassword(HttpServletRequest request,
                                       @Valid @RequestBody UpdatePasswordRequest updateRequest) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("修改密码：userId={}", userId);
        userService.updatePassword(userId, updateRequest);
        return Result.success("修改密码成功", null);
    }
    
    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        log.info("用户登出");
        userService.logout(token);
        return Result.success("登出成功", null);
    }
}

