package com.corki.sca.mq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;

/**
 * RocketMQ 消费者基类（兼容 V4/V5）
 * <p>
 * 基于 rocketmq-spring-boot-starter 提供的注解驱动编程模型
 * <p>
 * 使用方式：
 * 1. 继承此类
 * 2. 实现 handleMessage(MessageExt message) 方法处理单条消息
 * 3. 在子类上添加 @RocketMQMessageListener 注解配置消费参数
 * <p>
 * 示例：
 * <pre>
 * {@code
 * @Component
 * @RocketMQMessageListener(
 *     topic = "order-topic",
 *     consumerGroup = "order-consumer-group",
 *     selectorExpression = "order-created"
 * )
 * public class OrderMessageListener extends BaseMessageListener {
 *     @Override
 *     protected void handleMessage(MessageExt message) throws Exception {
 *         // 处理消息逻辑
 *         String body = new String(message.getBody(), StandardCharsets.UTF_8);
 *         log.info("收到消息: {}", body);
 *     }
 * }
 * }
 * </pre>
 *
 * @author Corki
 * @since 2026-01-04
 */
@Slf4j
public abstract class BaseMessageListener implements RocketMQListener<MessageExt> {

    /**
     * 消息消费方法
     * <p>
     * 使用 MessageExt 作为消息载体（V4 客户端，但 V5 也兼容）
     *
     * @param message 消息对象
     */
    @Override
    public void onMessage(MessageExt message) {
        try {
            log.info("RocketMQ 消费消息 - Topic: {}, Tags: {}, Keys: {}, MsgId: {}, Body: {}",
                    message.getTopic(),
                    message.getTags(),
                    message.getKeys(),
                    message.getMsgId(),
                    new String(message.getBody(), java.nio.charset.StandardCharsets.UTF_8));

            // 调用子类实现的消费逻辑
            handleMessage(message);

        } catch (Exception e) {
            log.error("RocketMQ 消费消息失败 - Topic: {}, MsgId: {}, Error: {}",
                    message.getTopic(),
                    message.getMsgId(),
                    e.getMessage(),
                    e);

            // 抛出异常，触发重试机制
            throw new RuntimeException("消息消费失败", e);
        }
    }

    /**
     * 处理单条消息
     * <p>
     * 子类需要实现此方法，定义具体的业务逻辑
     * <p>
     * 注意事项：
     * 1. 抛出异常会导致消息重试
     * 2. 建议在方法内部捕获可预期的业务异常，避免无限重试
     * 3. 消费幂等性需要业务方自行保证
     *
     * @param message 消息对象
     * @throws Exception 处理失败时抛出异常
     */
    protected abstract void handleMessage(MessageExt message) throws Exception;
}
