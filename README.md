# Corki-SCA 微服务架构项目

## 项目简介

Corki-SCA 是一个基于 Spring Cloud Alibaba 的微服务架构项目，采用现代化的微服务设计理念，整合了分布式系统中常用的中间件和工具，为构建企业级应用提供了一套完整的技术解决方案。

## 🏗️ 整体架构

### 技术栈

- **Java 版本**: JDK 21
- **框架版本**: Spring Boot 3.2.9, Spring Cloud 2023.0.3, Spring Cloud Alibaba 2023.0.3.4
- **服务治理**: Nacos (服务注册发现、配置中心)
- **API 网关**: Spring Cloud Gateway
- **负载均衡**: Spring Cloud LoadBalancer
- **服务调用**: OpenFeign
- **分布式事务**: Seata AT 模式
- **消息队列**: RocketMQ 5.1.4
- **数据库**: MySQL 8.0
- **缓存**: Redis
- **数据访问**: MyBatis-Plus 3.5.14
- **认证授权**: Sa-Token 1.44.0
- **API 文档**: Knife4j 4.3.0 (聚合网关)
- **日志监控**: Logback + P6Spy (SQL 性能分析)
- **工具库**: Hutool 5.8.40, Lombok 1.18.42

### 核心特性

- ✅ **微服务架构**: 采用 Spring Cloud Alibaba 全家桶
- ✅ **服务治理**: Nacos 作为注册中心和配置中心
- ✅ **API 网关**: 统一入口、路由、认证和文档聚合
- ✅ **分布式事务**: Seata AT 模式确保数据一致性
- ✅ **消息驱动**: RocketMQ 支持异步通信
- ✅ **安全认证**: Sa-Token 提供权限管理
- ✅ **监控日志**: P6Spy 分析 SQL 性能，Logback 结构化日志
- ✅ **容器化部署**: Docker Compose 一键启动中间件

## 📦 模块架构

```
corki-sca/
├── corki-sca-gateway/     # 网关服务 (10006)
├── corki-sca-admin/       # 管理端服务 (10001)
├── corki-sca-member/      # 会员服务
├── corki-sca-product/     # 产品服务
├── corki-sca-order/       # 订单服务
├── corki-sca-pay/         # 支付服务
├── corki-sca-common/      # 公共模块
└── demo/                  # 示例模块
```

### 模块说明

| 模块 | 端口 | 功能描述 |
|------|------|----------|
| **corki-sca-gateway** | 10006 | API 网关，统一入口，路由转发，认证鉴权，文档聚合 |
| **corki-sca-admin** | 10001 | 管理后台服务，用户管理，权限配置 |
| **corki-sca-member** | - | 会员服务，用户信息，积分管理 |
| **corki-sca-product** | - | 产品服务，商品管理，库存控制 |
| **corki-sca-order** | - | 订单服务，订单处理，业务流程编排 |
| **corki-sca-pay** | - | 支付服务，支付处理，交易管理 |
| **corki-sca-common** | - | 公共组件，工具类，通用配置 |

## 🗄️ 数据库设计

### 业务数据库

1. **integrated_storage** - 库存服务数据库
   ```sql
   - storage: 商品库存表
   ```

2. **integrated_account** - 账户服务数据库
   ```sql
   - account: 用户账户表
   ```

3. **integrated_order** - 订单服务数据库
   ```sql
   - order: 订单信息表
   ```

4. **integrated_praise** - 点赞服务数据库
   ```sql
   - item: 点赞项目表
   ```

### Seata 分布式事务支持

每个业务数据库都包含 `undo_log` 表，支持 Seata AT 模式的分布式事务回滚。

## 🚀 快速开始

### 环境要求

- JDK 21+
- Maven 3.6+
- Docker & Docker Compose
- MySQL 8.0+
- Redis 6.0+

### 启动步骤

#### 1. 启动基础中间件

```bash
# 进入 docker-compose 目录
cd docker-compose/env

# 设置环境变量
export NACOS_VERSION=v2.3.2

# 启动所有中间件
docker-compose -f docker-compose-env.yml up -d
```

#### 2. 初始化数据库

数据库初始化脚本位于 `docker-compose/config/sql/init.sql`，包含：
- 业务数据库创建
- 基础数据初始化
- Seata undo_log 表创建

#### 3. 启动应用服务

```bash
# 根目录下执行
mvn clean install

# 按顺序启动服务
# 1. 启动公共模块
mvn spring-boot:run -pl corki-sca-common

# 2. 启动业务服务
mvn spring-boot:run -pl corki-sca-admin
mvn spring-boot:run -pl corki-sca-member
mvn spring-boot:run -pl corki-sca-product
mvn spring-boot:run -pl corki-sca-order
mvn spring-boot:run -pl corki-sca-pay

# 3. 启动网关服务
mvn spring-boot:run -pl corki-sca-gateway
```

### 访问地址

| 服务 | 地址 | 描述 |
|------|------|------|
| **Nacos 控制台** | http://localhost:8848/nacos | 服务治理中心 |
| **API 网关** | http://localhost:10006 | 统一入口 |
| **API 文档** | http://localhost:10006/doc.html | Knife4j 聚合文档 |
| **管理服务** | http://localhost:10001 | 管理后台 |

## 🔧 配置说明

### Nacos 配置中心

项目使用 Nacos 作为配置中心，支持以下配置：

- **web-common**: Web 通用配置
- **mysql**: 数据库连接配置
- **redis**: Redis 连接配置
- **{服务名}**: 各服务独立配置

### 网关路由配置

```yaml
spring.cloud.gateway.routes:
  - id: corki-sca-admin
    uri: lb://corki-sca-admin
    predicates:
      - Path=/admin/**
    filters:
      - StripPrefix=1
```

### 安全配置

网关层面配置了路径白名单，无需认证即可访问：

```yaml
app:
  excludePaths:
    - /favicon.ico
    - /admin/login/**
    - /member/login/**
```

## 🔍 监控与日志

### 日志配置

- **日志框架**: Logback
- **日志级别**: 可通过环境配置调整
- **SQL 监控**: P6Spy 插件监控 SQL 执行性能
- **日志文件**: 结构化输出，便于 ELK 收集

### 性能监控

- **Nacos 监控**: 服务健康状态、配置变更
- **P6Spy**: SQL 性能分析，慢查询检测
- **Spring Boot Actuator**: 应用指标监控

## 🛡️ 安全特性

### 认证授权

- **Sa-Token**: 轻量级权限认证框架
- **Redis 存储**: Token 状态存储
- **网关鉴权**: 统一认证拦截

### 安全更新

项目已修复以下安全漏洞：

- **CVE-2022-1471**: snakeyaml 版本升级至 2.0
- **WS-2022-0468**: jackson-core 版本升级至 2.15.2
- **CVE-2025-22228 / CVE-2024-38827**: spring-security-crypto 版本升级至 6.2.0

## 🔄 分布式事务

### Seata AT 模式

- **协调者**: Seata Server (TC)
- **参与者**: 各业务服务 (RM)
- **事务模式**: AT 模式，自动补偿
- **配置**: file 存储模式

### 事务流程

1. **TM 开启全局事务**
2. **各分支事务注册到 TC**
3. **执行业务逻辑**
4. **TM 提交或回滚全局事务**
5. **TC 协调所有分支事务**

## 📝 开发规范

### 代码规范

- **编码规范**: 遵循阿里巴巴 Java 开发手册
- **注释规范**: 中文注释，业务逻辑清晰
- **命名规范**: 统一命名约定
- **包结构**: 按功能模块划分

### Git 规范

- **分支策略**: Git Flow 工作流
- **提交信息**: Conventional Commits 规范
- **代码审查**: 必须通过 Code Review

## 🔮 后续规划

### 短期计划

- [ ] 完善单元测试和集成测试
- [ ] 接入 Prometheus + Grafana 监控
- [ ] 实现 ELK 日志收集分析
- [ ] 添加 Redis 分布式锁

### 长期规划

- [ ] Service Mesh 服务网格
- [ ] Serverless 函数服务
- [ ] 云原生 K8s 部署
- [ ] DevOps 流水线集成

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request，请确保：

1. 代码风格与项目保持一致
2. 添加必要的测试用例
3. 更新相关文档
4. 通过所有 CI 检查

## 📄 许可证

本项目基于 [Apache License 2.0](LICENSE) 许可证开源。

## 📞 联系方式

- **项目维护者**: Corki Team
- **邮箱**: [项目邮箱]
- **文档**: [项目文档地址]

---

**注意**: 本项目仅用于学习和研究目的，生产环境使用请进行充分测试。