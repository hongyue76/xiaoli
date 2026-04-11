@echo off
cd /d d:\me\project\xiaoli\backend
set MAVEN_OPTS=-X
mvn clean compile -DskipTests -pl common/common-core -X > debug.log 2>&1
findstr /C:"lombok" debug.log > lombok.log
type lombok.log