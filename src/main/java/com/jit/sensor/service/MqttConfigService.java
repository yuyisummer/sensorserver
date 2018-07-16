package com.jit.sensor.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jit.sensor.entity.Relay;
import com.jit.sensor.entity.TResult;
import com.jit.sensor.entity.TResultCode;
import com.jit.sensor.global.MqttClient;
import com.jit.sensor.mapper.RelayMapper;
import com.jit.sensor.util.MqttConfig;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author chy
 */
@Service
public class MqttConfigService {
    @Autowired
    RelayMapper relayMapper;
    private MqttConfig mqttConfig = new MqttConfig();
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
            /*
             * TODO:多线程，结果判定存疑
             * */
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
     * 设置传感器上传周期
     */
    public Object updateSensorCycle(JSONObject jsonObject) throws Exception {

        byte[] decodedAckData = getPublishAck(jsonObject);
        // 下发成功
        if (decodedAckData[0] == 0) {
            return TResult.success();
        } else {
            return TResult.failure(TResultCode.DOWNLOADDATA_BROKEN);
        }
    }

    /**
     * 设置继电器开关
     */
    public Object setRelayStatus(JSONObject jsonObject) throws Exception {

        String[] datas = jsonObject.getString("message").split(" ");
        byte[] databytes = new byte[5];
        int leek = 0;
        for (String leekString : datas) {
            databytes[leek] = (byte) Integer.parseInt(leekString, 16);
            leek++;
        }

        Relay relay = new Relay();
        relay.setDeveui(jsonObject.getString("Deveui"));
        Relay relay1 = relayMapper.selectByPrimaryKey(relay.getDeveui());
        // 数据库有此传感器开关数据，数据库开关状态与目标开关状态相同，直接返回失败
        if (relay1 != null) {
            if (relay1.getDeveui().equals(Byte.toString(databytes[2]))) {
                return TResult.failure(TResultCode.CHANGE_RELAY_STATUS_FAILURE);
            }
        }
        byte[] decodedAckData = getPublishAck(jsonObject);
        // 数据下发成功
        if (decodedAckData[0] == 0) {
            Relay relay2 = new Relay();
            relay2.setDeveui(jsonObject.getString("Deveui"));
            // 设置对象属性，开FF,关00
            if (databytes[2] == 0) {
                relay2.setRelayStatus("00");
            } else {
                relay2.setRelayStatus("FF");
            }
            // 操作数据库
            if (relay1 == null) {
                relayMapper.insert(relay2);
            } else {
                relayMapper.updateByPrimaryKey(relay2);
            }
            return TResult.success();
        } else {
            // ack校验失败
            return TResult.failure(TResultCode.DOWNLOADDATA_BROKEN);
        }
    }

    /**
     * 获取publish结果
     */
    private byte[] getPublishAck(JSONObject jsonObject) throws Exception {
        String sendTopic = "application/2/device/" +
                jsonObject.getString("Deveui") +
                "/tx";
        String ackTopic = "application/2/device/" +
                jsonObject.getString("Deveui") +
                "/rx";
        Topic[] ackTopics = {new Topic(ackTopic, QoS.EXACTLY_ONCE)};
        LinkedHashMap<Object, Object> linkedHashMap = new LinkedHashMap<>();
        String messageKey = "message";

        linkedHashMap.put("reference", jsonObject.getString("reference"));
        linkedHashMap.put("confirmed", jsonObject.getString("confirmed"));
        linkedHashMap.put("fPort", jsonObject.getString("fPort"));

        Base64.Encoder encoder = Base64.getEncoder();
        String[] datas = jsonObject.getString(messageKey).split(" ");
        byte[] databytes = new byte[5];
        int leek = 0;
        for (String leekString : datas) {
            databytes[leek] = (byte) Integer.parseInt(leekString, 16);
            leek++;
        }

        linkedHashMap.put("data", encoder.encodeToString(databytes));

        Base64.Decoder decoder = Base64.getDecoder();
        MQTT mqtt = new MQTT();
        mqttConfig.configure(mqtt);
        String newClientID = String.valueOf(Integer.parseInt(String.valueOf(mqtt.getClientId())) + clientIdOffset);
        mqtt.setClientId(newClientID);
        clientIdOffset++;
        if (clientIdOffset >= maxOffset) {
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
        return decoder.decode(ackJsonObject.getString("data"));
    }

    /**
     * 继电器八路状态读取
     */
    public Object getRelayStatus() throws Exception {
        /*
         * 向随意传感器发送功能码为02的指令即可获取八路继电器状态
         * 指令数据区数据区无意义
         * */
        LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("Deveui", "004a770066003289");
        linkedHashMap.put("reference", "abcd1234");
        linkedHashMap.put("confirmed", "true");
        linkedHashMap.put("fPort", "10");
        linkedHashMap.put("message", "02 00 00 D0 00");
        JSONObject jsonObject = new JSONObject();
        jsonObject.putAll(linkedHashMap);

        byte[] decodedAckData = getPublishAck(jsonObject);

        String switchString = Integer.toBinaryString((decodedAckData[3] & 0xFF) + 0x100).substring(1);
        char[] chars = switchString.toCharArray();

        return TResult.success(switchString);
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
