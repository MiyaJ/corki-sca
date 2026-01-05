# RocketMQ 测试指南

## 问题说明

错误：`RemotingTooMuchRequestException: sendDefaultImpl call timeout`

原因：Topic 不存在，且 Broker 未开启自动创建功能。

---

## 解决方案

### 方案 1：使用命令行创建 Topic（推荐）

#### 1. 查找 RocketMQ 安装目录

```bash
# 查找 RocketMQ 进程
tasklist | findstr java

# 查找 RocketMQ 目录（常见的安装位置）
D:\rocketmq\rocketmq-all-5.1.4-bin-release
C:\rocketmq\rocketmq-all-5.1.4-bin-release
```

#### 2. 创建 Topic

打开 **CMD**（命令提示符），进入 RocketMQ 安装目录：

```cmd
cd D:\rocketmq\rocketmq-all-5.1.4-bin-release

# 创建 Topic
mqadmin.cmd updateTopic -n 127.0.0.1:9876 -c DefaultCluster -t order_topic_test

# 如果上述命令失败，尝试指定 Broker
mqadmin.cmd updateTopic -n 127.0.0.1:9876 -b 127.0.0.1:10911 -t order_topic_test
```

**参数说明：**
- `-n`: NameServer 地址
- `-c`: 集群名称
- `-b`: Broker 地址
- `-t`: Topic 名称
- `-r`: 读队列数量（默认 8）
- `-w`: 写队列数量（默认 8）

#### 3. 验证 Topic 是否创建成功

```cmd
# 查看所有 Topic
mqadmin.cmd topicList -n 127.0.0.1:9876

# 查看 Topic 详情
mqadmin.cmd topicRoute -n 127.0.0.1:9876 -t order_topic_test
```

#### 4. 重启 admin 服务并测试

```bash
cd D:\workspace\corki-sca\corki-sca-admin
mvn spring-boot:run
```

访问：`http://localhost:12345/test/testMq`

---

### 方案 2：修改 Broker 配置开启自动创建

#### 1. 找到 Broker 配置文件

配置文件通常在：
```
D:\rocketmq\rocketmq-all-5.1.4-bin-release\conf\broker.conf
```

#### 2. 修改配置文件

在 `broker.conf` 中添加或修改以下内容：

```properties
# 允许自动创建 Topic
autoCreateTopicEnable=true

# 允许自动创建订阅组
autoCreateSubscriptionGroup=true

# 关闭 VIPChannel（Windows 环境必须关闭）
brokerVIPChannelEnabled=false
```

#### 3. 重启 Broker

```cmd
# 1. 停止 Broker（查找进程 ID）
netstat -ano | findstr "10911"
taskkill /F /PID <进程ID>

# 2. 重新启动 Broker（使用新配置）
cd D:\rocketmq\rocketmq-all-5.1.4-bin-release
start /b bin\mqbroker -n 127.0.0.1:9876 -c conf\broker.conf
```

#### 4. 重启 admin 服务并测试

```bash
cd D:\workspace\corki-sca\corki-sca-admin
mvn spring-boot:run
```

访问：`http://localhost:12345/test/testMq`

---

### 方案 3：使用 Docker 启动 RocketMQ（最简单）

如果不想手动配置，可以使用 Docker 快速启动 RocketMQ：

```bash
# 1. 启动 NameServer
docker run -d --name rmqnamesrv -p 9876:9876 rocketmq/rocketmq:5.1.4 sh mqnamesrv

# 2. 启动 Broker（开启自动创建 Topic）
docker run -d --name rmqbroker --link rmqnamesrv:namesrv -e "NAMESRV_ADDR=namesrv:9876" -p 10909:10909 -p 10911:10911 -p 10912:10912 rocketmq/rocketmq:5.1.4 sh mqbroker -c conf/broker.conf autoCreateTopicEnable=true

# 3. 创建 Topic
docker exec -it rmqbroker sh mqadmin updateTopic -n 127.0.0.1:9876 -c DefaultCluster -t order_topic_test
```

---

## 测试步骤

### 1. 确认 RocketMQ 服务正常运行

```bash
# 检查 NameServer（端口 9876）
netstat -ano | findstr "9876"

# 检查 Broker（端口 10911）
netstat -ano | findstr "10911"
```

### 2. 创建 Topic

```cmd
cd D:\rocketmq\rocketmq-all-5.1.4-bin-release
mqadmin.cmd updateTopic -n 127.0.0.1:9876 -c DefaultCluster -t order_topic_test
```

### 3. 重启 admin 服务

```bash
cd D:\workspace\corki-sca\corki-sca-admin
mvn clean spring-boot:run
```

### 4. 访问测试接口

浏览器访问：`http://localhost:12345/test/testMq`

或者使用 curl：
```bash
curl http://localhost:12345/test/testMq
```

### 5. 查看日志

```bash
# 查看 admin 服务日志
tail -f logs/corki-sca-admin.log

# 查看 RocketMQ Broker 日志
tail -f D:\rocketmq\rocketmq-all-5.1.4-bin-release\logs\broker.log
```

---

## 常见问题排查

### 问题 1：找不到 mqadmin 命令

**解决方法：**
1. 确认在正确的目录：`rocketmq-all-5.1.4-bin-release\bin`
2. Windows 使用 `mqadmin.cmd`，Linux 使用 `sh mqadmin`

### 问题 2：连接超时

**解决方法：**
1. 检查 NameServer 和 Broker 是否启动
2. 检查防火墙是否开放端口（9876, 10911）
3. 修改配置中的超时时间（已改为 10 秒）

### 问题 3：Topic 已存在但仍然超时

**解决方法：**
1. 检查生产者组名是否唯一（已改为 `my_producer_group_test`）
2. 确保 VIPChannel 关闭（已配置 `vip-channel-enabled: false`）
3. 重启 Broker 和 admin 服务

---

## 验证成功标志

成功发送消息后，admin 服务日志会显示：

```
RocketMQ 同步发送消息 - Topic: order_topic_test, Tags: null, Keys: 123456, Content: {...}
消息发送成功 - MsgId: XXXXXXXXXXXXXXXXXXX
```

浏览器会返回：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": "消息发送成功，MsgId: XXXXXXXXXXXXXXXXXXX"
}
```
