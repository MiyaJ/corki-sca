package com.corki.common.utils;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.redisson.api.listener.MessageListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Redisson分布式锁工具类
 *
 * @author Corki
 * @since 2025-12-16
 */
@Slf4j
@Component
public class RedissonUtil {

    /**
     * Redisson客户端
     */
    @Resource
    private RedissonClient redissonClient;

    // =============================Lock============================

    /**
     * 获取可重入锁
     *
     * @param lockKey 锁的key
     * @return RLock
     */
    public RLock getLock(String lockKey) {
        return redissonClient.getLock(lockKey);
    }

    /**
     * 获取公平锁
     *
     * @param lockKey 锁的key
     * @return RLock
     */
    public RLock getFairLock(String lockKey) {
        return redissonClient.getFairLock(lockKey);
    }

    /**
     * 获取读写锁的读锁
     *
     * @param lockKey 锁的key
     * @return RLock
     */
    public RLock getReadLock(String lockKey) {
        return redissonClient.getReadWriteLock(lockKey).readLock();
    }

    /**
     * 获取读写锁的写锁
     *
     * @param lockKey 锁的key
     * @return RLock
     */
    public RLock getWriteLock(String lockKey) {
        return redissonClient.getReadWriteLock(lockKey).writeLock();
    }

    /**
     * 尝试获取锁
     *
     * @param lockKey      锁的key
     * @param waitTime     等待时间
     * @param leaseTime    锁持有时间
     * @param timeUnit     时间单位
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit timeUnit) {
        RLock lock = getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, timeUnit);
        } catch (InterruptedException e) {
            log.error("获取锁失败: {}", lockKey, e);
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 尝试获取可重入锁
     *
     * @param lockKey      锁的key
     * @param waitTime     等待时间(秒)
     * @param leaseTime    锁持有时间(秒)
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, long waitTime, long leaseTime) {
        return tryLock(lockKey, waitTime, leaseTime, TimeUnit.SECONDS);
    }

    /**
     * 尝试获取锁（无限等待）
     *
     * @param lockKey      锁的key
     * @param leaseTime    锁持有时间(秒)
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, long leaseTime) {
        return tryLock(lockKey, -1, leaseTime, TimeUnit.SECONDS);
    }

    /**
     * 加锁（不设置过期时间）
     *
     * @param lockKey 锁的key
     */
    public void lock(String lockKey) {
        RLock lock = getLock(lockKey);
        lock.lock();
    }

    /**
     * 加锁并设置过期时间
     *
     * @param lockKey   锁的key
     * @param leaseTime 锁持有时间
     * @param timeUnit  时间单位
     */
    public void lock(String lockKey, long leaseTime, TimeUnit timeUnit) {
        RLock lock = getLock(lockKey);
        lock.lock(leaseTime, timeUnit);
    }

    /**
     * 加锁并设置过期时间（单位：秒）
     *
     * @param lockKey   锁的key
     * @param leaseTime 锁持有时间(秒)
     */
    public void lock(String lockKey, long leaseTime) {
        lock(lockKey, leaseTime, TimeUnit.SECONDS);
    }

    /**
     * 解锁
     *
     * @param lockKey 锁的key
     */
    public void unlock(String lockKey) {
        RLock lock = getLock(lockKey);
        // 只有持有锁的线程才能解锁
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    /**
     * 强制解锁（不管是否是当前线程持有）
     *
     * @param lockKey 锁的key
     */
    public void forceUnlock(String lockKey) {
        RLock lock = getLock(lockKey);
        lock.forceUnlock();
    }

    /**
     * 判断锁是否被持有
     *
     * @param lockKey 锁的key
     * @return 是否被持有
     */
    public boolean isLocked(String lockKey) {
        RLock lock = getLock(lockKey);
        return lock.isLocked();
    }

    /**
     * 判断锁是否被当前线程持有
     *
     * @param lockKey 锁的key
     * @return 是否被当前线程持有
     */
    public boolean isHeldByCurrentThread(String lockKey) {
        RLock lock = getLock(lockKey);
        return lock.isHeldByCurrentThread();
    }

    /**
     * 获取锁的等待线程数
     *
     * @param lockKey 锁的key
     * @return 等待线程数
     */
    public int getHoldCount(String lockKey) {
        RLock lock = getLock(lockKey);
        return lock.getHoldCount();
    }

    /**
     * 获取锁的剩余持有时间
     *
     * @param lockKey 锁的key
     * @return 剩余时间（毫秒）
     */
    public long getRemainingTime(String lockKey) {
        RLock lock = getLock(lockKey);
        return lock.remainTimeToLive();
    }

    // =============================MultiLock============================

    /**
     * 获取联锁
     *
     * @param lockKeys 多个锁的key
     * @return RLock
     */
    public RLock getMultiLock(String... lockKeys) {
        RLock[] locks = new RLock[lockKeys.length];
        for (int i = 0; i < lockKeys.length; i++) {
            locks[i] = getLock(lockKeys[i]);
        }
        return redissonClient.getMultiLock(locks);
    }

    /**
     * 尝试获取联锁
     *
     * @param waitTime  等待时间
     * @param leaseTime 锁持有时间
     * @param timeUnit  时间单位
     * @param lockKeys  多个锁的key
     * @return 是否获取成功
     */
    public boolean tryMultiLock(long waitTime, long leaseTime, TimeUnit timeUnit, String... lockKeys) {
        RLock multiLock = getMultiLock(lockKeys);
        try {
            return multiLock.tryLock(waitTime, leaseTime, timeUnit);
        } catch (InterruptedException e) {
            log.error("获取联锁失败", e);
            Thread.currentThread().interrupt();
            return false;
        }
    }

    // =============================RedLock============================

    /**
     * 获取红锁（用于多个Redis实例的分布式锁）
     *
     * @param lockKeys 多个锁的key
     * @return RLock
     */
    @Deprecated
    public RLock getRedLock(String... lockKeys) {
        RLock[] locks = new RLock[lockKeys.length];
        for (int i = 0; i < lockKeys.length; i++) {
            locks[i] = getLock(lockKeys[i]);
        }
        return redissonClient.getRedLock(locks);
    }

    /**
     * 尝试获取红锁
     *
     * @param waitTime  等待时间
     * @param leaseTime 锁持有时间
     * @param timeUnit  时间单位
     * @param lockKeys  多个锁的key
     * @return 是否获取成功
     */
    @Deprecated
    public boolean tryRedLock(long waitTime, long leaseTime, TimeUnit timeUnit, String... lockKeys) {
        RLock redLock = getRedLock(lockKeys);
        try {
            return redLock.tryLock(waitTime, leaseTime, timeUnit);
        } catch (InterruptedException e) {
            log.error("获取红锁失败", e);
            Thread.currentThread().interrupt();
            return false;
        }
    }

    // =============================Lock Executor============================

    /**
     * 执行带锁的任务
     *
     * @param lockKey   锁的key
     * @param waitTime  等待时间
     * @param leaseTime 锁持有时间
     * @param timeUnit  时间单位
     * @param task      要执行的任务
     * @return 是否执行成功
     */
    public boolean executeWithLock(String lockKey, long waitTime, long leaseTime, TimeUnit timeUnit, Runnable task) {
        if (tryLock(lockKey, waitTime, leaseTime, timeUnit)) {
            try {
                task.run();
                return true;
            } finally {
                unlock(lockKey);
            }
        }
        return false;
    }

    /**
     * 执行带锁的任务（返回结果）
     *
     * @param lockKey   锁的key
     * @param waitTime  等待时间
     * @param leaseTime 锁持有时间
     * @param timeUnit  时间单位
     * @param task      要执行的任务
     * @param <T>       返回值类型
     * @return 执行结果（如果获取锁失败返回null）
     */
    public <T> T executeWithLock(String lockKey, long waitTime, long leaseTime, TimeUnit timeUnit, java.util.concurrent.Callable<T> task) {
        if (tryLock(lockKey, waitTime, leaseTime, timeUnit)) {
            try {
                return task.call();
            } catch (Exception e) {
                log.error("执行锁任务失败: {}", lockKey, e);
                throw new RuntimeException(e);
            } finally {
                unlock(lockKey);
            }
        }
        return null;
    }

    /**
     * 执行带锁的任务（默认等待时间10秒，持有时间30秒）
     *
     * @param lockKey 锁的key
     * @param task    要执行的任务
     * @return 是否执行成功
     */
    public boolean executeWithLock(String lockKey, Runnable task) {
        return executeWithLock(lockKey, 10, 30, TimeUnit.SECONDS, task);
    }

    /**
     * 执行带锁的任务（返回结果，默认等待时间10秒，持有时间30秒）
     *
     * @param lockKey 锁的key
     * @param task    要执行的任务
     * @param <T>     返回值类型
     * @return 执行结果（如果获取锁失败返回null）
     */
    public <T> T executeWithLock(String lockKey, java.util.concurrent.Callable<T> task) {
        return executeWithLock(lockKey, 10, 30, TimeUnit.SECONDS, task);
    }

    // =============================Map============================

    /**
     * 获取Map对象
     *
     * @param <K> Key类型
     * @param <V> Value类型
     * @param name Map名称
     * @return RMap
     */
    public <K, V> RMap<K, V> getMap(String name) {
        return redissonClient.getMap(name);
    }

    /**
     * 获取Map缓存
     *
     * @param <K>  Key类型
     * @param <V>  Value类型
     * @param name Map名称
     * @return Map
     */
    public <K, V> Map<K, V> getMapCache(String name) {
        return redissonClient.getMapCache(name);
    }

    // =============================Set============================

    /**
     * 获取Set对象
     *
     * @param <V>  Value类型
     * @param name Set名称
     * @return RSet
     */
    public <V> RSet<V> getSet(String name) {
        return redissonClient.getSet(name);
    }

    /**
     * 获取Set缓存
     *
     * @param <V>  Value类型
     * @param name Set名称
     * @return RSetCache
     */
    public <V> RSetCache<V> getSetCache(String name) {
        return redissonClient.getSetCache(name);
    }

    /**
     * 获取SortedSet对象
     *
     * @param <V>  Value类型
     * @param name SortedSet名称
     * @return RScoredSortedSet
     */
    public <V> RScoredSortedSet<V> getScoredSortedSet(String name) {
        return redissonClient.getScoredSortedSet(name);
    }

    // =============================List============================

    /**
     * 获取List对象
     *
     * @param <V>  Value类型
     * @param name List名称
     * @return RList
     */
    public <V> RList<V> getList(String name) {
        return redissonClient.getList(name);
    }

    // =============================Queue============================

    /**
     * 获取Queue对象
     *
     * @param <V>  Value类型
     * @param name Queue名称
     * @return RQueue
     */
    public <V> RQueue<V> getQueue(String name) {
        return redissonClient.getQueue(name);
    }

    /**
     * 获取BlockingQueue对象
     *
     * @param <V>  Value类型
     * @param name BlockingQueue名称
     * @return RBlockingQueue
     */
    public <V> RBlockingQueue<V> getBlockingQueue(String name) {
        return redissonClient.getBlockingQueue(name);
    }

    /**
     * 获取BoundedBlockingQueue对象
     *
     * @param <V>    Value类型
     * @param name   Queue名称
     * @param capacity 容量
     * @return RBoundedBlockingQueue
     */
    public <V> RBoundedBlockingQueue<V> getBoundedBlockingQueue(String name, int capacity) {
        RBoundedBlockingQueue<V> queue = redissonClient.getBoundedBlockingQueue(name);
        if (queue.trySetCapacity(capacity)) {
            return queue;
        }
        return queue;
    }

    /**
     * 获取PriorityQueue对象
     *
     * @param <V>  Value类型
     * @param name PriorityQueue名称
     * @return RPriorityQueue
     */
    public <V> RPriorityQueue<V> getPriorityQueue(String name) {
        return redissonClient.getPriorityQueue(name);
    }

    /**
     * 获取DelayedQueue对象
     *
     * @param <V>  Value类型
     * @param name DelayedQueue名称
     * @return RDelayedQueue
     */
    public <V> RDelayedQueue<V> getDelayedQueue(String name) {
        return redissonClient.getDelayedQueue(getQueue(name));
    }

    // =============================Deque============================

    /**
     * 获取Deque对象
     *
     * @param <V>  Value类型
     * @param name Deque名称
     * @return RDeque
     */
    public <V> RDeque<V> getDeque(String name) {
        return redissonClient.getDeque(name);
    }

    // =============================Topic============================

    /**
     * 获取Topic对象
     *
     * @param name Topic名称
     * @return RTopic
     */
    public RTopic getTopic(String name) {
        return redissonClient.getTopic(name);
    }

    /**
     * 发布消息
     *
     * @param name    Topic名称
     * @param message 消息
     * @return 监听器数量
     */
    public long publish(String name, Object message) {
        RTopic topic = getTopic(name);
        return topic.publish(message);
    }

    /**
     * 订阅消息
     *
     * @param <T>          消息类型
     * @param name         Topic名称
     * @param listener     监听器
     * @param messageClass 消息类型
     */
    public <T> void subscribe(String name, MessageListener<T> listener, Class<T> messageClass) {
        RTopic topic = getTopic(name);
        topic.addListener(messageClass, listener);
    }

    /**
     * 订阅消息
     *
     * @param name     Topic名称
     * @param listener 监听器
     */
    public void subscribe(String name, MessageListener<Object> listener) {
        subscribe(name, listener, Object.class);
    }

    // =============================BitSet============================

    /**
     * 获取BitSet对象
     *
     * @param name BitSet名称
     * @return RBitSet
     */
    public RBitSet getBitSet(String name) {
        return redissonClient.getBitSet(name);
    }

    // =============================AtomicLong============================

    /**
     * 获取AtomicLong对象
     *
     * @param name AtomicLong名称
     * @return RAtomicLong
     */
    public RAtomicLong getAtomicLong(String name) {
        return redissonClient.getAtomicLong(name);
    }

    /**
     * 获取AtomicDouble对象
     *
     * @param name AtomicDouble名称
     * @return RAtomicDouble
     */
    public RAtomicDouble getAtomicDouble(String name) {
        return redissonClient.getAtomicDouble(name);
    }

    // =============================BloomFilter============================

    /**
     * 获取布隆过滤器
     *
     * @param <T>  元素类型
     * @param name 名称
     * @return RBloomFilter
     */
    public <T> RBloomFilter<T> getBloomFilter(String name) {
        return redissonClient.getBloomFilter(name);
    }

    // =============================HyperLogLog============================

    /**
     * 获取HyperLogLog对象
     *
     * @param name HyperLogLog名称
     * @return RHyperLogLog
     */
    public RHyperLogLog<Object> getHyperLogLog(String name) {
        return redissonClient.getHyperLogLog(name);
    }

    // =============================Script============================

    /**
     * 执行Lua脚本
     *
     * @param script Lua脚本
     * @param keys   键列表
     * @param values 值列表
     * @return 执行结果
     */
//    public Object eval(String script, List<Object> keys, Object... values) {
//        RScript rScript = redissonClient.getScript();
//        return rScript.eval(RScript.Mode.READ_WRITE, script, keys, values);
//    }

    /**
     * 执行Lua脚本（返回指定类型）
     *
     * @param <T>        返回值类型
     * @param script     Lua脚本
     * @param resultType 返回值类型
     * @param keys       键列表
     * @param values     值列表
     * @return 执行结果
     */
//    public <T> T eval(String script, Class<T> resultType, List<Object> keys, Object... values) {
//        Object result = eval(script, keys, values);
//        if (result != null) {
//            if (resultType.isInstance(result)) {
//                return resultType.cast(result);
//            }
//            // 尝试类型转换
//            if (resultType == String.class) {
//                return resultType.cast(result.toString());
//            } else if (resultType == Long.class && result instanceof Number) {
//                return resultType.cast(((Number) result).longValue());
//            } else if (resultType == Integer.class && result instanceof Number) {
//                return resultType.cast(((Number) result).intValue());
//            } else if (resultType == Double.class && result instanceof Number) {
//                return resultType.cast(((Number) result).doubleValue());
//            } else if (resultType == Boolean.class) {
//                return resultType.cast(Boolean.valueOf(result.toString()));
//            }
//        }
//        return null;
//    }

    /**
     * 执行Lua脚本（异步）
     *
     * @param script Lua脚本
     * @param keys   键列表
     * @param values 值列表
     * @return RFuture
     */
//    public RFuture<Object> evalAsync(String script, List<Object> keys, Object... values) {
//        RScript rScript = redissonClient.getScript();
//        return rScript.evalAsync(RScript.Mode.READ_WRITE, script, keys, values);
//    }

    /**
     * 执行Lua脚本（异步，返回指定类型）
     *
     * @param <T>        返回值类型
     * @param script     Lua脚本
     * @param resultType 返回值类型
     * @param keys       键列表
     * @param values     值列表
     * @return RFuture
     */
//    public <T> RFuture<T> evalAsync(String script, Class<T> resultType, List<Object> keys, Object... values) {
//        RFuture<Object> future = evalAsync(script, keys, values);
//        return future.thenApply(result -> {
//            if (result != null) {
//                if (resultType.isInstance(result)) {
//                    return resultType.cast(result);
//                }
//                // 尝试类型转换
//                if (resultType == String.class) {
//                    return resultType.cast(result.toString());
//                } else if (resultType == Long.class && result instanceof Number) {
//                    return resultType.cast(((Number) result).longValue());
//                } else if (resultType == Integer.class && result instanceof Number) {
//                    return resultType.cast(((Number) result).intValue());
//                } else if (resultType == Double.class && result instanceof Number) {
//                    return resultType.cast(((Number) result).doubleValue());
//                } else if (resultType == Boolean.class) {
//                    return resultType.cast(Boolean.valueOf(result.toString()));
//                }
//            }
//            return null;
//        });
//    }

    // =============================Batch============================

    /**
     * 批量操作
     *
     * @param consumer 批量操作消费者
     */
    public void executeBatch(Consumer<RBatch> consumer) {
        RBatch batch = redissonClient.createBatch();
        consumer.accept(batch);
        batch.execute();
    }

    /**
     * 批量操作（异步）
     *
     * @param consumer 批量操作消费者
     * @return RFuture
     */
    public RFuture<BatchResult<?>> executeBatchAsync(Consumer<RBatch> consumer) {
        RBatch batch = redissonClient.createBatch();
        consumer.accept(batch);
        return batch.executeAsync();
    }

    // =============================Other============================

    /**
     * 获取RedissonClient
     *
     * @return RedissonClient
     */
    public RedissonClient getRedissonClient() {
        return redissonClient;
    }

    /**
     * 关闭RedissonClient
     */
    public void shutdown() {
        if (redissonClient != null && !redissonClient.isShutdown()) {
            redissonClient.shutdown();
        }
    }
}