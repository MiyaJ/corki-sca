package com.corki.sca.mq.config;

import com.corki.sca.mq.producer.RocketMQTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RocketMQ 自动配置类
 * <p>
 * 自动配置 RocketMQ 的生产者，根据配置文件中的参数自动初始化
 * <p>
 * 配置示例：
 * <pre>
 * rocketmq:
 *   name-server: 127.0.0.1:9876
 *   producer:
 *     group: my_producer_group
 * </pre>
 * <p>
 * 消费者使用注解驱动，通过 @RocketMQMessageListener 注解配置，不需要自动配置
 *
 * @author Corki
 * @since 2026-01-04
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(RocketMQProperties.class)
@ConditionalOnProperty(prefix = "rocketmq", name = "name-server")
public class RocketMQAutoConfiguration {

    private final RocketMQProperties properties;

    public RocketMQAutoConfiguration(RocketMQProperties properties) {
        this.properties = properties;
    }

    /**
     * 配置 RocketMQ 生产者
     * <p>
     * 只有当配置了 rocketmq.name-server 且配置了 producer.group 时才会创建
     *
     * @return DefaultMQProducer 实例
     */
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnProperty(prefix = "rocketmq.producer", name = "group")
    public DefaultMQProducer rocketMQProducer() throws Exception {
        RocketMQProperties.Producer producerConfig = properties.getProducer();

        DefaultMQProducer producer = new DefaultMQProducer(producerConfig.getGroup());

        // 设置 NameServer 地址
        producer.setNamesrvAddr(properties.getNameServer());

        // 设置消息最大大小
        if (producerConfig.getMaxMessageSize() != null) {
            producer.setMaxMessageSize(producerConfig.getMaxMessageSize());
        }

        // 设置消息发送超时时间
        if (producerConfig.getSendMsgTimeout() != null) {
            producer.setSendMsgTimeout(producerConfig.getSendMsgTimeout());
        }

        // 设置消息发送失败重试次数
        if (producerConfig.getRetryTimesWhenSendFailed() != null) {
            producer.setRetryTimesWhenSendFailed(producerConfig.getRetryTimesWhenSendFailed());
        }

        // 设置消息压缩阈值
        if (producerConfig.getCompressMsgBodyOverHowmuch() != null) {
            producer.setCompressMsgBodyOverHowmuch(producerConfig.getCompressMsgBodyOverHowmuch());
        }

        // 设置 VIPChannel
        if (producerConfig.getVipChannelEnabled() != null) {
            producer.setVipChannelEnabled(producerConfig.getVipChannelEnabled());
        }

        // 启动生产者
        try {
            producer.start();
            log.info("RocketMQ 生产者启动成功 - Group: {}, NameServer: {}", producerConfig.getGroup(), properties.getNameServer());
        } catch (Exception e) {
            log.error("RocketMQ 生产者启动失败 - Group: {}, Error: {}", producerConfig.getGroup(), e.getMessage(), e);
            throw e;
        }

        return producer;
    }

    /**
     * 配置 RocketMQ 生产者模板工具类
     * <p>
     * 封装了常用的消息发送方法，提供更便捷的操作
     *
     * @return RocketMQTemplate 实例
     */
    @Bean
    @ConditionalOnProperty(prefix = "rocketmq.producer", name = "group")
    public RocketMQTemplate rocketMQTemplate(DefaultMQProducer producer) {
        return new RocketMQTemplate(producer);
    }

    /**
     * JVM 关闭钩子
     * <p>
     * 确保应用关闭时，RocketMQ 生产者能够优雅地关闭
     */
    @jakarta.annotation.PostConstruct
    public void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("RocketMQ 开始关闭...");
            // 生产者和消费者的关闭由 Spring 容器管理
            log.info("RocketMQ 关闭完成");
        }));
    }
}
