package com.corki.common.service;

import com.corki.common.annotation.RedissonLock;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 分布式锁使用示例
 * 展示如何在注解中使用动态key
 *
 * @author Corki
 * @since 2025-12-16
 */
@Slf4j
@Service
public class LockExampleService {

    /**
     * 方式一：使用实际参数名（需要编译时开启 -parameters）
     *
     * Maven配置示例：
     * <plugin>
     *     <groupId>org.apache.maven.plugins</groupId>
     *     <artifactId>maven-compiler-plugin</artifactId>
     *     <configuration>
     *         <compilerArgs>
     *             <arg>-parameters</arg>
     *         </compilerArgs>
     *     </configuration>
     * </plugin>
     */
    @RedissonLock(value = "lock:user", keyExpression = "#userId", waitTime = 5, leaseTime = 30)
    public void updateUserWithRealParamName(Long userId, String name) {
        log.info("更新用户，userId: {}, name: {}", userId, name);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 方式二：使用参数索引（通用方式，不需要特殊编译配置）
     * p0 表示第一个参数，p1 表示第二个参数，以此类推
     */
    @RedissonLock(value = "lock:order", keyExpression = "'order:' + #p0", waitTime = 5, leaseTime = 30)
    public void processOrder(Long orderId, String status) {
        log.info("处理订单，orderId: {}, status: {}", orderId, status);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 方式三：混合使用实际参数名和索引
     */
    @RedissonLock(value = "lock:product", keyExpression = "'product:' + #p0 + ':user:' + #userId", waitTime = 5, leaseTime = 30)
    public void purchaseProduct(Long productId, Long userId, Integer quantity) {
        log.info("购买商品，productId: {}, userId: {}, quantity: {}", productId, userId, quantity);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 方式四：使用对象参数（需要开启 -parameters）
     */
    @RedissonLock(value = "lock:account", keyExpression = "'account:' + #transfer.fromAccount + ':to:' + #transfer.toAccount",
                    waitTime = 5, leaseTime = 30)
    public void transfer(TransferRequest transfer) {
        log.info("转账，from: {}, to: {}, amount: {}",
                transfer.getFromAccount(), transfer.getToAccount(), transfer.getAmount());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 转账请求对象
     */
    @Data
    public static class TransferRequest {
        private String fromAccount;
        private String toAccount;
        private Double amount;

    }
}