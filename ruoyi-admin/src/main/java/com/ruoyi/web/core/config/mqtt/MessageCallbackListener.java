package com.ruoyi.web.core.config.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @author by Guoshun
 * @version 1.0.0
 * @description 消息回调返回
 * @date 2023/12/12 17:27
 */
@Component
public class MessageCallbackListener implements IMqttMessageListener {

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String messageBody = new String(message.getPayload(), StandardCharsets.UTF_8);
        System.out.println("收到消息："+topic+", 消息内容是："+ messageBody);
    }
}


