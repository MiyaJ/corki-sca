package com.corki.admin.controller;

import cn.hutool.json.JSONUtil;
import com.corki.admin.feign.MemberFeignClient;
import com.corki.admin.model.dto.MqDTO;
import com.corki.core.model.R;
import com.corki.sca.mq.producer.RocketMQTemplate;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/test")
@RefreshScope
public class TestController {

    @Value("${test:777}")
    private String test;

    @Resource
    private MemberFeignClient memberFeignClient;

    @GetMapping("/hello")
    public String hello() {
        return "hello world: " + test;
    }

    @GetMapping("/feign/member/test")
    public R<String> test() {
        return memberFeignClient.test();
    }

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @GetMapping("/testMq")
    public R<String> testMq() {
        try {
            MqDTO mqDTO = new MqDTO();
            mqDTO.setOrderNo("123456");
            mqDTO.setName("corki");
            mqDTO.setTitle("测试");
            mqDTO.setDescription("测试消息");

            // 1. 同步发送消息（可靠性高，速度慢）
            // 注意：Topic 需要提前创建，或者 Broker 开启 autoCreateTopicEnable=true
            SendResult receipt = rocketMQTemplate.syncSend(
                    "order_topic_test",              // Topic（使用下划线命名）
                    null,                             // Tags
                    mqDTO.getOrderNo(),              // Keys（用于索引和查询）
                    JSONUtil.toJsonStr(mqDTO)        // 消息内容
            );

            log.info("消息发送成功 - MsgId: {}", receipt.getMsgId());
            return R.success("消息发送成功，MsgId: " + receipt.getMsgId());

        } catch (Exception e) {
            log.error("消息发送失败", e);
            return R.fail("消息发送失败: " + e.getMessage());
        }
    }
}
