# 快递蜂核心业务流程测试指南

## 📋 测试前准备

### 1. 初始化数据库
```sql
-- 在MySQL客户端执行
SOURCE db/schema.sql;

-- 或者手动创建订单表（如果已有user和balance_record表）
USE fetchbee;

CREATE TABLE IF NOT EXISTS `order` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    `order_no` VARCHAR(32) NOT NULL UNIQUE COMMENT '订单号',
    `publisher_id` BIGINT NOT NULL COMMENT '发布者ID',
    `receiver_id` BIGINT DEFAULT NULL COMMENT '接单者ID',
    `express_company` VARCHAR(50) DEFAULT NULL COMMENT '快递公司',
    `pickup_code` VARCHAR(50) NOT NULL COMMENT '取件码',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '快递描述',
    `pickup_address` VARCHAR(255) NOT NULL COMMENT '取件地址',
    `delivery_address` VARCHAR(255) NOT NULL COMMENT '送达地址',
    `reward` DECIMAL(10, 2) NOT NULL COMMENT '报酬金额',
    `deadline` DATETIME NOT NULL COMMENT '截止时间',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-待接单，2-已接单，3-已完成，4-已取消',
    `actual_reward` DECIMAL(10, 2) DEFAULT NULL COMMENT '实际支付金额',
    `complete_time` DATETIME DEFAULT NULL COMMENT '完成时间',
    `cancel_reason` VARCHAR(255) DEFAULT NULL COMMENT '取消原因',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_order_no` (`order_no`),
    INDEX `idx_publisher_id` (`publisher_id`),
    INDEX `idx_receiver_id` (`receiver_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_create_time` (`create_time`),
    FOREIGN KEY (`publisher_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`receiver_id`) REFERENCES `user`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';
```

### 2. 准备两个测试用户
```sql
-- 插入两个测试用户（密码都是123456）
INSERT INTO `user` (`username`, `password`, `phone`, `address`, `balance`) VALUES
('zhangsan', 'e10adc3949ba59abbe56e057f20f883e', '13800138001', '一号宿舍楼101', 100.00),
('lisi', 'e10adc3949ba59abbe56e057f20f883e', '13800138002', '二号宿舍楼202', 50.00);
```

### 3. 启动项目
```bash
mvn spring-boot:run
```

---

## 🎯 完整业务流程测试

### 场景：张三发布订单，李四接单并完成

---

## 第一步：注册/登录用户

### 1.1 张三登录
```
POST http://localhost:8080/user/login
Content-Type: application/json

{
  "username": "zhangsan",
  "password": "123456"
}
```

**返回示例：**
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGci...",
    "userId": 1,
    "username": "zhangsan",
    "balance": 100.00
  }
}
```

**⚠️ 保存张三的Token → `token_zhangsan`**

---

### 1.2 李四登录
```
POST http://localhost:8080/user/login
Content-Type: application/json

{
  "username": "lisi",
  "password": "123456"
}
```

**⚠️ 保存李四的Token → `token_lisi`**

---

## 第二步：张三发布订单

### 2.1 发布代取订单
```
POST http://localhost:8080/order/create
Authorization: Bearer {token_zhangsan}
Content-Type: application/json

{
  "expressCompany": "菜鸟驿站",
  "pickupCode": "8888",
  "description": "一件运动鞋",
  "pickupAddress": "东门菜鸟驿站",
  "reward": 3.00,
  "deadline": "2025-10-23T18:00:00"
}
```

**返回示例：**
```json
{
  "code": 200,
  "message": "发布成功",
  "data": {
    "orderId": 1
  }
}
```

**✅ 预期结果：**
- 订单创建成功
- 张三余额从100元扣除3元 → 剩余97元
- balance_record表新增一条扣款记录

---

### 2.2 验证张三余额
```
GET http://localhost:8080/user/info
Authorization: Bearer {token_zhangsan}
```

**返回示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "balance": 97.00  // ← 已扣除3元
  }
}
```

---

### 2.3 查看我发布的订单
```
GET http://localhost:8080/order/my-published
Authorization: Bearer {token_zhangsan}
```

**返回示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "orderNo": "FO20251022150000abc123",
      "publisherName": "zhangsan",
      "expressCompany": "菜鸟驿站",
      "pickupCode": "8888",
      "description": "一件运动鞋",
      "pickupAddress": "东门菜鸟驿站",
      "deliveryAddress": "一号宿舍楼101",
      "reward": 3.00,
      "status": 1,
      "statusDesc": "待接单"
    }
  ]
}
```

---

## 第三步：李四查看并接单

### 3.1 查看待接单列表
```
GET http://localhost:8080/order/pending
Authorization: Bearer {token_lisi}
```

**返回示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "publisherName": "zhangsan",
      "pickupCode": "***",  // ← 未接单前看不到取件码
      "description": "一件运动鞋",
      "reward": 3.00,
      "status": 1,
      "statusDesc": "待接单"
    }
  ]
}
```

---

### 3.2 李四接单
```
POST http://localhost:8080/order/1/accept
Authorization: Bearer {token_lisi}
```

**返回示例：**
```json
{
  "code": 200,
  "message": "接单成功",
  "data": null
}
```

**✅ 预期结果：**
- 订单状态变为"已接单"
- 接单者ID记录为李四的ID
- 李四现在可以看到取件码了

---

### 3.3 查看订单详情（可以看到取件码）
```
GET http://localhost:8080/order/1
Authorization: Bearer {token_lisi}
```

**返回示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "pickupCode": "8888",  // ← 接单后可以看到取件码
    "status": 2,
    "statusDesc": "已接单",
    "receiverName": "lisi"
  }
}
```

---

### 3.4 查看我接的订单列表
```
GET http://localhost:8080/order/my-accepted
Authorization: Bearer {token_lisi}
```

---

## 第四步：李四完成订单

### 4.1 完成代取
```
POST http://localhost:8080/order/1/complete
Authorization: Bearer {token_lisi}
```

**返回示例：**
```json
{
  "code": 200,
  "message": "订单已完成",
  "data": null
}
```

**✅ 预期结果：**
- 订单状态变为"已完成"
- 李四余额从50元增加3元 → 剩余53元
- balance_record表新增一条收入记录
- 记录完成时间

---

### 4.2 验证李四余额
```
GET http://localhost:8080/user/info
Authorization: Bearer {token_lisi}
```

**返回示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "balance": 53.00  // ← 增加了3元
  }
}
```

---

## 🎯 其他测试场景

### 场景2：取消订单（退款）

#### 1. 张三发布新订单
```
POST http://localhost:8080/order/create
Authorization: Bearer {token_zhangsan}
Content-Type: application/json

{
  "pickupCode": "9999",
  "description": "文件",
  "pickupAddress": "西门快递点",
  "reward": 2.00,
  "deadline": "2025-10-23T20:00:00"
}
```

#### 2. 张三取消订单
```
POST http://localhost:8080/order/2/cancel?reason=快递已自取
Authorization: Bearer {token_zhangsan}
```

**✅ 预期结果：**
- 订单状态变为"已取消"
- 张三余额退回2元
- balance_record表新增一条退款记录

---

### 场景3：超时完成（惩罚机制）

#### 测试方法：
发布订单时设置一个很近的截止时间（比如当前时间+1分钟），等过期后再完成。

```json
{
  "deadline": "2025-10-22T15:01:00"  // 1分钟后
}
```

等待过期后，李四完成订单：
```
POST http://localhost:8080/order/3/complete
Authorization: Bearer {token_lisi}
```

**✅ 预期结果：**
- 订单完成
- 实际支付金额 = 报酬 × 80%
- 李四只能收到打折后的金额

---

### 场景4：不能接自己的单

```
POST http://localhost:8080/order/1/accept
Authorization: Bearer {token_zhangsan}  // 用发布者的token
```

**预期返回：**
```json
{
  "code": 400,
  "message": "不能接自己发布的订单",
  "data": null
}
```

---

### 场景5：余额不足发布订单

#### 1. 李四尝试发布高额订单
```
POST http://localhost:8080/order/create
Authorization: Bearer {token_lisi}
Content-Type: application/json

{
  "pickupCode": "1234",
  "pickupAddress": "快递点",
  "reward": 100.00,  // 超过李四的余额
  "deadline": "2025-10-23T20:00:00"
}
```

**预期返回：**
```json
{
  "code": 1004,
  "message": "余额不足，当前余额：53.00元",
  "data": null
}
```

---

## 📊 数据验证

### 查看余额变动记录
```sql
SELECT 
    br.id,
    u.username,
    br.amount,
    br.balance_before,
    br.balance_after,
    CASE br.type
        WHEN 1 THEN '充值'
        WHEN 2 THEN '发布订单扣款'
        WHEN 3 THEN '完成订单收入'
        WHEN 4 THEN '订单退款'
    END as type_desc,
    br.remark,
    br.create_time
FROM balance_record br
JOIN user u ON br.user_id = u.id
ORDER BY br.create_time DESC;
```

### 查看订单列表
```sql
SELECT 
    o.id,
    o.order_no,
    pub.username as publisher,
    rec.username as receiver,
    o.reward,
    o.actual_reward,
    CASE o.status
        WHEN 1 THEN '待接单'
        WHEN 2 THEN '已接单'
        WHEN 3 THEN '已完成'
        WHEN 4 THEN '已取消'
    END as status_desc,
    o.create_time,
    o.complete_time
FROM `order` o
JOIN user pub ON o.publisher_id = pub.id
LEFT JOIN user rec ON o.receiver_id = rec.id
ORDER BY o.create_time DESC;
```

---

## ✅ 核心业务验证清单

- [x] 用户可以发布订单（余额扣款）
- [x] 其他用户可以查看待接单列表（取件码隐藏）
- [x] 用户可以接单（不能接自己的）
- [x] 接单后可以看到取件码
- [x] 用户可以完成订单（余额转账）
- [x] 超时完成有惩罚（80%支付）
- [x] 可以取消待接单的订单（退款）
- [x] 余额不足无法发布订单
- [x] 已接单的订单无法取消
- [x] 余额变动有完整记录

---

## 🎉 测试完成

如果所有场景都通过，说明核心业务流程已经完整跑通！

接下来可以：
1. 前端开发（Vue + Element Plus）
2. 添加更多功能（充值、评价、消息通知等）
3. 优化性能和安全性

