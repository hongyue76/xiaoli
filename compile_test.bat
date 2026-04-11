@echo off
set LOMBOK=C:\Users\25912\.m2\repository\org\projectlombok\lombok\1.18.30\lombok-1.18.30.jar
set DEPS=d:\me\project\xiaoli\backend\common\common-core\target\deps
set OUTPUT=d:\me\project\xiaoli\backend\common\common-core\target\classes
set SRC=d:\me\project\xiaoli\backend\common\common-core\src\main\java

echo === Manual Compile with Lombok ===
echo LOMBOK: %LOMBOK%
echo OUTPUT: %OUTPUT%

if not exist "%OUTPUT%" (
    mkdir "%OUTPUT%"
)

echo Compiling...
javac -d "%OUTPUT%" -cp "%LOMBOK%;%DEPS%\*" -processorpath "%LOMBOK%" -encoding UTF-8 "%SRC%\com\xiaoli\legal\common\core\exception\*.java" "%SRC%\com\xiaoli\legal\common\core\security\AuthController.java" 2>&1

echo.
if errorlevel 1 (
    echo FAILED
) else (
    echo SUCCESS
)

pause