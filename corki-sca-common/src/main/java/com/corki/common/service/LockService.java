package com.corki.common.service;

import com.corki.common.annotation.RedissonLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁示例服务
 *
 * @author Corki
 * @since 2025-12-16
 */
@Slf4j
@Service
public class LockService {

    /**
     * 使用注解方式加锁
     */
    @RedissonLock(value = "lock:order", waitTime = 10, leaseTime = 60)
    public void createOrderWithAnnotation(String orderId) {
        log.info("创建订单，订单ID: {}", orderId);
        try {
            // 模拟业务处理
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.info("订单创建完成，订单ID: {}", orderId);
    }

    /**
     * 使用注解方式加锁，动态key
     */
    @RedissonLock(value = "lock:user", keyExpression = "#userId", waitTime = 5, leaseTime = 30)
    public void updateUserWithAnnotation(Long userId, String name) {
        log.info("更新用户信息，用户ID: {}, 姓名: {}", userId, name);
        try {
            // 模拟业务处理
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.info("用户信息更新完成，用户ID: {}", userId);
    }

    /**
     * 使用读锁
     */
    @RedissonLock(value = "lock:config", lockType = RedissonLock.LockType.READ, waitTime = 5, leaseTime = 10)
    public String readConfig() {
        log.info("读取配置信息");
        // 模拟读取配置
        return "config_value";
    }

    /**
     * 使用写锁
     */
    @RedissonLock(value = "lock:config", lockType = RedissonLock.LockType.WRITE, waitTime = 5, leaseTime = 10)
    public void writeConfig(String config) {
        log.info("写入配置信息: {}", config);
        try {
            // 模拟写入配置
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}