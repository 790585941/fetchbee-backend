-- 测试数据脚本
USE fetchbee;

-- 清空现有数据
TRUNCATE TABLE balance_record;
TRUNCATE TABLE user;

-- 插入测试用户
-- 密码都是 123456 (MD5加密后: e10adc3949ba59abbe56e057f20f883e)
INSERT INTO `user` (`username`, `password`, `phone`, `address`, `balance`, `status`) VALUES
('testuser1', 'e10adc3949ba59abbe56e057f20f883e', '13800138001', '一号宿舍楼101', 100.00, 1),
('testuser2', 'e10adc3949ba59abbe56e057f20f883e', '13800138002', '二号宿舍楼202', 50.00, 1),
('testuser3', 'e10adc3949ba59abbe56e057f20f883e', '13800138003', '三号宿舍楼303', 200.00, 1);

-- 查询验证
SELECT * FROM user;

