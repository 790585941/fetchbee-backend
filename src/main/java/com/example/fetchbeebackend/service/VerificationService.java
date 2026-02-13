package com.example.fetchbeebackend.service;

import com.example.fetchbeebackend.common.ResultCode;
import com.example.fetchbeebackend.dto.ReviewVerificationRequest;
import com.example.fetchbeebackend.dto.SubmitVerificationRequest;
import com.example.fetchbeebackend.entity.User;
import com.example.fetchbeebackend.exception.BusinessException;
import com.example.fetchbeebackend.mapper.UserMapper;
import com.example.fetchbeebackend.vo.VerificationRecordVO;
import com.example.fetchbeebackend.vo.VerificationStatusVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 学生认证服务类
 */
@Slf4j
@Service
public class VerificationService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 提交学生认证
     */
    @Transactional
    public void submitVerification(Long userId, SubmitVerificationRequest request) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND, "用户不存在");
        }

        int result = userMapper.updateVerificationSubmit(userId, request.getVerificationImage());
        if (result <= 0) {
            throw new BusinessException("提交认证失败");
        }

        log.info("用户提交学生认证：userId={}", userId);
    }

    /**
     * 审核学生认证
     */
    @Transactional
    public void reviewVerification(Long userId, ReviewVerificationRequest request) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND, "用户不存在");
        }

        int result = userMapper.updateVerificationReview(userId, request.getVerificationStatus(),
                request.getVerificationRemark());
        if (result <= 0) {
            throw new BusinessException("审核失败");
        }

        log.info("管理员审核学生认证：userId={}, status={}", userId, request.getVerificationStatus());
    }

    /**
     * 获取用户认证状态
     */
    public VerificationStatusVO getVerificationStatus(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND, "用户不存在");
        }

        VerificationStatusVO vo = new VerificationStatusVO();
        vo.setVerificationStatus(user.getVerificationStatus());
        vo.setVerificationImage(user.getVerificationImage());
        vo.setVerificationTime(user.getVerificationTime());
        vo.setVerificationRemark(user.getVerificationRemark());
        return vo;
    }

    /**
     * 获取待审核认证列表
     */
    public List<VerificationRecordVO> getPendingVerifications() {
        List<User> users = userMapper.findByVerificationStatus(1);
        return users.stream().map(user -> {
            VerificationRecordVO vo = new VerificationRecordVO();
            vo.setUserId(user.getId());
            vo.setUsername(user.getUsername());
            vo.setVerificationStatus(user.getVerificationStatus());
            vo.setVerificationImage(user.getVerificationImage());
            vo.setVerificationTime(user.getVerificationTime());
            vo.setVerificationRemark(user.getVerificationRemark());
            return vo;
        }).collect(Collectors.toList());
    }
}
