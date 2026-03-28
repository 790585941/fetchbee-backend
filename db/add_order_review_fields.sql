-- 添加订单审核相关字段
-- 执行时间：2026-02-14

-- 添加审核状态字段
ALTER TABLE `order` ADD COLUMN `review_status` tinyint(0) NOT NULL DEFAULT 0 COMMENT '审核状态：0-待审核，1-已通过，2-已拒绝' AFTER `cancel_reason`;

-- 添加审核备注字段
ALTER TABLE `order` ADD COLUMN `review_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审核备注（拒绝原因）' AFTER `review_status`;

-- 添加审核时间字段
ALTER TABLE `order` ADD COLUMN `review_time` datetime(0) NULL DEFAULT NULL COMMENT '审核时间' AFTER `review_remark`;

-- 添加审核人ID字段
ALTER TABLE `order` ADD COLUMN `reviewer_id` bigint(0) NULL DEFAULT NULL COMMENT '审核人ID' AFTER `review_time`;

-- 添加审核状态索引
ALTER TABLE `order` ADD INDEX `idx_review_status`(`review_status`) USING BTREE;

-- 更新订单状态注释
ALTER TABLE `order` MODIFY COLUMN `status` tinyint(0) NOT NULL DEFAULT 0 COMMENT '状态：0-待审核，1-待接单，2-已接单，3-待确认，4-已完成，5-已取消，6-已拒绝';

-- 将现有订单的审核状态设置为已通过（兼容旧数据）
UPDATE `order` SET `review_status` = 1 WHERE `status` IN (1, 2, 3, 4);

-- 将已取消的订单审核状态也设置为已通过（因为它们是在审核功能之前创建的）
UPDATE `order` SET `review_status` = 1 WHERE `status` = 5;
