# 腾讯云学生机部署指南

## 目录

- [一、购买腾讯云学生机](#一购买腾讯云学生机)
- [二、服务器初始化配置](#二服务器初始化配置)
- [三、上传项目到服务器](#三上传项目到服务器)
- [四、一键部署](#四一键部署)
- [五、配置域名（可选）](#五配置域名可选)
- [六、常见问题](#六常见问题)

---

## 一、购买腾讯云学生机

### 1.1 访问学生机页面

```
https://cloud.tencent.com/act/campus
```

### 1.2 选择配置

| 配置项 | 推荐选择 | 说明 |
|--------|----------|------|
| **地域** | 广州/上海 | 延迟最低 |
| **系统** | Ubuntu 22.04 LTS | 稳定易用 |
| **套餐** | ¥10/月 学生套餐 | 2核2G 够用 |

### 1.3 支付方式

支持 **支付宝**、微信支付，无需信用卡！

---

## 二、服务器初始化配置

### 2.1 连接服务器

在本地终端执行：

```bash
# Windows 用户可使用 PowerShell 或 Git Bash
ssh root@你的服务器IP
```

### 2.2 修改 SSH 密码

```bash
# 首次登录会要求修改密码
passwd root
```

### 2.3 配置安全组（重要！）

在腾讯云控制台操作：

```
控制台 → 云服务器 → 安全组 → 入站规则

添加规则：
┌────────────────────────────────────────────┐
│ 协议端口 │ 来源     │ 策略   │ 说明        │
├────────────────────────────────────────────┤
│ TCP:80   │ 0.0.0.0/0 │ 允许  │ HTTP访问   │
│ TCP:443  │ 0.0.0.0/0 │ 允许  │ HTTPS访问  │
│ TCP:22   │ 你的IP    │ 允许  │ SSH连接    │
└────────────────────────────────────────────┘
```

---

## 三、上传项目到服务器

### 3.1 方法一：使用 SCP（推荐）

在本地电脑执行（非服务器）：

```bash
# 进入项目目录
cd d:/me/project/xiaoli

# 上传到服务器
scp -r deployment/docker root@你的服务器IP:/root/

# 或者只上传必要文件
scp -r deployment/docker/* root@你的服务器IP:/root/docker/
```

### 3.2 方法二：使用 Git（推荐）

在服务器上执行：

```bash
# 在服务器上克隆仓库
ssh root@你的服务器IP
git clone https://github.com/hongyue76/xiaoli.git
cd xiaoli/deployment/docker
```

### 3.3 方法三：打包上传

```bash
# 本地打包
cd d:/me/project/xiaoli
tar -czvf xiaoli-deploy.tar.gz deployment/

# 上传
scp xiaoli-deploy.tar.gz root@你的服务器IP:/root/

# 服务器解压
ssh root@你的服务器IP
cd /root
tar -xzvf xiaoli-deploy.tar.gz
```

---

## 四、一键部署

### 4.1 方式一：使用部署脚本（推荐）

```bash
# 在服务器上执行
cd /root/docker
bash deploy.sh
```

脚本会自动：
1. 安装 Docker
2. 安装 Docker Compose
3. 配置环境变量
4. 启动所有服务

### 4.2 方式二：手动部署

```bash
# 1. 进入部署目录
cd /root/docker

# 2. 创建 .env 文件
cat > .env << 'EOF'
DB_PASSWORD=Xiaoli@2024
DEEPSEEK_API_KEY=你的DeepSeek密钥
DELILEGAL_APP_ID=你的得理APP_ID
DELILEGAL_SECRET=你的得理SECRET
EOF

# 3. 启动服务
docker-compose -f docker-compose.simple.yml up -d --build

# 4. 查看状态
docker-compose -f docker-compose.simple.yml ps
```

### 4.3 验证部署

```bash
# 查看容器状态
docker ps

# 查看日志
docker-compose -f docker-compose.simple.yml logs -f

# 测试访问
curl http://localhost
```

---

## 五、配置域名（可选）

### 5.1 购买域名

推荐：阿里云/腾讯云域名，¥20-30/年

### 5.2 配置 DNS 解析

```
域名管理 → DNS解析 → 添加记录

┌──────────────────────────────────────┐
│ 主机记录 │ 记录类型 │ 记录值        │
├──────────────────────────────────────┤
│ www     │ A       │ 你的服务器IP   │
│ @       │ A       │ 你的服务器IP   │
└──────────────────────────────────────┘
```

### 5.3 申请免费 SSL 证书

使用 Let's Encrypt 免费证书：

```bash
# 安装 Certbot
apt update && apt install certbot python3-certbot-nginx

# 申请证书
certbot --nginx -d yourdomain.com -d www.yourdomain.com
```

---

## 六、常见问题

### Q1: 部署脚本报错 "Permission denied"

```bash
# 添加执行权限
chmod +x deploy.sh
```

### Q2: Docker 启动失败

```bash
# 查看详细错误
docker logs xiaoli-ms-consult

# 检查端口占用
netstat -tlnp | grep 80
```

### Q3: 前端无法访问后端 API

```bash
# 检查后端是否正常运行
curl http://localhost:8081/api/consult/health

# 检查防火墙
ufw status
```

### Q4: 如何更新代码？

```bash
cd /root/docker

# 拉取最新代码
git pull

# 重新构建
docker-compose -f docker-compose.simple.yml up -d --build
```

### Q5: 如何备份数据？

```bash
# 备份数据库
docker exec xiaoli-postgres pg_dump -U xiaoli xiaoli_legal > backup.sql

# 备份到本地
scp root@你的服务器IP:/root/docker/backup.sql ./
```

### Q6: 学生机配置不够用怎么办？

```
当前配置（2核2G）运行的服务：
✅ PostgreSQL 数据库
✅ ms-consult 后端
✅ ms-case 案例服务
✅ Nginx 前端

如果卡顿，可以：
1. 关闭不需要的服务
2. 限制 Docker 内存使用
3. 升级配置或使用付费服务器
```

---

## 七、日常维护

### 常用命令

```bash
# 查看所有服务状态
docker-compose -f docker-compose.simple.yml ps

# 查看实时日志
docker-compose -f docker-compose.simple.yml logs -f

# 重启所有服务
docker-compose -f docker-compose.simple.yml restart

# 停止所有服务
docker-compose -f docker-compose.simple.yml down

# 重新构建并启动
docker-compose -f docker-compose.simple.yml up -d --build

# 进入容器调试
docker exec -it xiaoli-ms-consult /bin/bash
```

### 定时任务建议

```bash
# 每天凌晨3点自动备份
crontab -e

# 添加以下行
0 3 * * * docker exec xiaoli-postgres pg_dump -U xiaoli xiaoli_legal > /root/backup_$(date +\%Y\%m\%d).sql
```

---

## 八、安全建议

1. **修改默认密码**
   ```bash
   # 修改 .env 中的 DB_PASSWORD
   ```

2. **限制 SSH 访问**
   ```bash
   # 只允许特定 IP SSH
   # 在腾讯云安全组中设置 TCP:22 来源为你的IP
   ```

3. **启用防火墙**
   ```bash
   ufw allow 80
   ufw allow 443
   ufw enable
   ```

4. **定期更新**
   ```bash
   # 定期更新 Docker 镜像
   docker-compose -f docker-compose.simple.yml pull
   docker-compose -f docker-compose.simple.yml up -d
   ```

---

## 九、技术支持

- **项目问题**: https://github.com/hongyue76/xiaoli/issues
- **Docker 文档**: https://docs.docker.com/
- **腾讯云文档**: https://cloud.tencent.com/document/product/

---

**祝部署顺利！🎉**
