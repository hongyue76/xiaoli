# 律法先锋 - 产品演示文档

## 一、项目简介

**律法先锋**是一款基于人工智能的法律咨询平台，为用户提供智能法律问答、案例检索、法规查询和文书生成等服务。

### 核心功能
- 🤖 **AI 法律咨询** - 智能回答法律问题
- 📚 **案例检索** - 快速查找相关判例
- 📖 **法规查询** - 检索相关法律法规
- 📝 **文书生成** - 自动生成法律文书

### 技术架构
- **后端**: Java 17 + Spring Boot 3.2 + MyBatis-Plus
- **数据库**: PostgreSQL 15 + Redis 7
- **AI 服务**: DeepSeek API + 得理法搜 API
- **部署**: Docker 容器化

---

## 二、功能演示

### 演示步骤

#### 步骤 1：启动服务
```
cd backend/ms-consult
java -jar target/ms-consult-1.0.0.jar
```

#### 步骤 2：访问服务
- 服务地址: http://localhost:18081
- 健康检查: http://localhost:18081/actuator/health

#### 步骤 3：测试 API

**健康检查**
```bash
curl http://localhost:18081/actuator/health
```

**法律咨询测试**
```bash
curl -X POST http://localhost:18081/api/consult/chat -H "Content-Type: application/json" -d "{\"messages\":[{\"role\":\"user\",\"content\":\"我签的合同有法律效力吗？\"}]}"
```

---

## 三、视频脚本

### 开场 (0:00-0:30)
"大家好，今天给大家介绍一款法律 AI 智能助手 - 律法先锋..."

### 功能演示 (0:30-3:00)
1. 启动后端服务
2. 演示健康检查接口
3. 演示法律咨询接口
4. 演示案例检索接口

### 结尾 (3:00-3:30)
"以上就是律法先锋的主要功能演示，感谢观看！"

---

## 四、Demo 打包

已编译 JAR: `backend/ms-consult/target/ms-consult-1.0.0.jar`
测试脚本: `backend/test-api.bat`
测试页面: `backend/test-api.html`
