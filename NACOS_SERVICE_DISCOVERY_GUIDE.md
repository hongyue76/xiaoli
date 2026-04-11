# Nacos 服务注册中心集成指南

## 概述

本项目已集成 Nacos 作为服务注册与发现中心，结合 OpenFeign 实现微服务间的声明式调用。

## 架构说明

```
┌─────────────────────────────────────────────────────────────┐
│                        Nacos Server                          │
│                    (localhost:8848)                          │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │              服务注册表 (Service Registry)               │ │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐   │ │
│  │  │ms-consult│ │ms-document│ │ms-caseinfo│ │ms-contract│  │ │
│  │  │  :8081   │ │  :8082   │ │  :8083   │ │  :8084   │   │ │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘   │ │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐   │ │
│  │  │ms-analysis│ │ms-decision│ │ms-compliance│ │ms-speech│  │ │
│  │  │  :8085   │ │  :8086   │ │  :8087   │ │  :8089   │   │ │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘   │ │
│  │  ┌──────────┐                                            │ │
│  │  │ms-evidence│                                           │ │
│  │  │  :8088   │                                           │ │
│  │  └──────────┘                                            │ │
│  └─────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
         ▲                    ▲                    ▲
         │ 注册/心跳          │ 服务发现           │ 服务发现
    ┌────┴────┐          ┌────┴────┐          ┌────┴────┐
    │ms-consult│─────────▶│ms-caseinfo│          │ms-document│
    │ (消费者) │ OpenFeign│ (提供者) │          │ (提供者) │
    └─────────┘          └──────────┘          └──────────┘
```

## 已注册的微服务

| 服务名 | 端口 | 描述 | Feign 调用名 |
|--------|------|------|-------------|
| ms-consult | 8081 | 法律咨询服务 | `ms-consult` |
| ms-document | 8082 | 法律文书服务 | `ms-document` |
| ms-caseinfo | 8083 | 案例检索服务 | `ms-caseinfo` |
| ms-contract | 8084 | 合同审查服务 | `ms-contract` |
| ms-analysis | 8085 | 案件分析服务 | `ms-analysis` |
| ms-decision | 8086 | 司法辅助决策 | `ms-decision` |
| ms-compliance | 8087 | 企业合规管理 | `ms-compliance` |
| ms-evidence | 8088 | 证据材料分析 | `ms-evidence` |
| ms-speech | 8089 | 语音对话服务 | `ms-speech` |

## 安装 Nacos

### 方式一：Docker（推荐）

```bash
# 单机模式
docker run -d \
  --name nacos \
  -e MODE=standalone \
  -p 8848:8848 \
  -p 9848:9848 \
  nacos/nacos-server:v2.3.0

# 访问控制台
# http://localhost:8848/nacos
# 默认账号: nacos / nacos
```

### 方式二：下载安装

```bash
# 下载 Nacos
curl -O https://github.com/alibaba/nacos/releases/download/2.3.0/nacos-server-2.3.0.zip

# 解压
unzip nacos-server-2.3.0.zip
cd nacos

# 单机启动
sh bin/startup.sh -m standalone

# Windows
startup.cmd -m standalone
```

## 配置说明

### application.yml 配置

```yaml
spring:
  application:
    name: ms-consult        # 服务名，必须与 Nacos 注册名一致

  cloud:
    nacos:
      discovery:
        server-addr: ${NACOS_HOST:localhost}:${NACOS_PORT:8848}
        service: ${spring.application.name}
        namespace: ${NACOS_NAMESPACE:public}    # 命名空间
        group: ${NACOS_GROUP:xiaoli-legal}      # 服务分组
```

### 环境变量

| 变量名 | 默认值 | 说明 |
|--------|--------|------|
| NACOS_HOST | localhost | Nacos 服务器地址 |
| NACOS_PORT | 8848 | Nacos 端口 |
| NACOS_NAMESPACE | public | 命名空间（用于环境隔离） |
| NACOS_GROUP | xiaoli-legal | 服务分组 |

## OpenFeign 使用

### 1. 定义 Feign Client

```java
@FeignClient(name = "ms-caseinfo")  // 使用 Nacos 注册的服务名
public interface CaseServiceClient {

    @GetMapping("/api/case/search")
    Result<List<Case>> search(@RequestParam("query") String query);
}
```

### 2. 在服务中使用

```java
@Service
@RequiredArgsConstructor
public class ConsultService {

    private final CaseServiceClient caseServiceClient;

    public ConsultResult consult(String question) {
        // 通过 Feign 调用案例服务
        Result<List<Case>> result = caseServiceClient.search(question);
        // 处理结果...
    }
}
```

### 3. Feign 全局配置（已内置）

```java
// FeignConfig.java
- 日志级别: BASIC（仅记录请求方法和URL、响应状态码）
- 重试策略: 间隔100ms，最大间隔1s，最多3次
- 消息转换: Spring MVC 消息转换器
```

### 4. 异常处理（已内置）

```java
// FeignExceptionHandler.java
- 404: 服务接口不存在
- 401: 未授权访问
- 500: 服务内部错误
- 503: 服务暂时不可用
- 超时: 服务调用超时
```

## 服务间调用关系

```
ms-consult ──Feign──▶ ms-caseinfo   (查询相关案例)
ms-consult ──Feign──▶ ms-document   (引用文书模板)
ms-document ──Feign──▶ ms-contract  (合同关联文书)
ms-analysis ──Feign──▶ ms-caseinfo  (案例分析引用)
ms-document ──Feign──▶ ms-caseinfo  (文书引用案例)
```

## 启动顺序

1. **启动基础设施**: PostgreSQL, Redis, Nacos
2. **启动业务服务**: 所有服务可并行启动（通过 Nacos 自动发现）
3. **验证注册**: 访问 http://localhost:8848/nacos 查看服务列表

```bash
# 1. 启动 Nacos
docker run -d --name nacos -e MODE=standalone -p 8848:8848 nacos/nacos-server:v2.3.0

# 2. 启动微服务（可并行）
cd backend
mvn spring-boot:run -pl ms-consult &
mvn spring-boot:run -pl ms-document &
mvn spring-boot:run -pl ms-case &
# ...
```

## 负载均衡

OpenFeign 默认集成 Spring Cloud LoadBalancer，支持：
- **轮询策略**（默认）
- **随机策略**

配置示例：
```yaml
spring:
  cloud:
    loadbalancer:
      cache:
        enabled: true
        ttl: 35s
```

## 多实例部署

同一服务启动多个实例时，Nacos 自动注册，LoadBalancer 自动负载均衡：

```bash
# 启动 ms-consult 的两个实例
java -jar ms-consult.jar --server.port=8081
java -jar ms-consult.jar --server.port=8091
```

## 常见问题

### Q: 服务未注册到 Nacos？
- 检查 `@EnableDiscoveryClient` 注解是否添加
- 检查 Nacos 地址是否正确
- 查看 Nacos 控制台服务列表

### Q: Feign 调用 404？
- 确认目标服务已注册到 Nacos
- 检查 `@FeignClient(name)` 与服务名一致
- 检查请求路径是否正确

### Q: Nacos 连接超时？
- 检查 Nacos 是否启动
- 检查防火墙是否开放 8848 和 9848 端口
