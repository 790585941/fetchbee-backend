package com.example.fetchbeebackend.controller;

import com.example.fetchbeebackend.common.Result;
import com.example.fetchbeebackend.common.ResultCode;
import com.example.fetchbeebackend.dto.ReviewVerificationRequest;
import com.example.fetchbeebackend.dto.ReviewRightsProtectionRequest;
import com.example.fetchbeebackend.dto.CreateAnnouncementRequest;
import com.example.fetchbeebackend.dto.UpdateAnnouncementRequest;
import com.example.fetchbeebackend.entity.User;
import com.example.fetchbeebackend.exception.BusinessException;
import com.example.fetchbeebackend.mapper.UserMapper;
import com.example.fetchbeebackend.service.VerificationService;
import com.example.fetchbeebackend.service.RightsProtectionService;
import com.example.fetchbeebackend.service.AnnouncementService;
import com.example.fetchbeebackend.vo.VerificationRecordVO;
import com.example.fetchbeebackend.vo.RightsProtectionVO;
import com.example.fetchbeebackend.vo.AnnouncementVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private RightsProtectionService rightsProtectionService;

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private UserMapper userMapper;

    /**
     * 检查管理员权限
     */
    private void checkAdminRole(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND, "用户不存在");
        }
        if (user.getRole() == null || user.getRole() != 1) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权限访问");
        }
    }

    /**
     * 获取待审核认证列表
     */
    @GetMapping("/verifications/pending")
    public Result<List<VerificationRecordVO>> getPendingVerifications(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        checkAdminRole(userId);
        log.info("管理员获取待审核认证列表：adminId={}", userId);
        List<VerificationRecordVO> records = verificationService.getPendingVerifications();
        return Result.success(records);
    }

    /**
     * 审核学生认证
     */
    @PutMapping("/verifications/{userId}/review")
    public Result<Void> reviewVerification(HttpServletRequest request,
                                           @PathVariable Long userId,
                                           @Valid @RequestBody ReviewVerificationRequest reviewRequest) {
        Long adminId = (Long) request.getAttribute("userId");
        checkAdminRole(adminId);
        log.info("管理员审核学生认证：adminId={}, targetUserId={}, status={}",
                adminId, userId, reviewRequest.getVerificationStatus());
        verificationService.reviewVerification(userId, reviewRequest);
        return Result.success("审核成功", null);
    }

    /**
     * 获取待审核维权列表
     */
    @GetMapping("/rights-protection/pending")
    public Result<List<RightsProtectionVO>> getPendingRightsProtectionList(HttpServletRequest request) {
        Long adminId = (Long) request.getAttribute("userId");
        checkAdminRole(adminId);
        log.info("管理员获取待审核维权列表：adminId={}", adminId);
        List<RightsProtectionVO> records = rightsProtectionService.getPendingRightsProtectionList();
        return Result.success(records);
    }

    /**
     * 审核维权申请
     */
    @PutMapping("/rights-protection/{orderId}/review")
    public Result<Void> reviewRightsProtection(HttpServletRequest request,
                                               @PathVariable Long orderId,
                                               @Valid @RequestBody ReviewRightsProtectionRequest reviewRequest) {
        Long adminId = (Long) request.getAttribute("userId");
        checkAdminRole(adminId);
        log.info("管理员审核维权申请：adminId={}, orderId={}, status={}",
                adminId, orderId, reviewRequest.getRightsStatus());
        rightsProtectionService.reviewRightsProtection(orderId, reviewRequest);
        return Result.success("审核成功", null);
    }

    /**
     * 创建公告
     */
    @PostMapping("/announcements")
    public Result<Void> createAnnouncement(HttpServletRequest request,
                                           @Valid @RequestBody CreateAnnouncementRequest createRequest) {
        Long adminId = (Long) request.getAttribute("userId");
        checkAdminRole(adminId);
        log.info("管理员创建公告：adminId={}, title={}", adminId, createRequest.getTitle());
        announcementService.createAnnouncement(createRequest, adminId);
        return Result.success("创建公告成功", null);
    }

    /**
     * 更新公告
     */
    @PutMapping("/announcements/{id}")
    public Result<Void> updateAnnouncement(HttpServletRequest request,
                                           @PathVariable Long id,
                                           @Valid @RequestBody UpdateAnnouncementRequest updateRequest) {
        Long adminId = (Long) request.getAttribute("userId");
        checkAdminRole(adminId);
        log.info("管理员更新公告：adminId={}, announcementId={}", adminId, id);
        announcementService.updateAnnouncement(id, updateRequest);
        return Result.success("更新公告成功", null);
    }

    /**
     * 删除公告（逻辑删除）
     */
    @DeleteMapping("/announcements/{id}")
    public Result<Void> deleteAnnouncement(HttpServletRequest request,
                                           @PathVariable Long id) {
        Long adminId = (Long) request.getAttribute("userId");
        checkAdminRole(adminId);
        log.info("管理员删除公告：adminId={}, announcementId={}", adminId, id);
        announcementService.deleteAnnouncement(id);
        return Result.success("删除公告成功", null);
    }

    /**
     * 查询所有公告（包含已下架）
     */
    @GetMapping("/announcements")
    public Result<List<AnnouncementVO>> getAllAnnouncements(HttpServletRequest request) {
        Long adminId = (Long) request.getAttribute("userId");
        checkAdminRole(adminId);
        log.info("管理员查询所有公告：adminId={}", adminId);
        List<AnnouncementVO> announcements = announcementService.getAllAnnouncements();
        return Result.success(announcements);
    }
}
