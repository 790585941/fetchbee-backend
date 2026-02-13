package com.example.fetchbeebackend.controller;

import com.example.fetchbeebackend.common.Result;
import com.example.fetchbeebackend.dto.SubmitVerificationRequest;
import com.example.fetchbeebackend.service.VerificationService;
import com.example.fetchbeebackend.vo.VerificationStatusVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 学生认证控制器（用户端）
 */
@Slf4j
@RestController
@RequestMapping("/verification")
public class VerificationController {

    @Autowired
    private VerificationService verificationService;

    /**
     * 提交学生认证
     */
    @PostMapping("/submit")
    public Result<Void> submitVerification(HttpServletRequest request,
                                           @Valid @RequestBody SubmitVerificationRequest submitRequest) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("提交学生认证：userId={}", userId);
        verificationService.submitVerification(userId, submitRequest);
        return Result.success("提交成功，请等待审核", null);
    }

    /**
     * 获取认证状态
     */
    @GetMapping("/status")
    public Result<VerificationStatusVO> getVerificationStatus(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("获取认证状态：userId={}", userId);
        VerificationStatusVO statusVO = verificationService.getVerificationStatus(userId);
        return Result.success(statusVO);
    }
}
