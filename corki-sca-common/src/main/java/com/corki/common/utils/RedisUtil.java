package com.corki.common.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Redis工具类
 *
 * @author Corki
 * @since 2025-12-16
 */
@Slf4j
@Component
@SuppressWarnings("unchecked")
public class RedisUtil {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    // =============================common============================

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            log.error("设置缓存失效时间失败，key: {}, time: {}", key, time, e);
            return false;
        }
    }

    /**
     * 指定缓存失效时间
     *
     * @param key      键
     * @param time     时间
     * @param timeUnit 时间单位
     */
    public boolean expire(String key, long time, TimeUnit timeUnit) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, timeUnit);
            }
            return true;
        } catch (Exception e) {
            log.error("设置缓存失效时间失败，key: {}, time: {}, unit: {}", key, time, timeUnit, e);
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("判断key是否存在失败，key: {}", key, e);
            return false;
        }
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete((Collection<String>) CollUtil.newArrayList(key));
            }
        }
    }

    /**
     * 批量删除key
     *
     * @param keys key集合
     */
    public void del(Collection<String> keys) {
        if (CollUtil.isNotEmpty(keys)) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 模糊删除
     *
     * @param pattern 匹配模式
     */
    public void delByPattern(String pattern) {
        Set<String> keys = keys(pattern);
        if (CollUtil.isNotEmpty(keys)) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 模糊查询key
     *
     * @param pattern 匹配模式
     * @return key集合
     */
    public Set<String> keys(String pattern) {
        try {
            return redisTemplate.keys(pattern);
        } catch (Exception e) {
            log.error("模糊查询key失败，pattern: {}", pattern, e);
            return Collections.emptySet();
        }
    }

    /**
     * 重命名key
     *
     * @param oldKey 旧key
     * @param newKey 新key
     */
    public void rename(String oldKey, String newKey) {
        redisTemplate.rename(oldKey, newKey);
    }

    // ============================String=============================

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error("设置缓存失败，key: {}, value: {}", key, value, e);
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error("设置缓存失败，key: {}, value: {}, time: {}", key, value, time, e);
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key      键
     * @param value    值
     * @param time     时间
     * @param timeUnit 时间单位
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long time, TimeUnit timeUnit) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, timeUnit);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error("设置缓存失败，key: {}, value: {}, time: {}, unit: {}", key, value, time, timeUnit, e);
            return false;
        }
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    /**
     * 设置并返回旧值
     *
     * @param key   键
     * @param value 新值
     * @return 旧值
     */
    public Object getAndSet(String key, Object value) {
        return redisTemplate.opsForValue().getAndSet(key, value);
    }

    /**
     * 批量获取
     *
     * @param keys key集合
     * @return 值列表
     */
    public List<Object> multiGet(Collection<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    /**
     * 批量设置
     *
     * @param map key-value映射
     */
    public void multiSet(Map<String, Object> map) {
        redisTemplate.opsForValue().multiSet(map);
    }

    // ================================Map=================================

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     */
    public Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     */
    public boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            log.error("HashSet失败，key: {}, map: {}", key, map, e);
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("HashSet失败，key: {}, map: {}, time: {}", key, map, time, e);
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            log.error("HashPut失败，key: {}, item: {}, value: {}", key, item, value, e);
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("HashPut失败，key: {}, item: {}, value: {}, time: {}", key, item, value, time, e);
            return false;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     */
    public double hincr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     */
    public double hdecr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }

    /**
     * 获取Hash中的所有字段
     *
     * @param key 键
     * @return 字段集合
     */
    public Set<Object> hkeys(String key) {
        return redisTemplate.opsForHash().keys(key);
    }

    /**
     * 获取Hash中的所有值
     *
     * @param key 键
     * @return 值集合
     */
    public List<Object> hvals(String key) {
        return redisTemplate.opsForHash().values(key);
    }

    /**
     * 获取Hash的大小
     *
     * @param key 键
     * @return 大小
     */
    public long hsize(String key) {
        return redisTemplate.opsForHash().size(key);
    }

    // ============================set=============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     */
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error("获取Set失败，key: {}", key, e);
            return Collections.emptySet();
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key, Object value) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
        } catch (Exception e) {
            log.error("判断Set中是否存在值失败，key: {}, value: {}", key, value, e);
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            log.error("Set添加失败，key: {}, values: {}", key, values, e);
            return 0;
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(String key, long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            log.error("Set添加失败，key: {}, time: {}, values: {}", key, time, values, e);
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     */
    public long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            log.error("获取Set大小失败，key: {}", key, e);
            return 0;
        }
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            log.error("Set删除失败，key: {}, values: {}", key, values, e);
            return 0;
        }
    }

    /**
     * 随机获取Set中的一个元素
     *
     * @param key 键
     * @return 元素
     */
    public Object sRandomMember(String key) {
        return redisTemplate.opsForSet().randomMember(key);
    }

    /**
     * 随机获取Set中的多个元素
     *
     * @param key   键
     * @param count 数量
     * @return 元素列表
     */
    public List<Object> sRandomMembers(String key, long count) {
        return redisTemplate.opsForSet().randomMembers(key, count);
    }

    /**
     * 获取两个Set的交集
     *
     * @param key1 键1
     * @param key2 键2
     * @return 交集
     */
    public Set<Object> sIntersect(String key1, String key2) {
        return redisTemplate.opsForSet().intersect(key1, key2);
    }

    /**
     * 获取两个Set的并集
     *
     * @param key1 键1
     * @param key2 键2
     * @return 并集
     */
    public Set<Object> sUnion(String key1, String key2) {
        return redisTemplate.opsForSet().union(key1, key2);
    }

    /**
     * 获取两个Set的差集
     *
     * @param key1 键1
     * @param key2 键2
     * @return 差集
     */
    public Set<Object> sDifference(String key1, String key2) {
        return redisTemplate.opsForSet().difference(key1, key2);
    }

    // ===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     */
    public List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.error("获取List范围失败，key: {}, start: {}, end: {}", key, start, end, e);
            return Collections.emptyList();
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     */
    public long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            log.error("获取List大小失败，key: {}", key, e);
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     */
    public Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            log.error("获取List索引失败，key: {}, index: {}", key, index, e);
            return null;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     */
    public boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            log.error("List右推失败，key: {}, value: {}", key, value, e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     */
    public boolean lSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("List右推失败，key: {}, value: {}, time: {}", key, value, time, e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     */
    public boolean lSet(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            log.error("List批量右推失败，key: {}, value: {}", key, value, e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     */
    public boolean lSet(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("List批量右推失败，key: {}, value: {}, time: {}", key, value, time, e);
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     */
    public boolean lUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            log.error("List更新索引失败，key: {}, index: {}, value: {}", key, index, value, e);
            return false;
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String key, long count, Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            log.error("List移除失败，key: {}, count: {}, value: {}", key, count, value, e);
            return 0;
        }
    }

    /**
     * List左推
     *
     * @param key   键
     * @param value 值
     * @return 列表长度
     */
    public long lLeftPush(String key, Object value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * List右推
     *
     * @param key   键
     * @param value 值
     * @return 列表长度
     */
    public long lRightPush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * List左弹出
     *
     * @param key 键
     * @return 弹出的值
     */
    public Object lLeftPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    /**
     * List右弹出
     *
     * @param key 键
     * @return 弹出的值
     */
    public Object lRightPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    /**
     * 阻塞式左弹出
     *
     * @param key     键
     * @param timeout 超时时间(秒)
     * @return 弹出的值
     */
    public Object lLeftPop(String key, long timeout) {
        return redisTemplate.opsForList().leftPop(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 阻塞式右弹出
     *
     * @param key     键
     * @param timeout 超时时间(秒)
     * @return 弹出的值
     */
    public Object lRightPop(String key, long timeout) {
        return redisTemplate.opsForList().rightPop(key, timeout, TimeUnit.SECONDS);
    }

    // ============================zset=============================

    /**
     * 添加元素到有序集合
     *
     * @param key   键
     * @param value 值
     * @param score 分数
     * @return 成功个数
     */
    public boolean zAdd(String key, Object value, double score) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForZSet().add(key, value, score));
        } catch (Exception e) {
            log.error("ZSet添加失败，key: {}, value: {}, score: {}", key, value, score, e);
            return false;
        }
    }

    /**
     * 批量添加元素到有序集合
     *
     * @param key   键
     * @param values 值和分数的集合
     * @return 成功个数
     */
    public long zAdd(String key, Set<ZSetOperations.TypedTuple<Object>> values) {
        try {
            Long count = redisTemplate.opsForZSet().add(key, values);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("ZSet批量添加失败，key: {}, values: {}", key, values, e);
            return 0;
        }
    }

    /**
     * 获取有序集合的元素个数
     *
     * @param key 键
     */
    public long zCard(String key) {
        try {
            Long count = redisTemplate.opsForZSet().size(key);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("获取ZSet大小失败，key: {}", key, e);
            return 0;
        }
    }

    /**
     * 获取指定分数范围内的元素个数
     *
     * @param key 键
     * @param min 最小分数
     * @param max 最大分数
     */
    public long zCount(String key, double min, double max) {
        try {
            Long count = redisTemplate.opsForZSet().count(key, min, max);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("获取ZSet分数范围内大小失败，key: {}, min: {}, max: {}", key, min, max, e);
            return 0;
        }
    }

    /**
     * 增加元素的分数
     *
     * @param key   键
     * @param value 值
     * @param delta 增量
     */
    public double zIncrementScore(String key, Object value, double delta) {
        return redisTemplate.opsForZSet().incrementScore(key, value, delta);
    }

    /**
     * 获取元素的分数
     *
     * @param key   键
     * @param value 值
     */
    public double zScore(String key, Object value) {
        try {
            Double score = redisTemplate.opsForZSet().score(key, value);
            return score != null ? score : 0;
        } catch (Exception e) {
            log.error("获取ZSet分数失败，key: {}, value: {}", key, value, e);
            return 0;
        }
    }

    /**
     * 获取指定排名范围内的元素（按分数从低到高）
     *
     * @param key   键
     * @param start 开始排名
     * @param end   结束排名
     */
    public Set<Object> zRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().range(key, start, end);
        } catch (Exception e) {
            log.error("获取ZSet排名范围失败，key: {}, start: {}, end: {}", key, start, end, e);
            return Collections.emptySet();
        }
    }

    /**
     * 获取指定分数范围内的元素（按分数从低到高）
     *
     * @param key 键
     * @param min 最小分数
     * @param max 最大分数
     */
    public Set<Object> zRangeByScore(String key, double min, double max) {
        try {
            return redisTemplate.opsForZSet().rangeByScore(key, min, max);
        } catch (Exception e) {
            log.error("获取ZSet分数范围失败，key: {}, min: {}, max: {}", key, min, max, e);
            return Collections.emptySet();
        }
    }

    /**
     * 获取指定排名范围内的元素（按分数从高到低）
     *
     * @param key   键
     * @param start 开始排名
     * @param end   结束排名
     */
    public Set<Object> zReverseRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().reverseRange(key, start, end);
        } catch (Exception e) {
            log.error("获取ZSet反向排名范围失败，key: {}, start: {}, end: {}", key, start, end, e);
            return Collections.emptySet();
        }
    }

    /**
     * 获取指定分数范围内的元素（按分数从高到低）
     *
     * @param key 键
     * @param min 最小分数
     * @param max 最大分数
     */
    public Set<Object> zReverseRangeByScore(String key, double min, double max) {
        try {
            return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
        } catch (Exception e) {
            log.error("获取ZSet反向分数范围失败，key: {}, min: {}, max: {}", key, min, max, e);
            return Collections.emptySet();
        }
    }

    /**
     * 获取元素的排名（从0开始）
     *
     * @param key   键
     * @param value 值
     */
    public long zRank(String key, Object value) {
        try {
            Long rank = redisTemplate.opsForZSet().rank(key, value);
            return rank != null ? rank : -1;
        } catch (Exception e) {
            log.error("获取ZSet排名失败，key: {}, value: {}", key, value, e);
            return -1;
        }
    }

    /**
     * 获取元素的排名（从高到低）
     *
     * @param key   键
     * @param value 值
     */
    public long zReverseRank(String key, Object value) {
        try {
            Long rank = redisTemplate.opsForZSet().reverseRank(key, value);
            return rank != null ? rank : -1;
        } catch (Exception e) {
            log.error("获取ZSet反向排名失败，key: {}, value: {}", key, value, e);
            return -1;
        }
    }

    /**
     * 删除有序集合中的元素
     *
     * @param key    键
     * @param values 值
     * @return 删除个数
     */
    public long zRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForZSet().remove(key, values);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("ZSet删除失败，key: {}, values: {}", key, values, e);
            return 0;
        }
    }

    /**
     * 删除指定排名范围内的元素
     *
     * @param key   键
     * @param start 开始排名
     * @param end   结束排名
     * @return 删除个数
     */
    public long zRemoveRange(String key, long start, long end) {
        try {
            Long count = redisTemplate.opsForZSet().removeRange(key, start, end);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("ZSet删除排名范围失败，key: {}, start: {}, end: {}", key, start, end, e);
            return 0;
        }
    }

    /**
     * 删除指定分数范围内的元素
     *
     * @param key 键
     * @param min 最小分数
     * @param max 最大分数
     * @return 删除个数
     */
    public long zRemoveRangeByScore(String key, double min, double max) {
        try {
            Long count = redisTemplate.opsForZSet().removeRangeByScore(key, min, max);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("ZSet删除分数范围失败，key: {}, min: {}, max: {}", key, min, max, e);
            return 0;
        }
    }

    // ============================lock=============================

    /**
     * 获取分布式锁
     *
     * @param lockKey 锁key
     * @param value   值
     * @param expire  过期时间(秒)
     * @return 是否获取成功
     */
    public boolean setLock(String lockKey, Object value, long expire) {
        try {
            Boolean result = redisTemplate.opsForValue().setIfAbsent(lockKey, value, expire, TimeUnit.SECONDS);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("获取分布式锁失败，key: {}, value: {}, expire: {}", lockKey, value, expire, e);
            return false;
        }
    }

    /**
     * 释放分布式锁
     *
     * @param lockKey 锁key
     * @param value   值
     * @return 是否释放成功
     */
    public boolean releaseLock(String lockKey, Object value) {
        try {
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
            Long result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), value);
            return Long.valueOf(1).equals(result);
        } catch (Exception e) {
            log.error("释放分布式锁失败，key: {}, value: {}", lockKey, value, e);
            return false;
        }
    }

    // ============================序列化工具=============================

    /**
     * 初始化RedisTemplate的序列化方式
     */
    private void initRedisTemplate() {
        RedisSerializer<String> stringSerializer = new StringRedisSerializer();
        RedisSerializer<Object> jsonSerializer = new GenericJackson2JsonRedisSerializer();

        // 设置key和value的序列化规则
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(jsonSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(jsonSerializer);
    }

    // ============================类型转换=============================

    /**
     * 获取String类型的值
     */
    public String getString(String key) {
        Object value = get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * 获取Integer类型的值
     */
    public Integer getInteger(String key) {
        String value = getString(key);
        return StrUtil.isNotBlank(value) ? Integer.parseInt(value) : null;
    }

    /**
     * 获取Long类型的值
     */
    public Long getLong(String key) {
        String value = getString(key);
        return StrUtil.isNotBlank(value) ? Long.parseLong(value) : null;
    }

    /**
     * 获取Boolean类型的值
     */
    public Boolean getBoolean(String key) {
        String value = getString(key);
        return StrUtil.isNotBlank(value) ? Boolean.parseBoolean(value) : null;
    }

    /**
     * 获取Double类型的值
     */
    public Double getDouble(String key) {
        String value = getString(key);
        return StrUtil.isNotBlank(value) ? Double.parseDouble(value) : null;
    }
}