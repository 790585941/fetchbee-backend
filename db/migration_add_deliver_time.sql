-- 数据库迁移脚本：添加deliver_time字段和更新订单状态
-- 执行日期：2025-11-07
-- 说明：实现订单双方确认机制，防止快递丢失

USE fetchbee;

-- 1. 添加送达时间字段
ALTER TABLE `order` 
ADD COLUMN `deliver_time` DATETIME DEFAULT NULL COMMENT '送达时间（接单者标记送达的时间）' 
AFTER `actual_reward`;

-- 2. 添加deliver_time索引，用于定时任务查询
ALTER TABLE `order` 
ADD INDEX `idx_deliver_time` (`deliver_time`);

-- 3. 更新订单状态注释（状态值已变更：3-待确认，4-已完成，5-已取消）
ALTER TABLE `order` 
MODIFY COLUMN `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-待接单，2-已接单，3-待确认，4-已完成，5-已取消';

-- 4. 更新现有数据：将已完成的订单状态从3改为4
UPDATE `order` SET `status` = 4 WHERE `status` = 3;

-- 5. 更新现有数据：将已取消的订单状态从4改为5
UPDATE `order` SET `status` = 5 WHERE `status` = 4;

-- 验证迁移结果
SELECT 
    COUNT(*) as total_orders,
    SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) as pending_orders,
    SUM(CASE WHEN status = 2 THEN 1 ELSE 0 END) as accepted_orders,
    SUM(CASE WHEN status = 3 THEN 1 ELSE 0 END) as delivered_orders,
    SUM(CASE WHEN status = 4 THEN 1 ELSE 0 END) as completed_orders,
    SUM(CASE WHEN status = 5 THEN 1 ELSE 0 END) as cancelled_orders
FROM `order`;
