@echo off
chcp 65001 >nul
echo ========================================
echo 律法先锋后端服务 - 一键启动脚本
echo ========================================
echo.

REM 检查PostgreSQL是否运行
echo [1/5] 检查PostgreSQL服务...
netstat -ano | findstr ":5432" >nul 2>&1
if %errorlevel% equ 0 (
    echo ✓ PostgreSQL服务正在运行
) else (
    echo ✗ PostgreSQL服务未运行，请先启动PostgreSQL
    echo 提示: 可以使用以下命令启动PostgreSQL服务:
    echo   - Windows服务: net start postgresql-x64-15
    echo   - 或手动启动PostgreSQL
    pause
    exit /b 1
)

REM 检查Redis是否运行
echo [2/5] 检查Redis服务...
netstat -ano | findstr ":6379" >nul 2>&1
if %errorlevel% equ 0 (
    echo ✓ Redis服务正在运行
) else (
    echo ✗ Redis服务未运行，请先启动Redis
    echo 提示: 可以使用以下命令启动Redis:
    echo   - redis-server.exe
    pause
    exit /b 1
)

echo.
echo [3/5] 启动ms-consult服务 (法律咨询/AI对话 - 端口8081)...
start "ms-consult" cmd /k "cd /d %~dp0ms-consult && mvn spring-boot:run"

echo.
echo [4/5] 启动ms-caseinfo服务 (案例检索 - 端口8083)...
start "ms-caseinfo" cmd /k "cd /d %~dp0ms-caseinfo && mvn spring-boot:run"

echo.
echo [5/5] 启动ms-document服务 (文书生成 - 端口8082)...
start "ms-document" cmd /k "cd /d %~dp0ms-document && mvn spring-boot:run"

echo.
echo ========================================
echo 所有服务启动命令已发送
echo ========================================
echo.
echo 服务列表:
echo   - ms-consult (法律咨询):    http://localhost:8081
echo   - ms-document (文书生成):   http://localhost:8082
echo   - ms-caseinfo (案例检索):   http://localhost:8083
echo.
echo 请等待约30-60秒让服务完全启动
echo 查看各个窗口的输出确认服务状态
echo.
echo 如需停止服务，请关闭各个命令行窗口
echo.
pause
