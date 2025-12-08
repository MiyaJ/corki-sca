# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

**Corki-SCA** 是一个基于 Spring Cloud Alibaba 的现代化微服务架构项目，采用领域驱动设计（DDD）理念构建的企业级系统。项目整合了完整的微服务生态系统，包括服务治理、分布式事务、消息队列、认证授权等核心能力。

### 技术栈概览

- **运行环境**: JDK 21, Maven 3.6+
- **核心框架**: Spring Boot 3.2.9, Spring Cloud 2023.0.3, Spring Cloud Alibaba 2023.0.3.4
- **服务治理**: Nacos 2.3.2 (注册发现 + 配置中心)
- **微服务间通信**: OpenFeign + Spring Cloud LoadBalancer
- **API网关**: Spring Cloud Gateway (端口: 10006)
- **认证授权**: Sa-Token 1.44.0 + Redis
- **分布式事务**: Seata 2.0.0 (AT模式)
- **消息队列**: RocketMQ 5.1.4
- **数据库**: MySQL 8.0 + MyBatis-Plus 3.5.14
- **缓存**: Redis 6.0+
- **监控日志**: Logback + P6Spy
- **API文档**: Knife4j 4.3.0 (网关聚合)

## 微服务架构设计

### 服务边界与职责划分

| 服务名称 | 端口 | 核心职责 | 数据库 | 技术特点 |
|---------|------|----------|---------|----------|
| **corki-sca-gateway** | 10006 | 统一入口、路由转发、认证鉴权、文档聚合 | 无 | 响应式编程、StripPrefix、黑白名单 |
| **corki-sca-admin** | 10001 | 管理后台、权限配置、用户管理 | admin | 管理界面、RBAC权限模型 |
| **corki-sca-member** | 10002 | 会员管理、用户信息、积分系统 | integrated_account | 用户画像、会员等级 |
| **corki-sca-product** | 10003 | 商品管理、库存控制、分类管理 | integrated_storage | 商品生命周期、库存锁 |
| **corki-sca-order** | 10004 | 订单处理、状态流转、业务编排 | integrated_order | 订单状态机、流程引擎 |
| **corki-sca-pay** | 10005 | 支付处理、交易管理、对账系统 | integrated_praise | 支付渠道、交易流水 |

### 分层架构模式 (DDD)

项目采用标准的领域驱动设计分层架构：
```
├── controller (接口层) - REST API定义，参数验证
├── service (应用层) - 业务流程编排，事务管理
│   ├── impl (业务逻辑实现)
│   └── manager (领域服务)
├── dao (持久层) - 数据访问，MyBatis-Plus操作
├── model (领域模型)
│   ├── entity (数据库实体)
│   ├── dto (数据传输对象)
│   └── vo (视图对象)
└── common (公共层) - 工具类，常量，通用组件
```

## 核心架构组件

### 1. 配置管理策略

**Nacos配置中心架构**：
```
配置层次结构：
├── web-common (公共配置) - 数据库、Redis、通用业务配置
├── mysql (数据库配置) - 数据源连接配置
├── redis (缓存配置) - Redis连接和缓存策略
└── {service-name} (服务独立配置) - 服务特性、环境、业务参数
```

**配置加载优先级**：
1. 本地 application.yml
2. Nacos 动态配置 (按优先级)
3. 环境变量覆盖

### 2. 数据架构设计

**数据库分离策略** - 每个微服务拥有独立数据库：
- `integrated_storage` → 库存服务
- `integrated_account` → 账户服务
- `integrated_order` → 订单服务
- `integrated_praise` → 点赞服务

**分布式事务** - 使用Seata AT模式：
- 每个业务库必须包含 `undo_log` 表
- 全局事务通过 `@GlobalTransactional` 注解管理
- XID 贯穿整个调用链

### 3. 认证授权设计

**Sa-Token架构**：
1. 网关统一认证 → 检查白名单路径
2. Sa-Token 解析 JWT Token
3. Redis 存储 Token 状态
4. `@SaCheckPermission` 注解权限拦截

**网关白名单路径**：
```yaml
app:
  excludePaths:
    - /favicon.ico
    - /admin/login/**
    - /member/login/**
    - /product/**
    - /order/**
    - /pay/**
```

### 4. 网关路由模式

使用前缀路由 + 负载均衡：
```yaml
spring.cloud.gateway.routes:
  - id: corki-sca-member
    uri: lb://corki-sca-member
    predicates:
      - Path=/member/**
    filters:
      - StripPrefix=1  # 移除/member前缀
```

## 开发工作流

### 1. 环境启动命令

```bash
# 1. 启动中间件服务 (必须第一步)
cd docker-compose/env
export NACOS_VERSION=v2.3.2
docker-compose -f docker-compose-env.yml up -d

# 2. 初始化数据库
# 执行: docker-compose/config/sql/init.sql

# 3. 构建项目
mvn clean install -DskipTests

# 4. 启动顺序（重要！）
# 4.1 先启动业务服务
mvn spring-boot:run -pl corki-sca-admin
mvn spring-boot:run -pl corki-sca-member
mvn spring-boot:run -pl corki-sca-product
mvn spring-boot:run -pl corki-sca-order
mvn spring-boot:run -pl corki-sca-pay

# 4.2 最后启动网关服务
mvn spring-boot:run -pl corki-sca-gateway
```

### 2. 开发调试

**关键访问地址**：
- **Nacos控制台**: http://localhost:8848/nacos (nacos/nacos)
- **API文档聚合**: http://localhost:10006/doc.html
- **网关健康检查**: http://localhost:10006/actuator/health

**日志查看**：
```bash
# SQL性能监控 (P6Spy)
tail -f logs/p6spy.log

# 应用日志
tail -f logs/corki-sca-*.log

# 日志级别可通过Nacos动态调整
```

### 3. 代码规范

**Controller层模式**：
```java
@RestController
@RequestMapping("/admin/user")
public class UserController {
    @Autowired
    private UserService userService;

    @SaCheckPermission("admin:user:list")
    @GetMapping("/list")
    public R<List<UserDTO>> list() {
        return R.ok(userService.listUsers());
    }
}
```

**统一响应格式**：
```java
// 成功响应
{
    "code": 200,
    "message": "操作成功",
    "data": {...}
}

// 错误响应
{
    "code": 500,
    "message": "服务器内部错误",
    "data": null
}
```

### 4. 服务间通信

**OpenFeign声明式调用**：
```java
@FeignClient(name = "corki-sca-member")
public interface MemberFeignApi {
    @GetMapping("/member/info/{id}")
    R<MemberDTO> getMemberInfo(@PathVariable("id") Long id);
}
```

**分布式事务使用**：
```java
@Service
@GlobalTransactional(rollbackFor = Exception.class)
public class OrderServiceImpl implements OrderService {

    public void createOrder(OrderDTO orderDTO) {
        // 1. 创建订单
        orderService.save(orderDTO);

        // 2. 扣减库存
        productFeignApi.deductStock(orderDTO.getProductId(), orderDTO.getCount());

        // 3. 扣减账户余额
        memberFeignApi.deductBalance(orderDTO.getUserId(), orderDTO.getAmount());
    }
}
```

## 常见问题解决

### 1. 服务注册失败
```bash
# 检查Nacos连接
telnet localhost 8848

# 检查服务注册
curl http://localhost:8848/nacos/v1/ns/instance/list?serviceName=corki-sca-admin
```

### 2. 分布式事务异常
```bash
# 检查Seata状态
curl http://localhost:8091/health

# 查看事务日志
tail -f logs/seata.log
```

### 3. 数据库连接问题
```bash
# 检查数据库连接
mysql -hlocalhost -P3306 -uroot -proot

# 验证数据库和表
show databases;
use integrated_storage;
show tables;
```

## 扩展指南

### 1. 添加新微服务

1. 在根 `pom.xml` 添加新模块
2. 创建标准微服务项目结构
3. 配置 Nacos 服务注册和配置发现
4. 实现 Feign 客户端接口
5. 在网关添加路由规则
6. 按标准启动顺序部署

### 2. 集成新中间件

1. 在 `corki-sca-common` 添加相关依赖
2. 在 Nacos 配置中心添加连接配置
3. 实现连接池和客户端封装
4. 添加健康检查和监控指标
5. 更新 Docker Compose 中间件配置

## 安全注意事项

项目已修复以下安全漏洞：
- **CVE-2022-1471**: snakeyaml 版本升级至 2.0
- **WS-2022-0468**: jackson-core 版本升级至 2.15.2
- **CVE-2025-22228 / CVE-2024-38827**: spring-security-crypto 版本升级至 6.2.0

开发时请确保：
- 敏感信息使用 Nacos 配置加密存储
- API 接口进行权限验证
- SQL 注入防护 (使用 MyBatis-Plus 参数绑定)
- 跨站脚本攻击防护 (输入验证和输出转义)