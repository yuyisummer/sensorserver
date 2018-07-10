package com.jit.sensor.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jit.sensor.base.MqttSet;
import com.jit.sensor.model.Sensordata;
import com.jit.sensor.model.Universaldata;
import com.jit.sensor.service.SensordataService;
import com.jit.sensor.service.UniversalDataService;
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.hawtdispatch.Dispatch;
import org.fusesource.mqtt.client.*;
import org.fusesource.mqtt.codec.MQTTFrame;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.LinkedHashMap;


//@RestController
//@RequestMapping("/MqttStart")

public class MqttClient {
    //  public static void main(String[] args) {

    private static ApplicationContext applicationContext;//启动类set入，调用下面set方法
    //  @Autowired
    MqttSet mqttSet;

    // String recvTopic = "application/2/device/004a7700660033d9/rx";
    String recvTopic = "application/2/device/004a770066003289/rx";
    String sendTopic = "application/2/device/004a770066003289/tx";
    SensordataService sensordataService;
    UniversalDataService universalDataService;

    public MqttClient(ApplicationContext context) {
        applicationContext = context;
        sensordataService = getSensordataService();
        mqttSet = getMqttSet();
        universalDataService = getUniversalDataService();
    }

    public static void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }

    public static boolean publishData(String string) {
        return false;
    }

    public static MqttSet getMqttSet() {
        return (MqttSet) applicationContext.getBean(MqttSet.class);
    }

    public void init() {
        try {
            mqttSet.builder();
            MQTT mqtt = mqttSet.getMqtt();
            // 选择消息分发队列
            mqtt.setDispatchQueue(Dispatch.createQueue("fol"));
            // 若没有调用方法setDispatchQueue，客户端将为连接新建一个队列。如果想实现多个连接使用公用的队列，显式地指定队列是一个非常方便的实现方法

            // 设置跟踪器
            mqtt.setTracer(new Tracer() {
                @Override
                public void onReceive(MQTTFrame frame) {
//                    System.out.println("recv: " + frame);
                }

                @Override
                public void onSend(MQTTFrame frame) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                    System.out.println("send: " + frame);
                }

                @Override
                public void debug(String message, Object... args) {
//                    System.out.println(String.format("debug: " + message, args));
                }
            });

            // 使用回调式API
            final CallbackConnection callbackConnection = mqtt
                    .callbackConnection();
            // 连接监听
            callbackConnection.listener(new Listener() {

                // 接收订阅话题发布的消息
                @Override
                public void onPublish(UTF8Buffer topic, Buffer payload,
                                      Runnable onComplete) {
//                    System.out
//                            .println("=============receive msg================"
//                                    + new String(payload.toByteArray()));

                    String bugString = "chy";
                    if (!bugString.equals(new String(payload.toByteArray()))) {
                        //获取数据
                        JSONObject jsonObject = JSON.parseObject(new String(payload.toByteArray()));
                        System.out.println(jsonObject);
                        Sensordata sensordata = new Sensordata();

                        String data1 = null;
                        data1 = jsonObject.getString("data");
                        Decoder decoder = Base64.getDecoder();
                        if (data1 != null) {
                            Universaldata universaldata = new Universaldata();
                            universaldata.setDeveui(jsonObject.getString("devEUI"));
                            byte[] decode = decoder.decode(data1);
                            System.out.println("decode:" + decode[0]);
                            universaldata.setDevtype(String.valueOf(decode[0]));
                            universaldata.setData(data1);
                            universaldata.setTime(String.valueOf(System.currentTimeMillis()));

                            if (universalDataService.insertdata(universaldata)) {
                                System.out.println("传感器数据插入成功");
                            } else {
                                System.out.println("传感器数据插入失败");
                            }
                        }
                    }

                    onComplete.run();
                }

                // 连接失败
                @Override
                public void onFailure(Throwable value) {
                    System.out.println("===========connect failure===========");
                    callbackConnection.disconnect(null);
                }

                // 连接断开
                @Override
                public void onDisconnected() {
                    System.out.println("====mqtt disconnected=====");

                }

                // 连接成功
                @Override
                public void onConnected() {
                    System.out.println("====mqtt connected=====");

                }
            });

            // 连接
            callbackConnection.connect(new Callback<Void>() {

                // 连接失败
                @Override
                public void onFailure(Throwable value) {
                    System.out.println("============连接失败："
                            + value.getLocalizedMessage() + "============");
                }

                // 连接成功
                @Override
                public void onSuccess(Void v) {
                    // 订阅主题

                    Topic[] recvTopics = {new Topic(recvTopic, QoS.AT_LEAST_ONCE)};
                    System.out.println("length:" + recvTopics.length);
                    callbackConnection.subscribe(recvTopics,
                            new Callback<byte[]>() {
                                // 订阅主题成功
                                @Override
                                public void onSuccess(byte[] qoses) {
                                    System.out.println("========订阅成功=======");
                                }

                                // 订阅主题失败
                                @Override
                                public void onFailure(Throwable value) {
                                    System.out.println("========订阅失败=======");
                                    callbackConnection.disconnect(null);
                                }
                            });


                    // 发布消息
//                    Base64.Encoder encoder = Base64.getEncoder();
//                    LinkedHashMap<Object, Object> linkedHashMap = new LinkedHashMap<>();
//                    linkedHashMap.put("reference", "abcd1234");
//                    linkedHashMap.put("confirmed", true);
//                    linkedHashMap.put("fPort", 10);
//                    linkedHashMap.put("data", new String(encoder.encode("send by chy".getBytes())));
//                    callbackConnection.publish(sendTopic, JSONObject.toJSON(linkedHashMap).toString().getBytes(),
//                            QoS.AT_LEAST_ONCE, true, new Callback<Void>() {
//                                @Override
//                                public void onSuccess(Void v) {
//                                    System.out
//                                            .println("===========消息发布成功============");
//                                }
//
//                                @Override
//                                public void onFailure(Throwable value) {
//                                    System.out.println("========消息发布失败=======");
//                                    System.out.println("value\t" + value);
//                                    callbackConnection.disconnect(this);
//                                }
//                            });
                }
            });
//            while (true) {
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public SensordataService getSensordataService() {
        return applicationContext.getBean(SensordataService.class);
    }

    public UniversalDataService getUniversalDataService() {
        return applicationContext.getBean(UniversalDataService.class);
    }
}
