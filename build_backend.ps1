Set-Location "d:/me/project/xiaoli/backend"
mvn -B clean install -DskipTests 2>&1 | Tee-Object -FilePath "build.log"