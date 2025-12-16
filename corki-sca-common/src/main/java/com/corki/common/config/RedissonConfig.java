package com.corki.common.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson配置类
 *
 * @author Corki
 * @since 2025-12-16
 */
@Configuration
@RefreshScope
public class RedissonConfig {

    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    @Value("${spring.data.redis.password:}")
    private String redisPassword;

    @Value("${spring.data.redis.database:0}")
    private int redisDatabase;

    @Value("${spring.data.redis.timeout:3000}")
    private int redisTimeout;

    @Value("${redisson.lockWatchdogTimeout:30000}")
    private int lockWatchdogTimeout;

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean(RedissonClient.class)
    public RedissonClient redissonClient() {
        Config config = new Config();

        // 单机模式配置
        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress("redis://" + redisHost + ":" + redisPort)
                .setDatabase(redisDatabase)
                .setTimeout(redisTimeout)
                // 连接池大小
                .setConnectionPoolSize(64)
                // 连接空闲超时时间
                .setConnectionMinimumIdleSize(10)
                // 空闲连接超时时间
                .setIdleConnectionTimeout(10000)
                // 命令等待超时时间
                .setRetryAttempts(3)
                // 命令重试间隔
                .setRetryInterval(1500)
                // 发布和订阅连接池大小
                .setSubscriptionConnectionPoolSize(50)
                // 发布和订阅连接的最小空闲数
                .setSubscriptionConnectionMinimumIdleSize(1)
                // DNS监测间隔
                .setDnsMonitoringInterval(5000);

        // 设置密码
        if (redisPassword != null && !redisPassword.isEmpty()) {
            serverConfig.setPassword(redisPassword);
        }

        // 设置看门狗超时时间（默认30秒）
        config.setLockWatchdogTimeout(lockWatchdogTimeout);

        // 编码配置
        config.setCodec(org.redisson.codec.JsonJacksonCodec.INSTANCE);

        return Redisson.create(config);
    }
}