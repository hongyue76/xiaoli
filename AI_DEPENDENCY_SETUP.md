# AI 依赖添加总结

## 📋 问题描述

### 原始问题
AI 能力模块（common-ai）已创建，但业务模块无法使用 AI 功能，因为：
1. 业务模块的 pom.xml 中没有添加 common-ai 依赖
2. common-ai 模块还未编译安装到本地 Maven 仓库

---

## ✅ 解决方案

### 1. 已添加依赖的业务模块

以下模块已在 pom.xml 中添加 common-ai 依赖：

| 模块 | 功能 | 优先级 | 状态 |
|------|------|--------|------|
| ms-consult | 法律咨询服务 | 🔴 高 | ✅ 已添加 |
| ms-document | 法律文书生成 | 🔴 高 | ✅ 已添加 |
| ms-contract | 合同审查 | 🟡 中 | ✅ 已添加 |
| analysis | 案件分析 | 🟡 中 | ✅ 已添加 |

### 2. 添加的依赖配置

所有模块都添加了以下依赖：

```xml
<dependencies>
    <!-- 公共模块 -->
    <dependency>
        <groupId>com.xiaoli.ai</groupId>
        <artifactId>common-core</artifactId>
        <version>1.0.0</version>
    </dependency>

    <!-- AI 公共模块 -->
    <dependency>
        <groupId>com.xiaoli.ai</groupId>
        <artifactId>common-ai</artifactId>
        <version>1.0.0</version>
    </dependency>

    <!-- 其他依赖... -->
</dependencies>
```

---

## 🚀 构建和安装步骤

### 步骤 1: 安装公共模块

```bash
# 进入 backend 目录
cd d:/me/project/xiaoli/backend

# 清理并安装公共模块到本地仓库
mvn clean install -pl common/common-core,common/common-ai -am

# 说明：
# -pl: 指定要构建的模块列表
# -am: also-make，同时构建依赖的模块
```

### 步骤 2: 构建业务模块

```bash
# 构建优先级高的业务模块
mvn clean install -pl ms-consult,ms-document,ms-contract,analysis -am

# 或者构建所有模块
mvn clean install
```

### 步骤 3: 验证构建

```bash
# 检查本地仓库中是否有 common-ai jar
# Windows
dir "%USERPROFILE%\.m2\repository\com\xiaoli\ai\common-ai\1.0.0"

# Linux/Mac
ls ~/.m2/repository/com/xiaoli/ai/common-ai/1.0.0/
```

---

## 📊 依赖关系图

```
┌─────────────────────────────────────────────────────────┐
│              xiaoli-legal (父 POM)              │
└─────────────────────────────────────────────────────────┘
                    │
        ┌───────────┼───────────┐
        │           │           │
    ┌───▼───┐  ┌───▼───┐  ┌───▼───┐
    │common-ai│  │common-  │  │ intent-│
    │         │  │core     │  │ core   │
    └────┬────┘  └────┬────┘  └────────┘
         │            │
         └─────┬──────┘
               │
    ┌──────────┼──────────┐
    │          │          │
┌───▼───┐ ┌──▼───┐ ┌──▼───┐
│ms-consult│ │ms-    │ │analysis│
│         │ │document│ │        │
└─────────┘ └────────┘ └────────┘
```

---

## 💡 使用 AI 功能的示例

### 在业务模块中使用 DeepSeek API

```java
import com.xiaoli.ai.common.core.service.DeepSeekService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
public class AIController {

    @Autowired
    private DeepSeekService deepSeekService;

    @PostMapping("/chat")
    public String chat(@RequestBody String question) {
        try {
            return deepSeekService.chat(question, "你是一个法律助手");
        } catch (Exception e) {
            return "AI 调用失败: " + e.getMessage();
        }
    }

    @PostMapping("/analyze")
    public Object analyze(@RequestBody String question) {
        try {
            return deepSeekService.analyzeIntent(question);
        } catch (Exception e) {
            return "意图分析失败: " + e.getMessage();
        }
    }
}
```

### 在业务模块中使用意图路由

```java
import com.xiaoli.ai.common.core.service.IntentRouterService;
import com.xiaoli.ai.common.core.model.Intent;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class ConsultService {

    @Autowired
    private IntentRouterService intentRouterService;

    @Autowired
    private DeepSeekService deepSeekService;

    public String processQuestion(String question) {
        // 1. 分析意图
        Intent intent = intentRouterService.analyzeIntent(question);

        // 2. 根据意图处理
        if (intent.isNeedSearch()) {
            // 专业问题：需要检索
            return processProfessionalQuestion(question);
        } else {
            // 通用问题：直接回答
            return deepSeekService.chat(question, "友好的助手");
        }
    }
}
```

---

## 🔍 Maven 错误处理

### 常见错误：Missing artifact

```
[ERROR] Missing artifact com.xiaoli.ai:common-ai:jar:1.0.0
```

**原因**: common-ai 模块尚未安装到本地 Maven 仓库

**解决方案**:
```bash
# 先安装公共模块
mvn clean install -pl common/common-core,common/common-ai -am

# 然后再构建业务模块
mvn clean install
```

### 常见错误：Bean not found

```
NoSuchBeanDefinitionException: No qualifying bean of type 'DeepSeekService'
```

**原因**: 包扫描范围没有包含 AI 模块

**解决方案**: 确保启动类的包扫描包含 AI 模块：
```java
@SpringBootApplication(
    scanBasePackages = {
        "com.xiaoli.legal.{当前模块}",     // 当前模块
        "com.xiaoli.legal.common.core",    // 公共核心
        "com.xiaoli.ai.common.core"        // AI 公共模块 ✅
    }
)
```

---

## 📝 构建脚本

### Windows 批处理脚本

```batch
@echo off
echo ========================================
echo 构建 AI 公共模块和业务模块
echo ========================================

cd /d %~dp0

echo [1/2] 构建公共模块...
call mvn clean install -pl common/common-core,common/common-ai -am
if %errorlevel% neq 0 (
    echo 公共模块构建失败
    pause
    exit /b 1
)

echo [2/2] 构建业务模块...
call mvn clean install -pl ms-consult,ms-document,ms-contract,analysis -am
if %errorlevel% neq 0 (
    echo 业务模块构建失败
    pause
    exit /b 1
)

echo ========================================
echo 构建成功！
echo ========================================
pause
```

### Linux/Mac Shell 脚本

```bash
#!/bin/bash
echo "========================================"
echo "构建 AI 公共模块和业务模块"
echo "========================================"

cd "$(dirname "$0")/backend"

echo "[1/2] 构建公共模块..."
mvn clean install -pl common/common-core,common/common-ai -am
if [ $? -ne 0 ]; then
    echo "公共模块构建失败"
    exit 1
fi

echo "[2/2] 构建业务模块..."
mvn clean install -pl ms-consult,ms-document,ms-contract,analysis -am
if [ $? -ne 0 ]; then
    echo "业务模块构建失败"
    exit 1
fi

echo "========================================"
echo "构建成功！"
echo "========================================"
```

---

## ✅ 验证清单

构建完成后，验证以下内容：

- [ ] common-ai 模块成功安装到本地仓库
- [ ] ms-consult 可以正常启动
- [ ] ms-document 可以正常启动
- [ ] ms-contract 可以正常启动
- [ ] analysis 可以正常启动
- [ ] AI 功能可以正常调用
- [ ] DeepSeek API 连接正常

---

## 📚 相关文档

- `DEEPSEEK_INTEGRATION_GUIDE.md` - DeepSeek 集成指南
- `PACKAGE_SCAN_OPTIMIZATION.md` - 包扫描优化说明
- `POM_FIX_SUMMARY.md` - POM 配置修复总结
- `MODULE_NAME_FIX.md` - 模块名称修正总结

---

## 🎯 总结

### 已完成
- ✅ 4 个高优先级业务模块添加 common-ai 依赖
- ✅ 创建构建和安装说明文档
- ✅ 提供使用示例代码
- ✅ 说明错误处理方法

### 下一步
1. 执行构建脚本安装公共模块
2. 构建业务模块
3. 测试 AI 功能
4. 验证服务启动

### 注意事项
- ⚠️ common-ai 模块需要先编译安装
- ⚠️ 确保包扫描范围包含 AI 模块
- ⚠️ 配置 DeepSeek API Key 才能使用

---

**更新时间**: 2024-03-31
**状态**: ✅ 已完成
**影响范围**: 4 个业务模块的 pom.xml
