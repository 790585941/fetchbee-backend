-- 修改 verification_image 字段类型，支持存储 base64 编码的图片
-- base64 编码的图片通常很长，需要使用 LONGTEXT 类型

ALTER TABLE `user` MODIFY COLUMN `verification_image` LONGTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '学生证照片（base64编码）';

-- 说明：
-- LONGTEXT 可以存储最多 4GB 的文本数据
-- 足够存储 base64 编码的图片
