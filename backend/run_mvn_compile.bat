@echo off
cd /d %~dp0
echo === Maven Compile === 
call mvn compile -DskipTests 2>&1
echo.
echo === DONE ===
pause
