-- 添加消息通知表
-- 执行方式：mysql -u root -p fetchbee < db/migration_add_notification.sql

USE fetchbee;

-- ============================================
-- 消息通知表
-- ============================================
CREATE TABLE IF NOT EXISTS `notification` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '通知ID',
    `user_id` BIGINT NOT NULL COMMENT '接收通知的用户ID',
    `type` VARCHAR(50) NOT NULL COMMENT '通知类型（ORDER_ACCEPTED/ORDER_DELIVERED等）',
    `title` VARCHAR(100) NOT NULL COMMENT '通知标题',
    `content` VARCHAR(500) NOT NULL COMMENT '通知内容',
    `order_id` BIGINT DEFAULT NULL COMMENT '关联订单ID',
    `is_read` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_is_read` (`is_read`),
    INDEX `idx_create_time` (`create_time`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息通知表';

-- 插入测试数据（可选）
-- INSERT INTO `notification` (`user_id`, `type`, `title`, `content`, `order_id`, `is_read`) VALUES
-- (1, 'ORDER_ACCEPTED', '订单已被接单', '您的订单【FO20241118001】已被 张三 接单', 1, 0),
-- (2, 'ORDER_COMPLETED', '订单已完成', '订单【FO20241118001】已完成，报酬 ¥5.00 已到账', 1, 0);

