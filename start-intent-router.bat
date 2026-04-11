@echo off
chcp 65001 >nul
echo ================================================
echo        DeepSeek 意图路由系统 - 快速启动
echo ================================================
echo.

REM 检查 Java 环境
echo [1/4] 检查 Java 环境...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ 错误: 未找到 Java，请先安装 JDK 17+
    pause
    exit /b 1
)
echo ✅ Java 环境检查通过

REM 检查 Maven 环境
echo [2/4] 检查 Maven 环境...
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ 错误: 未找到 Maven，请先安装 Maven
    pause
    exit /b 1
)
echo ✅ Maven 环境检查通过

REM 编译后端
echo [3/4] 编译后端项目...
cd /d "%~dp0backend"
call mvn clean compile -DskipTests
if %errorlevel% neq 0 (
    echo ❌ 后端编译失败
    pause
    exit /b 1
)
echo ✅ 后端编译成功

REM 启动意图路由服务
echo [4/4] 启动意图路由服务...
cd /d "%~dp0backend\intent-core"
echo.
echo 正在启动服务，请稍候...
echo 服务地址: http://localhost:8087/intent-router
echo 健康检查: http://localhost:8087/intent-router/health
echo.
echo 按 Ctrl+C 停止服务
echo ================================================
echo.

call mvn spring-boot:run

pause
