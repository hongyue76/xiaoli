# 律法先锋 - 后端服务

## 目录结构

```
backend/
├── common/                    # 公共模块
│   ├── common-core/          # 核心工具类
│   ├── common-security/      # 安全模块
│   ├── common-redis/         # Redis缓存
│   └── common-swagger/       # Swagger文档
├── ms-auth/                   # 认证服务
├── ms-user/                   # 用户服务
├── ms-consult/                # 法律咨询服务
├── ms-document/               # 法律文书服务
├── ms-caseinfo/              # 案件管理服务
├── ms-contract/               # 合同审查服务
├── ms-knowledge/              # 知识库服务
├── ms-speech/                 # 语音处理服务
└── ms-admin/                  # 管理后台服务
```

## 技术选型

- 框架：Spring Boot 3.x
- 服务注册：Nacos
- 配置中心：Nacos
- API网关：Spring Cloud Gateway
- 数据库：PostgreSQL + MongoDB
- 缓存：Redis
- 向量数据库：Milvus
- 消息队列：Kafka
- 搜索引擎：Elasticsearch

## 快速开始

### 环境要求
- JDK 17+
- Maven 3.8+
- PostgreSQL 15+
- Redis 7+
- Nacos
- Milvus

### 配置

1. 创建配置文件 `ms-auth/src/main/resources/application.yml`

```yaml
server:
  port: 8081

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/xiaoli_auth
    username: postgres
    password: your-password
  data:
    redis:
      host: localhost
      port: 6379

tencent:
  yuanqi:
    base-url: ${TENCENT_YUANQI_BASE_URL}
    assistant-id: ${TENCENT_YUANQI_ASSISTANT_ID}
    token: ${TENCENT_YUANQI_TOKEN}

nacos:
  server-addr: localhost:8848
```

### 启动服务

```bash
cd ms-auth
mvn spring-boot:run
```

## 微服务列表

| 服务名 | 端口 | 说明 |
|--------|------|------|
| api-gateway | 8080 | API网关 |
| ms-auth | 8081 | 认证服务 |
| ms-user | 8082 | 用户服务 |
| ms-consult | 8083 | 法律咨询 |
| ms-document | 8084 | 法律文书 |
| ms-caseinfo | 8083 | 案例检索 |
| ms-contract | 8086 | 合同审查 |
| ms-knowledge | 8087 | 知识库 |
| ms-speech | 8088 | 语音服务 |
| ms-admin | 8089 | 管理后台 |
