@echo off
chcp 65001 >nul
echo ==========================================
echo   律法先锋 - Maven 项目更新脚本
echo ============================================
echo.

cd /d "%~dp0.."

echo [1/3] 清理旧的项目配置文件...
if exist .classpath del /q .classpath
if exist .project del /q .project

echo [2/3] 更新 Maven 项目配置...
call mvnw.cmd eclipse:eclipse -DskipTests

if %ERRORLEVEL% neq 0 (
    echo.
    echo [警告] eclipse:eclipse 失败，尝试使用 mvn 命令...
    call mvn eclipse:eclipse -DskipTests
)

echo [3/3] 重新导入项目到 IDE...
echo.
echo ============================================
echo   更新完成！
echo ============================================
echo.
echo 请在 Eclipse/VSCode 中:
echo   1. 关闭并重新打开项目
echo   2. 或右键项目 -> Maven -> Update Project
echo.
pause
