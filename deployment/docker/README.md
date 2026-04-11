# 律法先锋 - Docker部署配置

## 环境要求

- Docker 20.10+
- Docker Compose 2.0+

## 快速启动

```bash
# 启动所有服务
docker-compose -f docker-compose.yml up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down
```

## 服务架构

```
┌─────────────────────────────────────────────────────────────┐
│                        Nginx反向代理                         │
│                      (负载均衡 + SSL)                        │
└────────────────────────────┬────────────────────────────────┘
                             │
         ┌───────────────────┼───────────────────┐
         │                   │                   │
         ▼                   ▼                   ▼
   ┌───────────┐       ┌───────────┐       ┌───────────┐
   │  Web-1   │       │  Web-2   │       │  Mobile  │
   └───────────┘       └───────────┘       └───────────┘
         │                   │                   │
         └───────────────────┼───────────────────┘
                             │
         ┌───────────────────┼───────────────────┐
         │                   │                   │
         ▼                   ▼                   ▼
   ┌───────────┐       ┌───────────┐       ┌───────────┐
   │  Gateway  │       │  Gateway  │       │  Gateway  │
   └───────────┘       └───────────┘       └───────────┘
                             │
         ┌───────────────────┼───────────────────┐
         │                   │                   │
         ▼                   ▼                   ▼
   ┌─────────────────────────────────────────────────────┐
   │                   微服务集群                         │
   │  ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐  │
   │  │Auth  │ │User  │ │Consult│ │Doc   │ │Case  │  │
   │  └──────┘ └──────┘ └──────┘ └──────┘ └──────┘  │
   └─────────────────────────────────────────────────────┘
                             │
         ┌───────────────────┼───────────────────┐
         │                   │                   │
         ▼                   ▼                   ▼
   ┌──────────┐      ┌──────────┐      ┌──────────┐
   │PostgreSQL│      │  Redis   │      │  Kafka   │
   └──────────┘      └──────────┘      └──────────┘
         │                                       │
         ▼                                       ▼
   ┌──────────┐                          ┌──────────┐
   │ MongoDB  │                          │ Milvus   │
   └──────────┘                          └──────────┘
```

## 核心服务

### PostgreSQL
- 端口: 5432
- 用途: 业务数据存储
- 数据卷: pgdata

### Redis
- 端口: 6379
- 用途: 缓存、会话存储
- 数据卷: redisdata

### MongoDB
- 端口: 27017
- 用途: 文档、日志存储
- 数据卷: mongodata

### Elasticsearch
- 端口: 9200
- 用途: 全文检索、日志分析
- 数据卷: esdata

### Milvus
- 端口: 19530
- 用途: 向量数据存储
- 数据卷: milvusdata

### Kafka
- 端口: 9092
- 用途: 消息队列
- 数据卷: kafkadata

### Nacos
- 端口: 8848
- 用途: 服务注册、配置中心

### 后端服务
- API网关(Nginx): 80
- ms-consult (法律咨询): 8081
- ms-document (文书生成): 8082
- ms-caseinfo (案例检索): 8083
- ms-contract (合同审查): 8084
- analysis (案件分析): 8085
- ms-decision (司法决策): 8090
- ms-compliance (企业合规): 8091
- ms-evidence (证据分析): 8088
- ms-speech (语音对话): 8089

### 前端服务
- Web管理后台: 3000
- 移动端H5: 3001
- 桌面客户端: 无(本地运行)

## 环境变量

```yaml
environment:
  # 数据库配置
  POSTGRES_HOST: postgres
  POSTGRES_PORT: 5432
  POSTGRES_DB: xiaoli
  POSTGRES_USER: postgres
  POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}

  # Redis配置
  REDIS_HOST: redis
  REDIS_PORT: 6379
  REDIS_PASSWORD: ${REDIS_PASSWORD}

  # 小理AI配置
  XIAOLI_API_KEY: ${XIAOLI_API_KEY}
  XIAOLI_API_URL: https://api.xiaoli.ai

  # 文件存储
  MINIO_ENDPOINT: minio:9000
  MINIO_ACCESS_KEY: ${MINIO_ACCESS_KEY}
  MINIO_SECRET_KEY: ${MINIO_SECRET_KEY}

  # JWT密钥
  JWT_SECRET: ${JWT_SECRET}
```

## 数据卷

```yaml
volumes:
  pgdata:
    driver: local
  redisdata:
    driver: local
  mongodata:
    driver: local
  esdata:
    driver: local
  milvusdata:
    driver: local
  kafkadata:
    driver: local
  miniodata:
    driver: local
  nacosdata:
    driver: local
```

## 健康检查

各服务均配置了健康检查:

```yaml
healthcheck:
  test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
  interval: 30s
  timeout: 10s
  retries: 3
```

## 运维命令

```bash
# 扩容服务
docker-compose up -d --scale ms-consult=3

# 更新服务
docker-compose up -d --build ms-consult

# 查看资源使用
docker stats

# 进入容器
docker exec -it ms-consult bash

# 备份数据库
docker exec postgres pg_dump -U postgres xiaoli > backup.sql

# 查看实时日志
docker-compose logs -f --tail=100 ms-consult
```

## 生产环境建议

1. **高可用**: 使用Kubernetes进行容器编排
2. **SSL/TLS**: 配置HTTPS证书
3. **监控**: 集成Prometheus + Grafana
4. **日志**: 集成ELK日志系统
5. **备份**: 定期备份数据库和文件
6. **安全**: 配置防火墙、限制端口访问
