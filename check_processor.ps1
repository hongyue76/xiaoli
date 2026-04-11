# 编译脚本
Set-Location 'd:/me/project/xiaoli/backend'
mvn clean compile -DskipTests -pl common/common-core -X 2>&1 | Select-String -Pattern 'processor|Processor|processing|Processing|lombok|Lombok' -Context 1,1 | Select-Object -First 60