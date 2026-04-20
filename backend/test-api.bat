@echo off
chcp 65001 >nul
echo ==========================================
echo   律法先锋 - API 测试脚本
echo ==========================================
echo.

set API=http://localhost:18081

echo [1] 健康检查
curl -s %API%/actuator/health
echo.
echo.

echo [2] 测试法律咨询接口
echo 请输入问题（或直接回车使用默认问题）...
set /p question=问题: 
if "%question%"=="" set question=你好，请问我可以起诉邻居吗？

curl -s -X POST %API%/api/consult/chat ^
    -H "Content-Type: application/json" ^
    -d "{\"messages\":[{\"role\":\"user\",\"content\":\"%question%\"}]}"

echo.
echo.
echo ==========================================
echo   测试完成！
echo ==========================================
pause
