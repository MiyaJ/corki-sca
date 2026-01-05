package com.corki.admin.mq;

import cn.hutool.json.JSONUtil;
import com.corki.admin.model.dto.MqDTO;
import com.corki.sca.mq.consumer.BaseMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;


@Slf4j
@Component
@RocketMQMessageListener(
        topic = "TBW102",                    // 订阅的主题
        consumerGroup = "demo-consumer-group"              // 订阅的标签（可选，默认订阅所有）
)
public class DemoMessageListener extends BaseMessageListener {
    @Override
    public void onMessage(MessageExt message) {
        super.onMessage(message);
    }

    @Override
    protected void handleMessage(MessageExt message) throws Exception {
        // 解析消息
        String body = new String(message.getBody(), StandardCharsets.UTF_8);
        MqDTO mqDTO = JSONUtil.toBean(body, MqDTO.class);

        // 处理业务逻辑
        log.info("DemoMessageListener--->处理业务逻辑: {}", mqDTO.getOrderNo());
    }
}
