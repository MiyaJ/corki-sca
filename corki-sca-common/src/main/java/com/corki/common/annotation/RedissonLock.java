package com.corki.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Redisson分布式锁注解
 *
 * @author Corki
 * @since 2025-12-16
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedissonLock {

    /**
     * 锁的key
     */
    String value();

    /**
     * 等待锁的时间（秒），默认10秒
     */
    long waitTime() default 10;

    /**
     * 锁持有时间（秒），默认30秒，-1表示看门狗模式
     */
    long leaseTime() default 30;

    /**
     * 锁类型
     */
    LockType lockType() default LockType.REENTRANT;

    /**
     * 是否为公平锁
     */
    boolean fair() default false;

    /**
     * 自定义SpEL表达式作为锁的key，可以拼接方法参数
     * 例如：'#userId' 或 '#order.id' 或 'order:' + #orderId
     * 注意：如果编译时保留了参数信息（-parameters），可以使用实际的参数名
     * 否则需要使用 p0, p1, p2... 等索引方式访问参数
     */
    String keyExpression() default "";

    /**
     * 锁的类型枚举
     */
    enum LockType {
        /**
         * 可重入锁
         */
        REENTRANT,
        /**
         * 公平锁
         */
        FAIR,
        /**
         * 读锁
         */
        READ,
        /**
         * 写锁
         */
        WRITE
    }
}