# 恋爱聊天助手 - 启动指南

## 1. 准备数据库

### 方式1：自动建表（推荐）
```bash
# 在MySQL中执行
mysql -uroot -p
CREATE DATABASE IF NOT EXISTS loveai DEFAULT CHARACTER SET utf8mb4;
exit;
```

### 方式2：手动建表
```bash
# 执行完整SQL脚本
mysql -uroot -p < docs/init.sql
```

## 2. 配置数据库密码

编辑 `src/main/resources/application.yml`：
```yaml
spring:
  datasource:
    username: root
    password: 你的MySQL密码  # 修改这里
```

## 3. 启动项目

```bash
# 方式1：Maven命令
mvn clean install
mvn spring-boot:run

# 方式2：IDE运行
# 直接运行 ChatAiApplication.java
```

## 4. 验证表是否创建

启动成功后，在MySQL中查看：
```sql
USE loveai;
SHOW TABLES;

-- 应该看到4个表：
-- users
-- target_profile
-- chat_history
-- suggestion_log
```

## 5. 测试接口

在浏览器打开：`file:///E:/studyzero/loveai/test.html`

测试流程：
1. 注册用户
2. 登录获取Token
3. 测试聊天建议
4. 测试对话分析
5. 测试情境模拟
6. 测试档案管理

## 常见问题

### Q: 启动报错 "Access denied for user 'root'"
A: 修改 application.yml 中的数据库密码

### Q: 启动报错 "Unknown database 'loveai'"
A: 先手动创建数据库：`CREATE DATABASE loveai;`

### Q: 表没有自动创建
A: 检查 application.yml 中 `ddl-auto: update` 配置是否正确

### Q: JWT Token 验证失败
A: 检查 application.yml 中 `jwt.secret` 配置，确保长度足够（至少32字符）

# 🎉 恋爱聊天助手 - 使用指南

## 📱 应用访问地址

启动后端项目后，在浏览器访问：

```
http://localhost:8089/
```

会自动跳转到登录页面。

祝你使用愉快！