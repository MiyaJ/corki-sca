package com.corki.sca.mq.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * RocketMQ 配置属性类
 * 用于从配置文件中读取 RocketMQ 相关配置
 *
 * @author Corki
 * @since 2026-01-04
 */
@Data
@ConfigurationProperties(prefix = "rocketmq")
public class RocketMQProperties {

    /**
     * NameServer 地址，多个地址用分号分隔
     * 例如: 127.0.0.1:9876;127.0.0.1:9877
     * V5 客户端也支持此配置，会自动转换为 endpoints
     */
    private String nameServer;

    /**
     * V5 客户端接入点地址（推荐使用）
     * 例如: 127.0.0.1:8080;127.0.0.1:8081
     * 如果配置了 endpoints，将优先使用此配置
     */
    private String endpoints;

    /**
     * 生产者配置
     */
    private Producer producer = new Producer();

    /**
     * 消费者配置
     */
    private Consumer consumer = new Consumer();

    /**
     * 访问密钥 (ACL 权限控制)
     */
    private String accessKey;

    /**
     * 密钥 (ACL 权限控制)
     */
    private String secretKey;

    /**
     * 消息轨迹开关
     */
    private Boolean traceEnabled = false;

    /**
     * 生产者配置
     */
    @Data
    public static class Producer {
        /**
         * 生产者组名
         */
        private String group = "default_producer_group";

        /**
         * 消息最大大小，默认 4MB
         */
        private Integer maxMessageSize = 4 * 1024 * 1024;

        /**
         * 消息发送超时时间，默认 3000ms
         */
        private Integer sendMsgTimeout = 3000;

        /**
         * 消息发送失败重试次数，默认 2 次
         */
        private Integer retryTimesWhenSendFailed = 2;

        /**
         * 消息压缩阈值，默认 4KB
         */
        private Integer compressMsgBodyOverHowmuch = 4096;

        /**
         * 是否开启 VIPChannel (端口为 10909)
         * V5 客户端已废弃此配置
         */
        @Deprecated
        private Boolean vipChannelEnabled = false;

        /**
         * 发送超时时间（毫秒）
         * V5 客户端使用此配置
         */
        private Integer requestTimeout = 3000;

        /**
         * 最大消息体大小（字节）
         * V5 客户端默认 4MB
         */
        private Integer maxBodyBytes = 4 * 1024 * 1024;
    }

    /**
     * 消费者配置
     */
    @Data
    public static class Consumer {
        /**
         * 消费者组名
         */
        private String group = "default_consumer_group";

        /**
         * 消息消费模式: CONSUME_CLUSTERING(集群模式) 或 CONSUME_ORDERLY(顺序消费)
         * 默认为集群模式
         */
        private String messageModel = "CLUSTERING";

        /**
         * 消费位点策略: CONSUME_FROM_LAST_OFFSET(最新) 或 CONSUME_FROM_FIRST_OFFSET(最早)
         * 默认从最新位置消费
         * V5 客户端支持以下策略:
         * - LATEST: 从最新位置开始
         * - EARLIEST: 从最早位置开始
         * - TIMESTAMP: 从指定时间戳开始
         */
        private String consumeFromWhere = "LATEST";

        /**
         * 消费线程最小数量
         */
        private Integer consumeThreadMin = 20;

        /**
         * 消费线程最大数量
         */
        private Integer consumeThreadMax = 64;

        /**
         * 消息消费超时时间（分钟）
         */
        private Integer consumeTimeout = 15;

        /**
         * 单个队列最大消息数
         */
        private Integer pullThresholdForQueue = 1000;

        /**
         * 消息拉批大小
         */
        private Integer pullBatchSize = 32;
    }
}
