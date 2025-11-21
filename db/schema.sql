-- 快递蜂数据库设计
-- 创建数据库
CREATE DATABASE IF NOT EXISTS fetchbee DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE fetchbee;

-- ============================================
-- 用户表
-- ============================================
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（加密后）',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `address` VARCHAR(255) DEFAULT NULL COMMENT '地址（宿舍/教学楼）',
    `balance` DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '余额',
    `avatar` MEDIUMTEXT DEFAULT NULL COMMENT '头像URL',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_username` (`username`),
    INDEX `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================
-- 余额变动记录表
-- ============================================
CREATE TABLE IF NOT EXISTS `balance_record` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `amount` DECIMAL(10, 2) NOT NULL COMMENT '变动金额（正数为收入，负数为支出）',
    `balance_before` DECIMAL(10, 2) NOT NULL COMMENT '变动前余额',
    `balance_after` DECIMAL(10, 2) NOT NULL COMMENT '变动后余额',
    `type` TINYINT NOT NULL COMMENT '类型：1-充值，2-发布订单扣款，3-完成订单收入，4-订单退款',
    `order_id` BIGINT DEFAULT NULL COMMENT '关联订单ID（如果有）',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_order_id` (`order_id`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='余额变动记录表';

-- ============================================
-- 订单表
-- ============================================
CREATE TABLE IF NOT EXISTS `order` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    `order_no` VARCHAR(32) NOT NULL UNIQUE COMMENT '订单号',
    `publisher_id` BIGINT NOT NULL COMMENT '发布者ID',
    `receiver_id` BIGINT DEFAULT NULL COMMENT '接单者ID',
    `express_company` VARCHAR(50) DEFAULT NULL COMMENT '快递公司',
    `pickup_code` VARCHAR(50) NOT NULL COMMENT '取件码',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '快递描述',
    `pickup_address` VARCHAR(255) NOT NULL COMMENT '取件地址',
    `delivery_address` VARCHAR(255) NOT NULL COMMENT '送达地址（发布者地址）',
    `reward` DECIMAL(10, 2) NOT NULL COMMENT '报酬金额',
    `deadline` DATETIME NOT NULL COMMENT '截止时间',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-待接单，2-已接单，3-待确认，4-已完成，5-已取消',
    `actual_reward` DECIMAL(10, 2) DEFAULT NULL COMMENT '实际支付金额（超时可能打折）',
    `deliver_time` DATETIME DEFAULT NULL COMMENT '送达时间（接单者标记送达的时间）',
    `complete_time` DATETIME DEFAULT NULL COMMENT '完成时间',
    `cancel_reason` VARCHAR(255) DEFAULT NULL COMMENT '取消原因',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_order_no` (`order_no`),
    INDEX `idx_publisher_id` (`publisher_id`),
    INDEX `idx_receiver_id` (`receiver_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_deliver_time` (`deliver_time`),
    INDEX `idx_create_time` (`create_time`),
    FOREIGN KEY (`publisher_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`receiver_id`) REFERENCES `user`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- ============================================
-- 初始化测试数据（可选）
-- ============================================
-- 插入测试用户（密码为：123456，MD5加密：e10adc3949ba59abbe56e057f20f883e）
-- INSERT INTO `user` (`username`, `password`, `phone`, `address`, `balance`) VALUES
-- ('testuser1', 'e10adc3949ba59abbe56e057f20f883e', '13800138001', '一号宿舍楼101', 100.00),
-- ('testuser2', 'e10adc3949ba59abbe56e057f20f883e', '13800138002', '二号宿舍楼202', 50.00);

