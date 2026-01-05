package com.corki.sca.mq.producer;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * RocketMQ 生产者模板工具类
 * 封装 RocketMQ 消息发送的常用操作，提供同步、异步、单向三种发送方式
 *
 * @author Corki
 * @since 2026-01-04
 */
@Getter
@Slf4j
@Component
public class RocketMQTemplate {

    /**
     * -- GETTER --
     *  获取生产者实例
     *  <p>
     *  用于更复杂的场景，可以直接使用 DefaultMQProducer 的所有功能
     *
     * @return DefaultMQProducer 实例
     */
    private final DefaultMQProducer producer;

    public RocketMQTemplate(DefaultMQProducer producer) {
        this.producer = producer;
    }

    /**
     * 同步发送消息
     * <p>
     * 这种方式可靠性最高，但发送速度较慢，阻塞直到收到响应
     *
     * @param topic   主题
     * @param tags    标签
     * @param keys    消息键（用于索引和查询）
     * @param content 消息内容
     * @return 发送结果
     * @throws Exception 发送失败时抛出异常
     */
    public SendResult syncSend(String topic, String tags, String keys, String content) throws Exception {
        Message message = buildMessage(topic, tags, keys, content);
        log.info("RocketMQ 同步发送消息 - Topic: {}, Tags: {}, Keys: {}, Content: {}", topic, tags, keys, content);
        return producer.send(message);
    }

    /**
     * 同步发送消息（无标签）
     *
     * @param topic   主题
     * @param keys    消息键
     * @param content 消息内容
     * @return 发送结果
     * @throws Exception 发送失败时抛出异常
     */
    public SendResult syncSend(String topic, String keys, String content) throws Exception {
        return syncSend(topic, null, keys, content);
    }

    /**
     * 同步发送消息（无标签、无键）
     *
     * @param topic   主题
     * @param content 消息内容
     * @return 发送结果
     * @throws Exception 发送失败时抛出异常
     */
    public SendResult syncSend(String topic, String content) throws Exception {
        return syncSend(topic, null, null, content);
    }

    /**
     * 异步发送消息
     * <p>
     * 这种方式发送速度快，不阻塞，但需要在回调中处理结果
     *
     * @param topic        主题
     * @param tags         标签
     * @param keys         消息键
     * @param content      消息内容
     * @param sendCallback 发送回调
     * @throws Exception 发送失败时抛出异常
     */
    public void asyncSend(String topic, String tags, String keys, String content, SendCallback sendCallback) throws Exception {
        Message message = buildMessage(topic, tags, keys, content);
        log.info("RocketMQ 异步发送消息 - Topic: {}, Tags: {}, Keys: {}, Content: {}", topic, tags, keys, content);
        producer.send(message, sendCallback);
    }

    /**
     * 异步发送消息（无标签）
     *
     * @param topic        主题
     * @param keys         消息键
     * @param content      消息内容
     * @param sendCallback 发送回调
     * @throws Exception 发送失败时抛出异常
     */
    public void asyncSend(String topic, String keys, String content, SendCallback sendCallback) throws Exception {
        asyncSend(topic, null, keys, content, sendCallback);
    }

    /**
     * 异步发送消息（无标签、无键）
     *
     * @param topic        主题
     * @param content      消息内容
     * @param sendCallback 发送回调
     * @throws Exception 发送失败时抛出异常
     */
    public void asyncSend(String topic, String content, SendCallback sendCallback) throws Exception {
        asyncSend(topic, null, null, content, sendCallback);
    }

    /**
     * 单向发送消息
     * <p>
     * 这种方式发送速度最快，但不保证可靠性，不关心发送结果，适用于不重要的日志类消息
     *
     * @param topic   主题
     * @param tags    标签
     * @param keys    消息键
     * @param content 消息内容
     * @throws Exception 发送失败时抛出异常
     */
    public void oneWaySend(String topic, String tags, String keys, String content) throws Exception {
        Message message = buildMessage(topic, tags, keys, content);
        log.info("RocketMQ 单向发送消息 - Topic: {}, Tags: {}, Keys: {}, Content: {}", topic, tags, keys, content);
        producer.sendOneway(message);
    }

    /**
     * 单向发送消息（无标签）
     *
     * @param topic   主题
     * @param keys    消息键
     * @param content 消息内容
     * @throws Exception 发送失败时抛出异常
     */
    public void oneWaySend(String topic, String keys, String content) throws Exception {
        oneWaySend(topic, null, keys, content);
    }

    /**
     * 发送延时消息（等级）
     * <p>
     * RocketMQ 支持的延时等级：1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
     * 对应等级：1  2  3   4   5  6  7  8  9  10 11 12 13 14  15  16  17  18
     *
     * @param topic        主题
     * @param tags         标签
     * @param keys         消息键
     * @param content      消息内容
     * @param delayLevel   延时等级 (1-18)
     * @return 发送结果
     * @throws Exception 发送失败时抛出异常
     */
    public SendResult sendDelayMessage(String topic, String tags, String keys, String content, int delayLevel) throws Exception {
        Message message = buildMessage(topic, tags, keys, content);
        message.setDelayTimeLevel(delayLevel);
        log.info("RocketMQ 发送延时消息 - Topic: {}, Tags: {}, Keys: {}, DelayLevel: {}, Content: {}", topic, tags, keys, delayLevel, content);
        return producer.send(message);
    }

    /**
     * 发送延时消息（等级）无标签
     *
     * @param topic        主题
     * @param keys         消息键
     * @param content      消息内容
     * @param delayLevel   延时等级 (1-18)
     * @return 发送结果
     * @throws Exception 发送失败时抛出异常
     */
    public SendResult sendDelayMessage(String topic, String keys, String content, int delayLevel) throws Exception {
        return sendDelayMessage(topic, null, keys, content, delayLevel);
    }

    /**
     * 构建消息对象
     *
     * @param topic   主题
     * @param tags    标签（可为空）
     * @param keys    消息键（可为空）
     * @param content 消息内容
     * @return Message 对象
     */
    private Message buildMessage(String topic, String tags, String keys, String content) {
        String fullTopic = topic;
        if (tags != null && !tags.isEmpty()) {
            fullTopic = topic + "_" + tags;
        }
        Message message = new Message(fullTopic, content.getBytes(StandardCharsets.UTF_8));
        if (keys != null && !keys.isEmpty()) {
            message.setKeys(keys);
        }
        return message;
    }

}
