package com.ruoyi.web.core.config.mqtt;

import com.ruoyi.common.utils.file.FileUtils;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

/**
 * @author by Guoshun
 * @version 1.0.0
 * @description 消息回调返回
 * @date 2023/12/12 17:27
 */
@Component
public class MessageCallbackListener implements IMqttMessageListener, MqttCallback {

    @Resource
    private MQTTClientUtils mqttClientUtils;


    @Override
    public void connectionLost(Throwable throwable) {

        System.out.println(throwable.getMessage());
        System.out.println("连接丢失，尝试重新连接...");

        mqttClientUtils.connect();
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String messageBody = new String(message.getPayload(), StandardCharsets.UTF_8);
        System.out.println("收到消息：" + topic + ", 消息内容是：" + messageBody);

        FileUtils.writeToFile("D:/mqtt/message.txt", "收到消息：" + topic + ", 消息内容是：" + messageBody);


    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }

}


