# Corki-SCA Common MQ - RocketMQ V5 组件

基于 RocketMQ V5 客户端（rocketmq-v5-client-spring-boot 2.3.5）的消息队列组件，提供生产者和消费者的封装，支持同步、异步消息发送，以及注解驱动的消费模式。

## 功能特性

- ✅ **生产者封装**：提供同步、异步两种消息发送方式
- ✅ **延时消息**：支持任意时长的延时消息（毫秒级）
- ✅ **消费者基类**：提供统一的消费者基类，简化消息处理
- ✅ **自动配置**：基于 Spring Boot 自动配置机制，开箱即用
- ✅ **优雅关闭**：支持 JVM 钩子，确保资源释放
- ✅ **ACL 权限控制**：支持阿里云 RocketMQ 的 ACL 认证

## V5 客户端主要变化

### 与 V4 客户端的主要区别

| 特性 | V4 客户端 | V5 客户端 |
|------|----------|----------|
| 接入点配置 | `nameServer: 127.0.0.1:9876` | `endpoints: 127.0.0.1:8080` |
| 生产者 API | `DefaultMQProducer` | `Producer` (Builder 模式) |
| 消息对象 | `Message` | `Message` (Builder 模式) |
| 消费者 API | `MessageListenerConcurrently` | `RocketMQListener<MessageView>` |
| 延时消息 | 固定等级（1-18） | 任意时长（毫秒） |
| 单向发送 | 支持 `sendOneway()` | 不支持，建议用异步替代 |
| 消费结果 | `ConsumeConcurrentlyStatus` | 异常抛出（自动重试） |

## 快速开始

### 1. 添加依赖

在需要使用的微服务模块的 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>com.corki</groupId>
    <artifactId>corki-sca-common-mq</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### 2. 配置 Nacos 或 application.yml

在 Nacos 配置中心或本地 `application.yml` 中添加配置：

```yaml
rocketmq:
  # 接入点地址（V5 推荐）
  endpoints: 127.0.0.1:8080

  # 兼容 V4 的 nameServer 配置（会自动转换为 endpoints）
  # name-server: 127.0.0.1:9876

  # 生产者配置
  producer:
    group: my_producer_group          # 生产者组名（必填）
    request-timeout: 3000             # 请求超时时间（毫秒）
    max-body-bytes: 4194304           # 最大消息体大小（默认 4MB）

  # ACL 权限控制（可选，阿里云 RocketMQ 需要）
  # access-key: your-access-key
  # secret-key: your-secret-key
```

### 3. 使用生产者发送消息

在需要发送消息的 Service 中注入 `RocketMQTemplate`：

```java
@Service
public class OrderService {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    public void createOrder(OrderDTO orderDTO) throws ClientException {
        // 1. 同步发送消息（可靠性高，速度慢）
        SendReceipt receipt = rocketMQTemplate.syncSend(
            "order-topic",                // Topic
            "order-created",              // Tags
            orderDTO.getOrderNo(),        // Keys（用于索引和查询）
            JSON.toJSONString(orderDTO)  // 消息内容
        );

        log.info("消息发送成功 - MsgId: {}", receipt.getMessageId());

        // 2. 异步发送消息（速度快，不阻塞）
        CompletableFuture<SendReceipt> future = rocketMQTemplate.asyncSend(
            "order-topic",
            "order-created",
            orderDTO.getOrderNo(),
            JSON.toJSONString(orderDTO)
        );

        // 可以使用 CompletableFuture 的回调处理结果
        future.thenAccept(receipt -> {
            log.info("异步消息发送成功 - MsgId: {}", receipt.getMessageId());
        }).exceptionally(throwable -> {
            log.error("异步消息发送失败", throwable);
            return null;
        });

        // 3. 异步发送消息并指定回调
        rocketMQTemplate.asyncSend(
            "order-topic",
            "order-created",
            orderDTO.getOrderNo(),
            JSON.toJSONString(orderDTO),
            receipt -> log.info("成功: {}", receipt.getMessageId()),
            throwable -> log.error("失败", throwable)
        );

        // 4. 发送延时消息（30分钟后执行）
        // V5 支持任意毫秒级的延时时间
        rocketMQTemplate.sendDelayMessage(
            "order-topic",
            "order-timeout",
            orderDTO.getOrderNo(),
            JSON.toJSONString(orderDTO),
            30 * 60 * 1000L  // 30 分钟（毫秒）
        );
    }
}
```

### 4. 使用消费者消费消息

V5 客户端使用注解驱动的编程模型，非常简洁：

```java
@Component
@RocketMQMessageListener(
    topic = "order-topic",                    // 订阅的主题
    consumerGroup = "order-consumer-group",   // 消费者组
    tag = "order-created"                     // 订阅的标签（可选，默认订阅所有）
)
public class OrderMessageListener extends BaseMessageListener {

    @Autowired
    private OrderService orderService;

    @Override
    protected void handleMessage(MessageView message) throws Exception {
        // 解析消息
        String body = new String(message.getBody(), StandardCharsets.UTF_8);
        OrderDTO orderDTO = JSON.parseObject(body, OrderDTO.class);

        // 处理业务逻辑
        orderService.processOrder(orderDTO);

        log.info("订单处理完成: {}", orderDTO.getOrderNo());
    }
}
```

**订阅多个标签：**

```java
@Component
@RocketMQMessageListener(
    topic = "order-topic",
    consumerGroup = "order-consumer-group",
    tag = "order-created || order-paid || order-shipped"  // 使用 || 分隔
)
public class OrderMessageListener extends BaseMessageListener {
    // ...
}
```

**订阅所有标签：**

```java
@Component
@RocketMQMessageListener(
    topic = "order-topic",
    consumerGroup = "order-consumer-group",
    tag = "*"  // 订阅所有标签
)
public class OrderMessageListener extends BaseMessageListener {
    // ...
}
```

## 架构设计

### 包结构

```
com.corki.sca.mq
├── config
│   ├── RocketMQProperties.java          # 配置属性类
│   └── RocketMQAutoConfiguration.java   # 自动配置类
├── producer
│   └── RocketMQTemplate.java             # 生产者模板工具类
└── consumer
    └── BaseMessageListener.java          # 消费者基类
```

### 设计原则

1. **KISS（简单至上）**：提供简洁的 API，隐藏 RocketMQ 复杂性
2. **YAGNI（精益求精）**：只实现核心功能，不过度设计
3. **DRY（杜绝重复）**：通过基类封装通用逻辑，减少重复代码
4. **单一职责**：每个类职责明确，配置、生产、消费分离
5. **开闭原则**：通过继承基类扩展功能，无需修改框架代码

## 最佳实践

### 1. Topic 命名规范

- 使用业务模块作为 Topic 名称
- 例如：`order-topic`、`product-topic`、`user-topic`

### 2. Tag 命名规范

- 使用业务操作作为 Tag 名称
- 例如：`order-created`、`order-paid`、`order-shipped`

### 3. Keys 使用规范

- 使用业务主键作为 Keys
- 用于消息查询和轨迹追踪
- 例如：订单号、用户ID

### 4. 消息大小控制

- 消息体建议不超过 4KB
- 大消息建议使用文件存储，消息中只传递文件路径

### 5. 异常处理

```java
@Override
protected void handleMessage(MessageView message) throws Exception {
    try {
        // 业务逻辑
        doSomething(message);
    } catch (BusinessException e) {
        // 业务异常，记录日志但不抛出异常（不重试）
        log.error("业务处理失败，不重试: {}", e.getMessage());
    } catch (Exception e) {
        // 系统异常，记录日志并抛出异常（触发重试）
        log.error("系统异常，稍后重试: {}", e.getMessage(), e);
        throw e;
    }
}
```

### 6. 消费幂等性

```java
@Override
protected void handleMessage(MessageView message) throws Exception {
    // V5 客户端使用 Optional 获取 Keys
    String keys = message.getKeys().orElse("");

    // 检查是否已处理
    if (redisService.hasProcessed(keys)) {
        log.info("消息已处理，跳过: {}", keys);
        return;
    }

    // 标记为处理中
    redisService.markAsProcessing(keys);

    try {
        // 处理业务逻辑
        doSomething(message);

        // 标记为已处理
        redisService.markAsProcessed(keys);
    } catch (Exception e) {
        // 清除处理中标记
        redisService.removeProcessingMark(keys);
        throw e;
    }
}
```

### 7. 异步发送最佳实践

```java
// 推荐：使用 CompletableFuture 的链式调用
rocketMQTemplate.asyncSend(topic, keys, content)
    .thenAccept(receipt -> {
        // 处理成功情况
        metricsService.incrementSuccess();
    })
    .exceptionally(throwable -> {
        // 处理失败情况
        metricsService.incrementFailure();
        return null;
    });

// 推荐：使用 asyncSend 的回调版本
rocketMQTemplate.asyncSend(topic, keys, content,
    receipt -> {
        // 成功回调
        log.info("发送成功: {}", receipt.getMessageId());
    },
    throwable -> {
        // 失败回调
        log.error("发送失败", throwable);
    }
);
```

## 高级特性

### 1. 顺序消息

V5 客户端通过相同的 `MessageGroup` 来保证消息顺序：

```java
// 发送顺序消息
Message message = Message.newBuilder()
    .setTopic("order-topic")
    .setKeys(orderId)
    .setBody(JSON.toJSONString(order).getBytes(StandardCharsets.UTF_8))
    .setGroup(orderId)  // 使用 orderId 作为 MessageGroup，保证同一订单的消息顺序
    .build();

producer.send(message);
```

### 2. 定时消息

V5 客户端支持精确到毫秒的定时消息：

```java
// 发送定时消息（指定具体的投递时间）
long deliveryTimestamp = LocalDateTime.now()
    .plusHours(2)
    .atZone(ZoneId.systemDefault())
    .toInstant()
    .toEpochMilli();

Message message = Message.newBuilder()
    .setTopic("reminder-topic")
    .setKeys(userId)
    .setBody(JSON.toJSONString(reminder).getBytes(StandardCharsets.UTF_8))
    .setDeliveryTimestamp(deliveryTimestamp)
    .build();

producer.send(message);
```

### 3. 消息过滤

V5 客户端支持 Tag 过滤和 SQL92 过滤：

**Tag 过滤（推荐）：**

```java
@RocketMQMessageListener(
    topic = "order-topic",
    consumerGroup = "order-consumer-group",
    tag = "order-paid || order-shipped"  // 只订阅特定标签
)
```

**SQL92 过滤：**

```java
@RocketMQMessageListener(
    topic = "order-topic",
    consumerGroup = "order-consumer-group",
    selectorType = SelectorType.SQL92,
    selectorExpression = "amount > 100 AND status = 'PAID'"
)
```

发送时需要设置消息属性：

```java
Message message = Message.newBuilder()
    .setTopic("order-topic")
    .setBody(content.getBytes(StandardCharsets.UTF_8))
    .addProperty("amount", "200")
    .addProperty("status", "PAID")
    .build();
```

## 常见问题

### 1. 生产者启动失败

**问题**：`RocketMQ V5 生产者启动失败`

**解决方案**：
- 检查 `endpoints` 或 `name-server` 地址是否正确
- 检查网络连接是否正常
- 检查防火墙是否开放端口（默认 8080）
- 阿里云 RocketMQ 需要配置 `access-key` 和 `secret-key`

### 2. 消息发送超时

**问题**：消息发送超时

**解决方案**：
- 增加 `request-timeout` 配置值
- 检查网络带宽
- 检查消息大小是否超过限制
- 考虑使用异步发送提高性能

### 3. 消费者重复消费

**问题**：同一条消息被消费多次

**解决方案**：
- 实现消费幂等性（参考上文第 6 点）
- 使用 Redis 或数据库记录已处理的消息
- 使用消息 Keys 作为唯一标识

### 4. 消息消费失败重试

**问题**：消息消费失败后的重试机制

**说明**：
- V5 客户端中，抛出异常会自动触发重试
- 默认最大重试次数为 16 次
- 超过最大重试次数后，消息会被发送到死信队列（DLQ）
- 业务方需要监控死信队列并处理异常消息

### 5. 从 V4 迁移到 V5

**主要步骤**：

1. **更新依赖**：将 `rocketmq-client` 替换为 `rocketmq-v5-client-spring-boot`
2. **更新配置**：将 `name-server` 改为 `endpoints`（或保持兼容）
3. **更新生产者代码**：使用新的 `Producer` API
4. **更新消费者代码**：继承 `BaseMessageListener`，处理 `MessageView`
5. **更新延时消息**：将延时等级改为毫秒数

## 版本信息

- **RocketMQ V5 Client**: 2.3.5
- **JDK**: 21+
- **Spring Boot**: 3.2.9

## 参考文档

- [RocketMQ 官方文档](https://rocketmq.apache.org/zh/)
- [RocketMQ V5 客户端文档](https://rocketmq.apache.org/zh/docs/v5/)
- [rocketmq-v5-client-spring-boot GitHub](https://github.com/apache/rocketmq-spring/tree/main/rocketmq-v5-client-spring-boot)
