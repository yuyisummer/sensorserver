package com.jit.sensor.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jit.sensor.entity.TResult;
import com.jit.sensor.entity.TResultCode;
import com.jit.sensor.global.MqttClient;
import com.jit.sensor.util.MqttConfig;
import com.jit.sensor.util.TrainsformUtil;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.*;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author chy
 */
@Service
public class MqttConfigService {
    private MqttConfig mqttConfig = new MqttConfig();
    /**
     * 下发数据,线程阻塞
     */
    private int clientIdOffset = 1;
    private int maxOffset = 100;

    /**
     * 添加订阅
     */
    public Object addSubscription(JSONObject jsonObject) {
        String recvTopic = "application/2" +
                "/device/" + jsonObject.getString("Deveui") +
                "/rx";
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
        for (Topic newTopic : newTopics) {
            System.out.println("newTopic\t" + newTopic);
        }

        if (Arrays.equals(newTopics, MqttClient.topics)) {
            return TResult.failure(TResultCode.SUBSCRIPTION_EXISTS);
        } else {
            callbackConnection.subscribe(newTopics,
                    new Callback<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            MqttClient.topics = topicSet.toArray(new Topic[topicSet.size()]);
                            System.out.println("\t订阅成功");
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            System.out.println("\t订阅失败");
                        }
                    });
            return TResult.success();
        }
    }

    /**
     * 删除订阅
     */
    public Object deleteSubscription(JSONObject jsonObject) {
        Topic topicToDelete = new Topic("application/2" +
                "/device/" + jsonObject.getString("Deveui") +
                "/rx", QoS.EXACTLY_ONCE);

        Set<Topic> topicSet = new HashSet<>(Arrays.asList(MqttClient.topics));
        CallbackConnection callbackConnection = MqttClient.callbackConnection;
        /*
         * 取消已订阅频道
         * */
        if (topicSet.contains(topicToDelete)) {
            topicSet.remove(topicToDelete);

            UTF8Buffer[] utf8Buffers = {new UTF8Buffer(topicToDelete.toString())};
            callbackConnection.unsubscribe(utf8Buffers, new Callback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    System.out.println("取消订阅成功");
                }

                @Override
                public void onFailure(Throwable throwable) {
                    System.out.println("取消订阅失败");
                }
            });
            MqttClient.topics = topicSet.toArray(new Topic[topicSet.size()]);
            return TResult.success();
        } else {
            return TResult.failure(TResultCode.UNSUBSCRIPTION_NOT_EXISTS);
        }
    }

    /**
     * 下发数据
     */
    public Object downloadData(JSONObject jsonObject) throws Exception {
        String sendTopic = "application/2" +
                "/device/" + jsonObject.getString("Deveui") +
                "/tx";

        String ackTopic = "application/2" +
                "/device/" + jsonObject.getString("Deveui") +
                "/rx";
        Topic[] ackTopics = {new Topic(ackTopic, QoS.EXACTLY_ONCE)};


        LinkedHashMap<Object, Object> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("reference", jsonObject.getString("reference"));
        linkedHashMap.put("confirmed", jsonObject.getString("confirmed"));
        linkedHashMap.put("fPort", jsonObject.getString("fPort"));
        String messageKey = "message";
        /*
         * TODO:  ACK校验，字符串转化为2进制，二进制encode，ack确认包解码，转换2进制
         * */
        TrainsformUtil trainsformUtil = new TrainsformUtil();
        String binaryString = trainsformUtil.hexString2BinaryString(jsonObject.getString(messageKey));

        Base64.Encoder encoder = Base64.getEncoder();
        linkedHashMap.put("data", encoder.encodeToString(binaryString.getBytes()));

        Base64.Decoder decoder = Base64.getDecoder();
        MQTT mqtt = new MQTT();
        mqttConfig.configure(mqtt);
        String newClientID = String.valueOf(Integer.parseInt(String.valueOf(mqtt.getClientId())) + clientIdOffset);
        mqtt.setClientId(newClientID);
        clientIdOffset++;
        if (clientIdOffset == maxOffset) {
            clientIdOffset = 1;
        }
        BlockingConnection connection = mqtt.blockingConnection();
        connection.connect();
        connection.subscribe(ackTopics);
        connection.publish(sendTopic, JSONObject.toJSON(linkedHashMap).toString().getBytes(),
                QoS.EXACTLY_ONCE, false);

        Message message = connection.receive();
        System.out.println("ack message\t" + message.getPayloadBuffer().toString().split("ascii: ")[1]);

        JSONObject ackJsonObject = JSON.parseObject(message.getPayloadBuffer().toString().split("ascii: ")[1]);
        byte[] decodedData = decoder.decode(ackJsonObject.getString("data"));
        if (decodedData[0] == 0 && decodedData[1] == 0) {
            return TResult.success();
        } else {
            return TResult.failure(TResultCode.DOWNLOADDATA_BROKEN);
        }
    }

    /**
     * 查看已订阅频道
     */
    public Object getSubscription() {
        if (MqttClient.topics == null || MqttClient.topics.length == 0) {
            return TResult.failure(TResultCode.SUBSCRIPTION_EMPTY);
        } else {
            return TResult.success(Arrays.toString(MqttClient.topics));
        }
    }
}
