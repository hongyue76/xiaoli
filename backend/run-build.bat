@echo off
set MAVEN_HOME=d:\maven\apache-maven-3.9.6
set PATH=%MAVEN_HOME%\bin;%PATH%
cd /d D:\me\project\xiaoli\backend
call mvn clean install -DskipTests
pause