package com.example.fetchbeebackend.interceptor;

import com.example.fetchbeebackend.common.ResultCode;
import com.example.fetchbeebackend.exception.BusinessException;
import com.example.fetchbeebackend.utils.JwtUtil;
import com.example.fetchbeebackend.utils.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 认证拦截器
 */
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private RedisUtil redisUtil;
    
    private static final String TOKEN_PREFIX = "token:";
    private static final String USER_ID_HEADER = "userId";
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 1. 从请求头获取Token
        String token = request.getHeader("Authorization");
        
        if (!StringUtils.hasText(token)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "未登录，请先登录");
        }
        
        // 2. 去除Bearer前缀（如果有）
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        // 3. 验证Token
        if (!jwtUtil.validateToken(token)) {
            throw new BusinessException(ResultCode.TOKEN_INVALID, "Token无效或已过期");
        }
        
        // 4. 检查Redis中是否存在Token
        if (!Boolean.TRUE.equals(redisUtil.hasKey(TOKEN_PREFIX + token))) {
            throw new BusinessException(ResultCode.TOKEN_EXPIRED, "登录已过期，请重新登录");
        }
        
        // 5. 从Token中获取用户ID，并设置到请求属性中
        Long userId = jwtUtil.getUserIdFromToken(token);
        request.setAttribute(USER_ID_HEADER, userId);
        
        return true;
    }
}

