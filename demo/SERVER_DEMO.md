# 律法先锋 - 快速上手指南

## 一句话说明

**律法先锋**是一个法律AI助手，能帮你：法律咨询、查案例、审合同，写文书。

---

## 你有两个选择

| 方式 | 优点 | 适合场景 |
|------|------|---------|
| **服务器部署** | 外网可访问 | 演示给客户 |
| **本地运行** | 简单快速 | 开发调试、个人使用 |

---

# 方式A：本地运行（推荐新手）

如果服务器连不上，用这个方法！

### 环境要求

- JDK 17+
- Node.js 18+
- PostgreSQL 15+（或使用Docker）
- Redis 7+

### 第一步：安装数据库（用Docker最简单）

如果没有安装PostgreSQL和Redis，运行：

```bash
# Windows PowerShell (管理员)
docker run -d --name xiaoli-postgres -e POSTGRES_DB=xiaoli_legal -e POSTGRES_USER=xiaoli -e POSTGRES_PASSWORD=Xiaoli@2024 -p 5432:5432 postgres:15

docker run -d --name xiaoli-redis -p 6379:6379 redis:7-alpine
```

### 第二步：一键启动

双击运行项目根目录的：
```
start-all.bat
```

等待几分钟后，浏览器会自动打开 http://localhost:3000

### 第三步：确认运行成功

看到前端页面说明成功了！

### 常见问题

| 问题 | 解决方法 |
|------|---------|
| Docker未运行 | 启动Docker Desktop |
| 端口被占用 | 停止其他占用5432、6379端口的程序 |
| 前端打不开 | 检查Node.js是否安装成功 |

---

# 方式B：服务器部署

## 服务器信息

| 项目 | 值 |
|------|-----|
| 服务器IP | `101.42.47.231` |
| SSH端口 | `22` |
| 用户名 | `ubuntu` |

---

## 第一步：连接服务器

### 如果你有私钥文件

打开终端/PowerShell，运行：

```bash
ssh -i C:\Users\你的用户名\.ssh\your-key.pem ubuntu@101.42.47.231
```

**Windows设置私钥权限：**
```powershell
icacls C:\Users\你的用户名\.ssh\your-key.pem /inheritance:r /grant:r "$env:USERNAME:R"
```

### 如果你用密码登录

```bash
ssh ubuntu@101.42.47.231
# 然后输入密码提示
```

---

## 第二步：一键启动所有服务

连接服务器后，**复制粘贴这条命令**：

```bash
cd /opt/xiaoli && bash start-all.sh
```

**没有start-all.sh？** 复制下面这段保存：

```bash
#!/bin/bash
mkdir -p /opt/xiaoli/logs

# 法律咨询服务 (端口18081)
nohup java -Xms256m -Xmx512m -jar /opt/xiaoli/backend/ms-consult/target/ms-consult-1.0.0.jar \
  --server.port=18081 \
  --spring.datasource.url=jdbc:postgresql://localhost:5432/xiaoli_legal \
  --spring.datasource.username=xiaoli \
  --spring.datasource.password=Xiaoli@2024 \
  > /opt/xiaoli/logs/consult.log 2>&1 &

# 文书服务 (端口18082)
nohup java -Xms256m -Xmx512m -jar /opt/xiaoli/backend/ms-document/target/ms-document-1.0.0.jar \
  --server.port=18082 \
  --spring.datasource.url=jdbc:postgresql://localhost:5432/xiaoli_legal \
  --spring.datasource.username=xiaoli \
  --spring.datasource.password=Xiaoli@2024 \
  > /opt/xiaoli/logs/document.log 2>&1 &

# 案例检索服务 (端口18083)
nohup java -Xms256m -Xmx512m -jar /opt/xiaoli/backend/ms-case/target/ms-caseinfo-1.0.0.jar \
  --server.port=18083 \
  --spring.datasource.url=jdbc:postgresql://localhost:5432/xiaoli_legal \
  --spring.datasource.username=xiaoli \
  --spring.datasource.password=Xiaoli@2024 \
  > /opt/xiaoli/logs/case.log 2>&1 &

echo "所有服务启动中，等待15秒..."
sleep 15
```

**保存脚本：**
```bash
nano /opt/xiaoli/start-all.sh
# 粘贴内容后按 Ctrl+X，再按 Y 保存
chmod +x /opt/xiaoli/start-all.sh
```

---

## 第三步：确认服务正常

**复制粘贴这条命令：**

```bash
curl -s http://localhost:18081/actuator/health
```

**看到 `{"status":"UP"}` 就是成功了！**

如果看到 `Connection refused`，说明服务还没启动好，等15秒再试。

---

## 第四步：开放防火墙（只需做一次）

1. 打开浏览器访问：https://console.cloud.tencent.com/lighthouse
2. 登录账号
3. 点击你的服务器
4. 点击 **防火墙** 标签
5. 点击 **添加规则**
6. 协议选 **TCP**，端口填 **18081-18089**，备注填"法律AI服务"
7. 点击确定

---

## 第五步：使用服务

在浏览器中打开：

| 功能 | 访问地址 |
|------|---------|
| 法律咨询API | http://101.42.47.231:18081 |
| 文书服务API | http://101.42.47.231:18082 |
| 案例检索API | http://101.42.47.231:18083 |

---

## 第六步：测试AI对话（快速验证）

**复制粘贴这条命令：**

```bash
curl -X POST http://localhost:18081/api/consult/chat \
  -H "Content-Type: application/json" \
  -d '{"messages":[{"role":"user","content":"你好，我想咨询劳动纠纷问题"}]}'
```

**看到回复的JSON就是成功了！**

---

## 服务器常用操作

### 查看服务是否在运行
```bash
ps aux | grep java
```

### 查看实时日志
```bash
tail -f /opt/xiaoli/logs/consult.log
```

### 停止所有服务
```bash
pkill -f java
```

### 重启单个服务（比如咨询）
```bash
pkill -f ms-consult
# 然后重新运行启动命令
```

### 更新代码（拉取最新版本）
```bash
cd /opt/xiaoli
git pull origin main
cd backend
mvn clean package -DskipTests
# 然后重启服务
```

---

## 常见问题

| 问题 | 解决方法 |
|------|---------|
| 服务器连不上 | 检查SSH连接是否正确，私钥权限是否设置 |
| curl报错Connection refused | 服务启动中，等15秒再试 |
| 端口访问不了 | 去腾讯云控制台开放防火墙端口 |
| 内存不足 | 减少 -Xms 和 -Xmx 的值（改成128m） |
| 本地Docker报错 | 确保Docker Desktop已启动 |

---

## 快速联系

- 服务器IP：**101.42.47.231**
- 主要端口：**18081**（咨询）、**18082**（文书）、**18083**（案例）

---

**搞定！有任何问题随时问。**
