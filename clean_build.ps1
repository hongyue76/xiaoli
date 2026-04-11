# 删除Lombok缓存并重新编译
if (Test-Path 'C:\Users\25912\.m2\repository\org\projectlombok\lombok\1.18.30') {
    Remove-Item -Path 'C:\Users\25912\.m2\repository\org\projectlombok\lombok\1.18.30' -Recurse -Force
}
Set-Location 'd:/me/project/xiaoli/backend'
mvn clean compile -DskipTests -pl common/common-core 2>&1