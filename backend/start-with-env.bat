@echo off
chcp 65001 >nul
title 律法先锋 - 环境变量启动

REM 加载 .env 文件中的环境变量
if exist "%~dp0.env" (
    echo 加载环境变量配置...
    for /f "usebackq tokens=1,* delims==" %%a in ("%~dp0.env") do (
        set "%%a=%%b"
    )
    echo 环境变量加载完成
) else (
    echo [警告] 未找到 .env 文件
    echo 请将 .env.example 复制为 .env 并填入API密钥
)

REM 检查必需的环境变量
if "%DEEPSEEK_API_KEY%"=="" (
    echo [警告] DEEPSEEK_API_KEY 未设置
)

echo.
echo ========================================
echo 启动微服务
echo ========================================
echo.

REM 启动 ms-consult
echo [启动] ms-consult...
start "ms-consult" cmd /k "cd /d %~dp0ms-consult && mvn spring-boot:run"

timeout /t 2 /nobreak >nul
