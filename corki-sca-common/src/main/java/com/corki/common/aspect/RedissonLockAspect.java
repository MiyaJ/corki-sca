package com.corki.common.aspect;

import com.corki.common.annotation.RedissonLock;
import com.corki.common.utils.RedissonUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * Redisson分布式锁AOP切面
 *
 * @author Corki
 * @since 2025-12-16
 */
@Slf4j
@Aspect
@Order(1)
@Component
public class RedissonLockAspect {

    @Autowired
    private RedissonUtil redissonUtil;

    private final ExpressionParser parser = new SpelExpressionParser();

    @Around("@annotation(redissonLock)")
    public Object around(ProceedingJoinPoint joinPoint, RedissonLock redissonLock) throws Throwable {
        // 获取锁的key
        String lockKey = getLockKey(joinPoint, redissonLock);

        // 获取锁
        RLock lock = getLock(redissonLock, lockKey);

        // 尝试获取锁
        boolean locked = false;
        try {
            if (redissonLock.leaseTime() == -1) {
                // 看门狗模式
                locked = lock.tryLock(redissonLock.waitTime(), -1, TimeUnit.SECONDS);
            } else {
                locked = lock.tryLock(redissonLock.waitTime(), redissonLock.leaseTime(), TimeUnit.SECONDS);
            }

            if (locked) {
                log.debug("获取分布式锁成功: {}", lockKey);
                // 执行业务方法
                return joinPoint.proceed();
            } else {
                log.warn("获取分布式锁失败: {}", lockKey);
                throw new RuntimeException("系统繁忙，请稍后再试");
            }
        } catch (InterruptedException e) {
            log.error("获取分布式锁被中断: {}", lockKey, e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("获取分布式锁失败", e);
        } finally {
            if (locked) {
                // 释放锁
                try {
                    lock.unlock();
                    log.debug("释放分布式锁成功: {}", lockKey);
                } catch (Exception e) {
                    log.error("释放分布式锁失败: {}", lockKey, e);
                }
            }
        }
    }

    /**
     * 获取锁的key
     */
    private String getLockKey(ProceedingJoinPoint joinPoint, RedissonLock redissonLock) {
        // 如果有自定义key表达式
        if (!redissonLock.keyExpression().isEmpty()) {
            return parseKeyExpression(joinPoint, redissonLock.keyExpression());
        }
        // 使用注解中的value
        return redissonLock.value();
    }

    /**
     * 解析SpEL表达式获取key
     */
    private String parseKeyExpression(ProceedingJoinPoint joinPoint, String keyExpression) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        String[] paramNames = methodSignature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        EvaluationContext context = new StandardEvaluationContext();
        if (paramNames != null) {
            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
        } else {
            // 如果无法获取参数名，使用 p0, p1, p2... 作为变量名
            for (int i = 0; i < args.length; i++) {
                context.setVariable("p" + i, args[i]);
            }
        }
        Expression expression = parser.parseExpression(keyExpression);
        Object value = expression.getValue(context);
        return value != null ? value.toString() : "";
    }

    /**
     * 根据锁类型获取锁对象
     */
    private RLock getLock(RedissonLock redissonLock, String lockKey) {
        switch (redissonLock.lockType()) {
            case FAIR:
                return redissonUtil.getFairLock(lockKey);
            case READ:
                return redissonUtil.getReadLock(lockKey);
            case WRITE:
                return redissonUtil.getWriteLock(lockKey);
            case REENTRANT:
            default:
                if (redissonLock.fair()) {
                    return redissonUtil.getFairLock(lockKey);
                } else {
                    return redissonUtil.getLock(lockKey);
                }
        }
    }
}