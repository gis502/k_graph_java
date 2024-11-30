package com.ruoyi.web.core.config.mqtt;

import lombok.Data;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author by Guoshun
 * @version 1.0.0
 * @description mqtt配置类
 * @date 2023/12/12 15:10
 */
@Configuration
@ConfigurationProperties(MQTTConfigBuilder.PREFIX)
@Data
public class MQTTConfigBuilder {

    //配置的名称
    public static final String PREFIX = "publish.mqtt";
    /**
     * 服务端地址
     */
    private String host;

    /**
     * 客户端id
     */
    private String clientId;
    /**
     * 配置链接项
     */
    private MqttConnectOptions options;

}
