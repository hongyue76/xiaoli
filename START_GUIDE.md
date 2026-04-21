# 律法先锋 - 一键启动指南

## 快速开始

### Windows 系统

1. 双击运行 `quick-start.bat`
2. 等待所有服务启动完成
3. 浏览器自动打开 http://localhost:3000

### Linux / macOS 系统

```bash
chmod +x quick-start.sh
./quick-start.sh
```

## 停止服务

- Windows: 运行 `stop.bat`
- Linux/macOS: 按 `Ctrl+C`

## 前提条件

| 软件 | 版本 | 下载地址 |
|------|------|----------|
| JDK | 17+ | https://adoptium.net/ |
| Maven | 3.6+ | https://maven.apache.org/download.cgi |
| Node.js | 18+ | https://nodejs.org/ |
| Docker | 最新版 | https://www.docker.com/ |

## 配置 API 密钥

```bash
cp backend/.env.example backend/.env
# 编辑 backend/.env，填入您的 API 密钥
```

## 服务端口

| 服务 | 端口 |
|------|------|
| 前端 | 3000 |
| ms-consult | 18081 |
| ms-document | 18082 |
| ms-case | 18083 |
| PostgreSQL | 5432 |
| Redis | 6379 |
