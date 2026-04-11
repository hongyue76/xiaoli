@echo off
cd /d d:\me\project\xiaoli\backend
mvn clean compile -DskipTests > build.log 2>&1
type build.log