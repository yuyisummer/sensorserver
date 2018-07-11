package com.jit.sensor.Service;

import com.alibaba.fastjson.JSONObject;
import com.jit.sensor.Entity.TResult;
import com.jit.sensor.Entity.TResultCode;
import com.jit.sensor.Global.MqttClient;
import com.jit.sensor.Util.MqttConfig;
import org.fusesource.mqtt.client.*;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author chy
 */
@Service
public class MqttConfigService {
    private MqttConfig mqttConfig = new MqttConfig();

    public Object addSubscription(JSONObject jsonObject) {
        String recvTopic = "application/" + jsonObject.getString("Application") +
                "/device/" + jsonObject.getString("Deveui") +
                "/" + jsonObject.getString("Channel");
        CallbackConnection callbackConnection = MqttClient.callbackConnection;

        /*
         * 订阅频道
         * */
        Set<Topic> topicSet;

        if (MqttClient.topics != null) {
            topicSet = new HashSet<>(Arrays.asList(MqttClient.topics));
        } else {
            topicSet = new HashSet<>();
        }
        topicSet.add(new Topic(recvTopic, QoS.EXACTLY_ONCE));
        System.out.println("topiclist.size\t" + topicSet.size());
        Topic[] newTopics = topicSet.toArray(new Topic[topicSet.size()]);
        MqttClient.topics = topicSet.toArray(new Topic[topicSet.size()]);
        for (Topic newTopic : newTopics) {
            System.out.println("newTopic\t" + newTopic);
        }

        if (Arrays.equals(newTopics, MqttClient.topics)) {
            return TResult.failure(TResultCode.SUBSCRIPTION_EXISTS);
        } else {
            callbackConnection.subscribe(newTopics,
                    new Callback<byte[]>() {
                        // 订阅主题成功
                        @Override
                        public void onSuccess(byte[] qoses) {
                            System.out.println("========订阅成功=======");
                        }

                        // 订阅主题失败
                        @Override
                        public void onFailure(Throwable value) {
                            System.out.println("========订阅失败=======\t" + value);
                            callbackConnection.disconnect(null);
                        }
                    });
            return TResult.success("频道订阅成功");
        }
    }

    /*
     *  线程阻塞，返回结果给前端
     * */
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
        String messageKey = "message";
        linkedHashMap.put("data", new String(encoder.encode((jsonObject.getString(messageKey)).getBytes())));

        MQTT mqtt = new MQTT();
        mqttConfig.configure(mqtt);
        BlockingConnection connection = mqtt.blockingConnection();
        connection.connect();
        connection.publish(sendTopic, JSONObject.toJSON(linkedHashMap).toString().getBytes(),
                QoS.EXACTLY_ONCE, false);

        connection.subscribe(sendTopics);
        Message message = connection.receive();

        if (jsonObject.getString(messageKey).equals(new String(message.getPayload()))) {
            return TResult.failure("publish failed");
        } else {
            return TResult.success("publish success");
        }

    }
}
