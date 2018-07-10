package com.jit.sensor.service;

import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;

import java.net.URISyntaxException;

public class MqttConfigService {

    public void configure(MQTT mqtt) throws URISyntaxException {

        mqtt.setHost("tcp://39.106.54.222:1883");
        mqtt.setClientId("876543211");
        mqtt.setCleanSession(true);
        mqtt.setKeepAlive((short) 60);
        mqtt.setUserName("admin");
        mqtt.setPassword("admin");
        mqtt.setWillTopic("WillTopic");
        mqtt.setWillMessage("willMessage");
        mqtt.setWillQos(QoS.AT_LEAST_ONCE);
        mqtt.setWillRetain(true);
        mqtt.setVersion("3.1.1");
        mqtt.setConnectAttemptsMax(10);
        mqtt.setReconnectAttemptsMax(3);
        mqtt.setReconnectDelay(10);
        mqtt.setReconnectDelayMax(30000);
        mqtt.setReconnectBackOffMultiplier(2);
        mqtt.setReceiveBufferSize(65536);
        mqtt.setSendBufferSize(65536);
        mqtt.setTrafficClass(8);
        mqtt.setMaxReadRate(0);
        mqtt.setMaxWriteRate(0);
    }
}