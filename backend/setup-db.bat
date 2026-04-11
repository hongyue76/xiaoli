@echo off
echo 创建数据库和用户...
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -c "CREATE DATABASE xiaoli_legal;" 2>NUL
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -c "CREATE USER xiaoli WITH PASSWORD 'Xiaoli@2024';" 2>NUL
"C:\Program Files\PostgreSQL\15\bin\psql.exe" -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE xiaoli_legal TO xiaoli;" 2>NUL
echo 完成！
pause