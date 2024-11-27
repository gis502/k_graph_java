package com.ruoyi.web.core.config.mqtt;


import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author by Guoshun
 * @version 1.0.0
 * @description MQTT服务类，负责调用发送消息
 * @date 2023/12/12 16:53
 */
@Service
public class MQTTService {

    @Resource
    private MQTTClientUtils mqttClientUtils;

    /**
     * 向主题发送消息
     * @param topicName
     * @param message
     */
    public void sendMessage(String topicName, String message){
        mqttClientUtils.publish(topicName, message);
    }

    /**
     * 向主题发送消息
     * @param topicName 主题名称
     * @param qos qos
     * @param message 具体消息
     */
    public void sendMessage(String topicName,int qos, String message){
        mqttClientUtils.publish(topicName, qos, message);
    }
}
