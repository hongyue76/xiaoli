# 律法先锋 - 5分钟快速上手指南

## 🎯 一句话说明

**律法先锋**是一个法律AI助手，能帮你：法律咨询、查案例、审合同、写文书。

---

## 📍 服务器信息

| 项目 | 值 |
|------|-----|
| 服务器IP | 101.42.47.231 |
| SSH端口 | 22 |
| 用户名 | ubuntu |
| 私钥文件 | 参考之前的配置 |

---

## 🚀 第一步：连接服务器

打开终端/PowerShell，运行：

```bash
ssh -i 你的私钥文件路径 ubuntu@101.42.47.231
```

**不会连接？** 私钥需要先设置权限：
```bash
# Windows PowerShell
icacls 私钥文件路径 /inheritance:r /grant:r "$env:USERNAME:R"

# Linux/Mac
chmod 400 私钥文件路径
```

---

## 🚀 第二步：一键启动所有服务

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

echo "✅ 所有服务启动中..."

# 等待10秒后检查状态
sleep 10
curl -s http://localhost:18081/actuator/health
curl -s http://localhost:18082/actuator/health
curl -s http://localhost:18083/actuator/health
```

保存为 `start-all.sh`：
```bash
nano /opt/xiaoli/start-all.sh
# 粘贴内容后按 Ctrl+X，再按 Y 保存
chmod +x /opt/xiaoli/start-all.sh
```

---

## ✅ 第三步：确认服务正常

**复制粘贴这条命令：**

```bash
curl -s http://localhost:18081/actuator/health
```

**看到 `{"status":"UP"}` 就是成功了！**

如果看到 `Connection refused`，说明服务还没启动好，等15秒再试。

---

## 🌐 第四步：开放防火墙（只需做一次）

1. 打开浏览器访问：https://console.cloud.tencent.com/lighthouse
2. 登录账号
3. 点击你的服务器
4. 点击 **防火墙** 标签
5. 点击 **添加规则**
6. 协议选 **TCP**，端口填 **18081-18089**，备注填"法律AI服务"
7. 点击确定

---

## 📱 第五步：使用服务

在浏览器中打开：

| 功能 | 访问地址 |
|------|---------|
| 法律咨询API | http://101.42.47.231:18081 |
| 文书服务API | http://101.42.47.231:18082 |
| 案例检索API | http://101.42.47.231:18083 |

---

## 💬 第六步：测试AI对话（快速验证）

**复制粘贴这条命令：**

```bash
curl -X POST http://localhost:18081/api/consult/chat \
  -H "Content-Type: application/json" \
  -d '{"messages":[{"role":"user","content":"你好，我想咨询劳动纠纷问题"}]}'
```

**看到回复的JSON就是成功了！** 🎉

---

## 🔧 常用操作（收藏备用）

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

## ❓ 常见问题

| 问题 | 解决方法 |
|------|---------|
| 连不上服务器 | 检查私钥权限是否正确设置 |
| curl报错Connection refused | 服务启动中，等15秒再试 |
| 端口访问不了 | 去腾讯云控制台开放防火墙端口 |
| 内存不足 | 减少 -Xms 和 -Xmx 的值（改成128m） |

---

## 📞 快速联系

- 服务器IP：**101.42.47.231**
- 主要端口：**18081**（咨询）、**18082**（文书）、**18083**（案例）

---

**搞定！有任何问题随时问。** 🎉
