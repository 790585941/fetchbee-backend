# 快速启动指南

## 🚀 5分钟快速启动

### Step 1: 准备环境

确保已安装：
- ✅ JDK 17+
- ✅ Maven 3.6+
- ✅ MySQL 8.0+
- ✅ Redis 6.0+

### Step 2: 启动MySQL和Redis

**启动MySQL**
```bash
# Windows
net start mysql

# Linux/Mac
sudo systemctl start mysql
# 或
sudo service mysql start
```

**启动Redis**
```bash
# Windows
redis-server.exe

# Linux/Mac
redis-server
# 或
sudo systemctl start redis
```

### Step 3: 初始化数据库

```bash
# 进入MySQL
mysql -u root -p

# 执行建表脚本（方式1：在MySQL命令行中）
source db/schema.sql

# 或者（方式2：直接用命令行）
mysql -u root -p < db/schema.sql

# 可选：导入测试数据
mysql -u root -p < db/test-data.sql
```

### Step 4: 修改配置文件

编辑 `src/main/resources/application.properties`：

```properties
# 修改数据库密码
spring.datasource.password=你的MySQL密码

# 如果Redis有密码，修改这里
spring.data.redis.password=你的Redis密码
```

### Step 5: 启动项目

```bash
# 方式1：使用Maven
mvn clean install
mvn spring-boot:run

# 方式2：使用IDE
# 直接运行 FetchbeeBackendApplication.java 的 main 方法
```

看到以下日志说明启动成功：
```
Started FetchbeeBackendApplication in X.XXX seconds
```

访问：http://localhost:8080

---

## 📝 测试接口

### 方式1：使用Postman

1. 导入 `fetchbee-api-test.postman_collection.json`
2. 按顺序执行：
   - ✅ 用户注册
   - ✅ 用户登录（会自动保存Token）
   - ✅ 获取用户信息
   - ✅ 更新用户信息
   - ✅ 修改密码
   - ✅ 用户登出

### 方式2：使用curl命令

**1. 注册用户**
```bash
curl -X POST http://localhost:8080/user/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"123456"}'
```

**2. 登录获取Token**
```bash
curl -X POST http://localhost:8080/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"123456"}'
```

响应示例：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": 1,
    "username": "testuser",
    "balance": 0.00
  }
}
```

**3. 获取用户信息（需要Token）**
```bash
curl -X GET http://localhost:8080/user/info \
  -H "Authorization: Bearer 你的token"
```

---

## ❓ 常见问题

### 1. 启动报错：`Access denied for user 'root'@'localhost'`

**原因**：数据库密码错误

**解决**：修改 `application.properties` 中的数据库密码

---

### 2. 启动报错：`Could not connect to Redis`

**原因**：Redis未启动

**解决**：
```bash
# 检查Redis是否启动
redis-cli ping
# 返回 PONG 说明正常

# 如果未启动，执行：
redis-server
```

---

### 3. 接口返回401：`未登录，请先登录`

**原因**：未传Token或Token过期

**解决**：
1. 先调用 `/user/login` 获取Token
2. 在请求头中添加：`Authorization: Bearer {token}`

---

### 4. 启动报错：`Table 'fetchbee.user' doesn't exist`

**原因**：数据库表未创建

**解决**：执行 `db/schema.sql` 脚本

---

### 5. 如何重置数据库？

```bash
# 删除数据库
mysql -u root -p -e "DROP DATABASE IF EXISTS fetchbee;"

# 重新创建
mysql -u root -p < db/schema.sql

# 导入测试数据（可选）
mysql -u root -p < db/test-data.sql
```

---

## 🎯 下一步

用户模块已经可以正常运行！接下来可以：

1. ✅ **测试所有用户接口**：确保注册、登录、信息管理都正常
2. 🔧 **开发订单模块**：实现发布订单、接单、完成订单等功能
3. 💰 **开发余额模块**：实现充值、扣款、转账等功能
4. 📱 **开发前端页面**：使用Vue + 组件库快速搭建

---

## 📞 需要帮助？

- 查看详细文档：`README.md`
- 查看API文档：`README.md` 中的"用户模块API文档"
- 查看数据库设计：`db/schema.sql`

祝开发顺利！🎉

