package com.jit.sensor.Service;

import com.alibaba.fastjson.JSONObject;
import com.jit.sensor.Entity.TResult;
import com.jit.sensor.Util.MqttConfig;
import org.fusesource.mqtt.client.*;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.LinkedHashMap;

@Service
public class MqttConfigService {
    private MqttConfig mqttConfig = new MqttConfig();

    public Object uploadData(JSONObject jsonObject) {
        String recvTopic = "application/" + jsonObject.getString("Application") +
                "/device/" + jsonObject.getString("Deveui") +
                "/" + jsonObject.getString("Channel");
        Topic[] recvTopics = {new Topic(recvTopic, QoS.AT_LEAST_ONCE)};

        return null;
    }

    public Object downloadData(JSONObject jsonObject) throws Exception {
        String sendTopic = "application/" + jsonObject.getString("Application") +
                "/device/" + jsonObject.getString("Deveui") +
                "/" + jsonObject.getString("Channel");
        Topic[] sendTopics = {new Topic(sendTopic, QoS.AT_LEAST_ONCE)};


        Base64.Encoder encoder = Base64.getEncoder();
        LinkedHashMap<Object, Object> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("reference", "abcd1234");
        linkedHashMap.put("confirmed", true);
        linkedHashMap.put("fPort", 10);
        linkedHashMap.put("data", new String(encoder.encode("send by chy".getBytes())));

        MQTT mqtt = new MQTT();
        mqttConfig.configure(mqtt);
        BlockingConnection connection = mqtt.blockingConnection();
        connection.connect();
        connection.publish(sendTopic, JSONObject.toJSON(linkedHashMap).toString().getBytes(),
                QoS.EXACTLY_ONCE, true);

        connection.subscribe(sendTopics);
        Message message = connection.receive();
        connection.disconnect();

        if (message != null) {
            return TResult.success();
        } else {
            return TResult.failure("publish failed");
        }
    }
}
