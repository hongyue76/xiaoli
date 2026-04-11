@echo off
setlocal enabledelayedexpansion

set PROJECT_DIR=d:\me\project\xiaoli\backend
set MODULE=common\common-core
set LOMBOK_VERSION=1.18.30
set LOMBOK_JAR=%USERPROFILE%\.m2\repository\org\projectlombok\lombok\%LOMBOK_VERSION%\lombok-%LOMBOK_VERSION%.jar
set OUTPUT_DIR=%PROJECT_DIR%\%MODULE%\target\classes

echo ========== Manual Build ==========
echo LOMBOK_JAR: %LOMBOK_JAR%

cd /d %PROJECT_DIR%

echo [1/3] Copy dependencies...
call mvn dependency:copy-dependencies -B -q -DoutputDirectory=%PROJECT_DIR%\%MODULE%\target\deps -pl %MODULE%

if not exist "%LOMBOK_JAR%" (
    echo ERROR: Lombok JAR not found
    exit /b 1
)

echo [2/3] Build classpath...
set CLASSPATH=%LOMBOK_JAR%
for %%f in (%PROJECT_DIR%\%MODULE%\target\deps\*.jar) do (
    set CLASSPATH=!CLASSPATH!;%%f
)

echo [3/3] Compile...
if not exist "%OUTPUT_DIR%" mkdir "%OUTPUT_DIR%"

set SRC_DIR=%PROJECT_DIR%\%MODULE%\src\main\java
set JAVA_FILES=%SRC_DIR%\com\xiaoli\legal\common\core\*.java

echo Compiling with Lombok processor...
javac -d "%OUTPUT_DIR%" -cp "%CLASSPATH%" -processorpath "%LOMBOK_JAR%" -encoding UTF-8 %JAVA_FILES%

if errorlevel 1 (
    echo FAILED
    exit /b 1
) else (
    echo SUCCESS
    echo Output: %OUTPUT_DIR%
)

endlocal