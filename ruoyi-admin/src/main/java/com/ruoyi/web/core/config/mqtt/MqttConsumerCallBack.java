package com.ruoyi.web.core.config.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * @author: xiaodemos
 * @date: 2024-11-25 19:03
 * @description: 消费者回调函数
 */

@Slf4j
public class MqttConsumerCallBack implements MqttCallback {

    /**
     * 客户端断开连接的回调
     */
    @Override
    public void connectionLost(Throwable throwable) {
        log.error("Consumer 与服务器断开连接，可重连");
    }

    /**
     * 消息到达的回调
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

        //TODO 收到消息后写入后续业务逻辑
        log.info(String.format("Consumer 接收消息主题 : %s", topic));
        log.info(String.format("Consumer 接收消息Qos : %d", message.getQos()));
        log.info(String.format("Consumer 接收消息内容 : %s", new String(message.getPayload())));
        log.info(String.format("Consumer 接收消息retained : %b", message.isRetained()));
    }

    /**
     * 消息发布成功的回调
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

        log.info("Consumer 消息发布成功:{}", iMqttDeliveryToken);
    }
}
