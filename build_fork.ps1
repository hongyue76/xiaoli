# 编译脚本
Set-Location 'd:/me/project/xiaoli/backend'
mvn clean compile -DskipTests -pl common/common-core -Dmaven.compiler.fork=true -Dmaven.compiler.source=17 -Dmaven.compiler.target=17 "-Dmaven.compiler.compilerArgs=-J-Dfile.encoding=UTF-8" 2>&1