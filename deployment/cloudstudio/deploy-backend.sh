#!/bin/bash
# 律法先锋后端部署脚本

echo "========== 律法先锋后端部署 =========="

# 配置
JAR_DIR="/workspace/backend-jars"
LOG_DIR="/workspace/logs"
mkdir -p $JAR_DIR $LOG_DIR

# 数据库配置（使用环境变量或默认值）
export DB_HOST=${DB_HOST:-localhost}
export DB_PORT=${DB_PORT:-5432}
export DB_NAME=${DB_NAME:-xiaoli_legal}
export DB_USERNAME=${DB_USERNAME:-xiaoli}
export DB_PASSWORD=${DB_PASSWORD:-Xiaoli@2024}

export REDIS_HOST=${REDIS_HOST:-localhost}
export REDIS_PORT=${REDIS_PORT:-6379}
export REDIS_PASSWORD=${REDIS_PASSWORD:-}

# AI服务配置
export DEEPSEEK_API_KEY=${DEEPSEEK_API_KEY:-sk-ab6f6d5baa9349c6a735df32f9cc4f16}

# 服务端口映射
# ms-consult: 18081
# ms-case: 18083
# ms-document: 18082
# ms-contract: 18085
# ms-speech: 18086
# ms-decision: 18087
# ms-compliance: 18088
# ms-evidence: 18089
# analysis: 18090

echo "数据库: $DB_HOST:$DB_PORT/$DB_NAME"
echo "Redis: $REDIS_HOST:$REDIS_PORT"
echo ""
echo "服务启动中..."
echo "请手动启动各个微服务或使用 Docker Compose"
