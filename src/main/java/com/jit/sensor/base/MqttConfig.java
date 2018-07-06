package com.jit.sensor.base;

import org.fusesource.mqtt.client.QoS;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import static org.fusesource.mqtt.client.QoS.AT_LEAST_ONCE;
import static org.fusesource.mqtt.client.QoS.AT_MOST_ONCE;
import static org.fusesource.mqtt.client.QoS.EXACTLY_ONCE;



@lombok.Setter
@lombok.Getter
@Configuration
@PropertySource("classpath:/config/mqtt.properties")
@ConfigurationProperties(prefix = "mqtt",ignoreInvalidFields = false)
@Component
public class MqttConfig {


    String host;
    String clientId;
    Boolean cleanSession;
    short keepalive;
    String userName;
    String password;
    String willTopic;
    String willmessage;
    QoS willqos = AT_LEAST_ONCE;
    Boolean WillRetain ;
    String version ;
    long connectattemptsmax ;
    long reconnectattemptsmax;
    long reconnectdelay;
    long reconnectDelaymax;
    int reconnectbackOffmultiplie;
    int receivebuffersize;
    int sendbuffersize;
    int trafficclass;
    int maxreadrate;
    int maxwriterate;
}
