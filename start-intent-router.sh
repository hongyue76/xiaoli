#!/bin/bash

# DeepSeek 意图路由系统 - 快速启动脚本

echo "================================================"
echo "     DeepSeek 意图路由系统 - 快速启动"
echo "================================================"
echo ""

# 检查 Java 环境
echo "[1/4] 检查 Java 环境..."
if ! command -v java &> /dev/null; then
    echo "❌ 错误: 未找到 Java，请先安装 JDK 17+"
    exit 1
fi
echo "✅ Java 环境检查通过"

# 检查 Maven 环境
echo "[2/4] 检查 Maven 环境..."
if ! command -v mvn &> /dev/null; then
    echo "❌ 错误: 未找到 Maven，请先安装 Maven"
    exit 1
fi
echo "✅ Maven 环境检查通过"

# 编译后端
echo "[3/4] 编译后端项目..."
cd "$(dirname "$0")/backend"
mvn clean compile -DskipTests
if [ $? -ne 0 ]; then
    echo "❌ 后端编译失败"
    exit 1
fi
echo "✅ 后端编译成功"

# 启动意图路由服务
echo "[4/4] 启动意图路由服务..."
cd "$(dirname "$0")/backend/intent-core"
echo ""
echo "正在启动服务，请稍候..."
echo "服务地址: http://localhost:8087/intent-router"
echo "健康检查: http://localhost:8087/intent-router/health"
echo ""
echo "按 Ctrl+C 停止服务"
echo "================================================"
echo ""

mvn spring-boot:run
