package com.example.fetchbeebackend.service;

import com.example.fetchbeebackend.common.ResultCode;
import com.example.fetchbeebackend.dto.ReviewRightsProtectionRequest;
import com.example.fetchbeebackend.dto.SubmitRightsProtectionRequest;
import com.example.fetchbeebackend.entity.Order;
import com.example.fetchbeebackend.entity.User;
import com.example.fetchbeebackend.enums.NotificationType;
import com.example.fetchbeebackend.exception.BusinessException;
import com.example.fetchbeebackend.mapper.OrderMapper;
import com.example.fetchbeebackend.mapper.UserMapper;
import com.example.fetchbeebackend.vo.RightsProtectionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 维权服务类
 */
@Slf4j
@Service
public class RightsProtectionService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private NotificationService notificationService;

    /**
     * 提交维权申请
     */
    @Transactional(rollbackFor = Exception.class)
    public void submitRightsProtection(Long orderId, Long userId, SubmitRightsProtectionRequest request) {
        // 1. 查询订单
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "订单不存在");
        }

        // 2. 检查订单状态：只有已接单和待确认状态可以申请维权
        if (order.getStatus() != 2 && order.getStatus() != 3) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "当前订单状态不允许申请维权");
        }

        // 3. 检查是否已完成
        if (order.getStatus() == 4 || order.getStatus() == 5) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "订单已完成或已取消，无法申请维权");
        }

        // 4. 检查是否是订单相关人员
        String applicant;
        Long notifyUserId;
        if (order.getPublisherId().equals(userId)) {
            applicant = "publisher";
            notifyUserId = order.getReceiverId();
        } else if (order.getReceiverId() != null && order.getReceiverId().equals(userId)) {
            applicant = "receiver";
            notifyUserId = order.getPublisherId();
        } else {
            throw new BusinessException(ResultCode.FORBIDDEN, "只有订单发布者或接单者才能申请维权");
        }

        // 5. 检查维权状态：只允许一方申请，等审核结束后另一方才能申请
        if (order.getRightsStatus() != null && order.getRightsStatus() == 1) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "该订单已有维权申请正在审核中，请等待审核结果");
        }

        // 6. 检查是否已经申请过维权
        if (order.getRightsApplicant() != null && order.getRightsApplicant().equals(applicant)) {
            if (order.getRightsStatus() == 2 || order.getRightsStatus() == 3) {
                throw new BusinessException(ResultCode.PARAM_ERROR, "您已经申请过维权，不能重复申请");
            }
        }

        // 7. 提交维权申请
        int result = orderMapper.submitRightsProtection(
                orderId,
                applicant,
                request.getRightsDescription(),
                request.getRightsImage(),
                LocalDateTime.now()
        );

        if (result <= 0) {
            throw new BusinessException("提交维权申请失败");
        }

        // 8. 通知对方
        if (notifyUserId != null) {
            notificationService.createNotification(
                    notifyUserId,
                    NotificationType.RIGHTS_APPLIED,
                    "对方已申请维权",
                    "订单【" + order.getOrderNo() + "】对方已申请维权，请等待管理员审核",
                    orderId
            );
        }

        log.info("提交维权申请成功：orderId={}, userId={}, applicant={}", orderId, userId, applicant);
    }

    /**
     * 审核维权申请
     */
    @Transactional(rollbackFor = Exception.class)
    public void reviewRightsProtection(Long orderId, ReviewRightsProtectionRequest request) {
        // 1. 查询订单
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "订单不存在");
        }

        // 2. 检查维权状态
        if (order.getRightsStatus() == null || order.getRightsStatus() != 1) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "该订单没有待审核的维权申请");
        }

        // 3. 检查审核状态参数
        if (request.getRightsStatus() != 2 && request.getRightsStatus() != 3) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "维权状态参数错误");
        }

        // 4. 如果是接单者维权通过，必须指定资金流向
        if ("receiver".equals(order.getRightsApplicant()) && request.getRightsStatus() == 2) {
            if (request.getRightsFundTo() == null ||
                (!request.getRightsFundTo().equals("publisher") && !request.getRightsFundTo().equals("receiver"))) {
                throw new BusinessException(ResultCode.PARAM_ERROR, "接单者维权通过时必须指定资金流向");
            }
        }

        // 5. 审核维权
        int result = orderMapper.reviewRightsProtection(
                orderId,
                request.getRightsStatus(),
                request.getRightsRemark(),
                request.getRightsFundTo(),
                LocalDateTime.now()
        );

        if (result <= 0) {
            throw new BusinessException("审核维权失败");
        }

        // 6. 如果维权通过，处理资金和订单状态
        if (request.getRightsStatus() == 2) {
            handleRightsApproved(order, request.getRightsFundTo());
        }

        // 7. 通知双方
        String resultDesc = request.getRightsStatus() == 2 ? "通过" : "不通过";
        notificationService.createNotification(
                order.getPublisherId(),
                NotificationType.RIGHTS_REVIEWED,
                "维权审核完成",
                "订单【" + order.getOrderNo() + "】维权审核结果：" + resultDesc,
                orderId
        );

        if (order.getReceiverId() != null) {
            notificationService.createNotification(
                    order.getReceiverId(),
                    NotificationType.RIGHTS_REVIEWED,
                    "维权审核完成",
                    "订单【" + order.getOrderNo() + "】维权审核结果：" + resultDesc,
                    orderId
            );
        }

        log.info("审核维权成功：orderId={}, rightsStatus={}, fundTo={}",
                orderId, request.getRightsStatus(), request.getRightsFundTo());
    }

    /**
     * 处理维权通过的情况
     */
    private void handleRightsApproved(Order order, String fundTo) {
        // 取消订单
        orderMapper.cancelOrder(order.getId(), "维权通过，订单取消");

        // 根据申请人和资金流向处理资金
        if ("publisher".equals(order.getRightsApplicant())) {
            // 发布者申请维权通过，退款给发布者
            balanceService.refund(order.getPublisherId(), order.getReward(), order.getId(),
                    "维权通过退款：" + order.getOrderNo());
        } else if ("receiver".equals(order.getRightsApplicant())) {
            // 接单者申请维权通过，根据管理员指定的资金流向处理
            if ("publisher".equals(fundTo)) {
                // 退款给发布者
                balanceService.refund(order.getPublisherId(), order.getReward(), order.getId(),
                        "维权通过退款：" + order.getOrderNo());
            } else if ("receiver".equals(fundTo)) {
                // 支付给接单者
                balanceService.transfer(order.getReceiverId(), order.getReward(), order.getId(),
                        "维权通过收入：" + order.getOrderNo());
            }
        }
    }

    /**
     * 查询维权状态
     */
    public RightsProtectionVO getRightsProtectionStatus(Long orderId, Long userId) {
        // 1. 查询订单
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "订单不存在");
        }

        // 2. 检查是否是订单相关人员
        if (!order.getPublisherId().equals(userId) &&
            (order.getReceiverId() == null || !order.getReceiverId().equals(userId))) {
            throw new BusinessException(ResultCode.FORBIDDEN, "只有订单发布者或接单者才能查看维权信息");
        }

        // 3. 构造VO
        return convertToVO(order);
    }

    /**
     * 查询待审核维权列表（管理员）
     */
    public List<RightsProtectionVO> getPendingRightsProtectionList() {
        List<Order> orders = orderMapper.findPendingRightsProtectionOrders();
        List<RightsProtectionVO> voList = new ArrayList<>();
        for (Order order : orders) {
            voList.add(convertToVO(order));
        }
        return voList;
    }

    /**
     * 转换为VO
     */
    private RightsProtectionVO convertToVO(Order order) {
        RightsProtectionVO vo = new RightsProtectionVO();
        vo.setOrderId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setRightsStatus(order.getRightsStatus());
        vo.setRightsApplicant(order.getRightsApplicant());
        vo.setRightsDescription(order.getRightsDescription());
        vo.setRightsImage(order.getRightsImage());
        vo.setRightsApplyTime(order.getRightsApplyTime());
        vo.setRightsReviewTime(order.getRightsReviewTime());
        vo.setRightsRemark(order.getRightsRemark());
        vo.setRightsFundTo(order.getRightsFundTo());
        vo.setReward(order.getReward());

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

        // 查询申请人姓名
        if ("publisher".equals(order.getRightsApplicant())) {
            vo.setApplicantName(vo.getPublisherName());
        } else if ("receiver".equals(order.getRightsApplicant())) {
            vo.setApplicantName(vo.getReceiverName());
        }

        return vo;
    }
}
