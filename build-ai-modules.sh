#!/bin/bash

echo "========================================"
echo "构建 AI 公共模块和业务模块"
echo "========================================"
echo ""

cd "$(dirname "$0")/backend"

echo "[1/2] 构建公共模块 (common-core + common-ai)..."
echo "----------------------------------------"
mvn clean install -pl common/common-core,common/common-ai -am
if [ $? -ne 0 ]; then
    echo ""
    echo "[ERROR] 公共模块构建失败"
    echo "请检查错误信息并重试"
    exit 1
fi
echo "[SUCCESS] 公共模块构建成功"
echo ""

echo "[2/2] 构建业务模块 (ms-consult + ms-document + ms-contract + analysis)..."
echo "----------------------------------------"
mvn clean install -pl ms-consult,ms-document,ms-contract,analysis -am
if [ $? -ne 0 ]; then
    echo ""
    echo "[ERROR] 业务模块构建失败"
    echo "请检查错误信息并重试"
    exit 1
fi
echo "[SUCCESS] 业务模块构建成功"
echo ""

echo "========================================"
echo "所有模块构建成功！"
echo "========================================"
echo ""
echo "已安装模块:"
echo "  - common-core"
echo "  - common-ai"
echo "  - ms-consult"
echo "  - ms-document"
echo "  - ms-contract"
echo "  - analysis"
echo ""
echo "下一步:"
echo "  1. 配置 DeepSeek API Key"
echo "  2. 启动各个微服务"
echo "  3. 测试 AI 功能"
echo ""
