@echo off
set LOMBOK=C:\Users\25912\.m2\repository\org\projectlombok\lombok\1.18.30\lombok-1.18.30.jar
set DEPS=d:\me\project\xiaoli\backend\common\common-core\target\deps
set OUTPUT=d:\me\project\xiaoli\backend\common\common-core\target\classes

echo Building classpath...
set CP=%LOMBOK%
for %%f in (%DEPS%\*.jar) do set CP=!CP!;%%f

echo Creating output directory...
if not exist "%OUTPUT%" mkdir "%OUTPUT%"

echo Compiling...
javac -d "%OUTPUT%" -cp "%CP%" -processorpath "%LOMBOK%" -encoding UTF-8 -sourcepath d:\me\project\xiaoli\backend\common\common-core\src\main\java d:\me\project\xiaoli\backend\common\common-core\src\main\java\com\xiaoli\legal\common\core\*.java

echo Done
pause