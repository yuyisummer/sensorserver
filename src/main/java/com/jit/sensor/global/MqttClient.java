package com.jit.sensor.global;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jit.sensor.entity.MqttSet;
import com.jit.sensor.entity.Universaldata;
import com.jit.sensor.global.socket.MyWebsocket;
import com.jit.sensor.service.SensordataService;
import com.jit.sensor.service.UniversalDataService;
import com.jit.sensor.util.AnalysisNeedData;
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.hawtdispatch.Dispatch;
import org.fusesource.mqtt.client.*;
import org.fusesource.mqtt.codec.MQTTFrame;
import org.springframework.context.ApplicationContext;

import java.util.Base64;
import java.util.Base64.Decoder;


//@RestController
//@RequestMapping("/MqttStart")

public class MqttClient {
    //  public static void main(String[] args) {

    public static MQTT mqtt;
    public static CallbackConnection callbackConnection;
    public static Topic[] topics;
    private static ApplicationContext applicationContext;
    /**
     * 启动类set入，调用下面set方法
     */
    SensordataService sensordataService;
    UniversalDataService universalDataService;
    private MqttSet mqttSet;
    private Boolean isConnected;

    public MqttClient(ApplicationContext context) {
        applicationContext = context;
        sensordataService = getSensordataService();
        mqttSet = getMqttSet();
        universalDataService = getUniversalDataService();
    }

    public static MqttSet getMqttSet() {
        return applicationContext.getBean(MqttSet.class);
    }

    public CallbackConnection init() throws Exception {
        mqttSet.builder();
        mqtt = mqttSet.getMqtt();
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
                System.out.println("debug: " + message);
                for (Object arg : args) {
                    System.out.println(arg);
                }
            }
        });
//           使用回调式API
        callbackConnection = mqtt
                .callbackConnection();
        // 连接监听器
        callbackConnection.listener(new Listener() {
            // 接收订阅话题发布的消息
            @Override
            public void onPublish(UTF8Buffer topic, Buffer payload,
                                  Runnable onComplete) {

                /*
                 * TODO :上行信息控制处理
                 * */
//                String bugString = "chy";
                System.out.println("recv from " + topic);
                System.out.println("payload " + new String(payload.toByteArray()));
//                if (!bugString.equals(new String(payload.toByteArray()))) {
                //获取数据
                JSONObject jsonObject = JSON.parseObject(new String(payload.toByteArray()));
                System.out.println("jsonobject\t" + jsonObject);

                String data1;
                data1 = jsonObject.getString("data");
                Decoder decoder = Base64.getDecoder();
                if (data1 != null) {
                    Universaldata universaldata = new Universaldata();
                    universaldata.setDeveui(jsonObject.getString("devEUI"));
                    byte[] decode = decoder.decode(data1);
                    System.out.println("decode\t" + decode[0]);
                    universaldata.setDevtype(String.valueOf(decode[0]));
                    universaldata.setData(data1);
                    universaldata.setTime(String.valueOf(System.currentTimeMillis()));

                    System.out.println("解析完的数据" + AnalysisNeedData.getWebSocketData(universaldata).toString());
                    MyWebsocket.sendMessage(AnalysisNeedData.getWebSocketData(universaldata).toString());
                    if (universalDataService.insertdata(universaldata)) {
                        System.out.println("传感器数据插入成功");
                    } else {
                        System.out.println("传感器数据插入失败");
                    }
                }
//                }
                onComplete.run();
            }

            // 连接失败
            @Override
            public void onFailure(Throwable value) {
                System.out.println("===========connect failure===========\t" + value.getCause());
                callbackConnection.disconnect(null);
                isConnected = false;
                for (int num = 0; (num < 3) && !isConnected; num++) {
                    callbackConnection.connect(new Callback<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            System.out.println("重连成功");
                            isConnected = true;
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            System.out.println("重连失败");
                        }
                    });
                }
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

//        建立连接
        callbackConnection.connect(new Callback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                System.out.println("MQTT Connect success.");
            }

            @Override
            public void onFailure(Throwable throwable) {
                System.out.println("MQTT Connect failed.");
            }
        });

        return callbackConnection;
    }

    public SensordataService getSensordataService() {
        return applicationContext.getBean(SensordataService.class);
    }

    public UniversalDataService getUniversalDataService() {
        return applicationContext.getBean(UniversalDataService.class);
    }
}
