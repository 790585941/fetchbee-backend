package com.example.fetchbeebackend.controller;

import com.example.fetchbeebackend.common.Result;
import com.example.fetchbeebackend.dto.SubmitRightsProtectionRequest;
import com.example.fetchbeebackend.service.RightsProtectionService;
import com.example.fetchbeebackend.vo.RightsProtectionVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 维权控制器（用户端）
 */
@Slf4j
@RestController
@RequestMapping("/rights-protection")
public class RightsProtectionController {

    @Autowired
    private RightsProtectionService rightsProtectionService;

    /**
     * 提交维权申请
     */
    @PostMapping("/{orderId}/submit")
    public Result<Void> submitRightsProtection(HttpServletRequest request,
                                                @PathVariable Long orderId,
                                                @Valid @RequestBody SubmitRightsProtectionRequest submitRequest) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("提交维权申请：userId={}, orderId={}", userId, orderId);
        rightsProtectionService.submitRightsProtection(orderId, userId, submitRequest);
        return Result.success("提交成功，请等待管理员审核", null);
    }

    /**
     * 查看维权状态
     */
    @GetMapping("/{orderId}/status")
    public Result<RightsProtectionVO> getRightsProtectionStatus(HttpServletRequest request,
                                                                 @PathVariable Long orderId) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("查看维权状态：userId={}, orderId={}", userId, orderId);
        RightsProtectionVO vo = rightsProtectionService.getRightsProtectionStatus(orderId, userId);
        return Result.success(vo);
    }
}
