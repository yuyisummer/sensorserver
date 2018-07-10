package com.jit.sensor.Controller;


import com.alibaba.fastjson.JSONObject;
import com.jit.sensor.Service.TransferDataService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("test")
public class TestController {
    TransferDataService transferDataService = new TransferDataService();


    @PostMapping("/UploadData")
    public Object uploadData(@RequestBody JSONObject jsonObject) {

        return null;
    }

//    @PostMapping("/DownloadData")
//    public Object downloadData(@RequestBody JSONObject jsonObject) throws Exception {
//        final Boolean[] flag = new Boolean[1];
//        String sendTopic = "application/" + jsonObject.getString("Application") +
//                "/device/" + jsonObject.getString("Deveui") +
//                "/" + jsonObject.getString("Channel");
//
//        Base64.Encoder encoder = Base64.getEncoder();
//        LinkedHashMap<Object, Object> linkedHashMap = new LinkedHashMap<>();
//        linkedHashMap.put("reference", "abcd1234");
//        linkedHashMap.put("confirmed", true);
//        linkedHashMap.put("fPort", 10);
//        linkedHashMap.put("data", new String(encoder.encode("send by chy".getBytes())));
//
//        MqttSet mqttSet = AnalysisNeedData.getBean(MqttSet.class);
//        mqttSet.builder();
//        MQTT mqtt = mqttSet.getMqtt();
//        final CallbackConnection callbackConnection = mqtt.callbackConnection();
//
//        callbackConnection.connect(new Callback<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//
//                callbackConnection.publish(sendTopic, JSONObject.toJSON(linkedHashMap).toString().getBytes(), QoS.AT_LEAST_ONCE, true,
//                        new Callback<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                System.out.println("发送成功");
//                                flag[0] = true;
//                            }
//
//                            @Override
//                            public void onFailure(Throwable throwable) {
//                                System.out.println("发送失败");
//                            }
//                        });
//                callbackConnection.disconnect(null);
//            }
//
//            @Override
//            public void onFailure(Throwable throwable) {
//                System.out.println("连接失败");
//            }
//        });
//        return flag;
////        return transferDataService.downloadDataService(jsonObject);
//    }

    @PostMapping("/DownloadData")
    public Object downloadData1(@RequestBody JSONObject jsonObject) throws Exception {
        return transferDataService.downloadDataService(jsonObject);
//            String sendTopic = "application/" + jsonObject.getString("Application") +
//                "/device/" + jsonObject.getString("Deveui") +
//                "/" + jsonObject.getString("Channel");
//        Topic[] sendTopics = {new Topic(sendTopic, QoS.AT_LEAST_ONCE)};
//
//
//        Base64.Encoder encoder = Base64.getEncoder();
//        LinkedHashMap<Object, Object> linkedHashMap = new LinkedHashMap<>();
//        linkedHashMap.put("reference", "abcd1234");
//        linkedHashMap.put("confirmed", true);
//        linkedHashMap.put("fPort", 10);
//        linkedHashMap.put("data", new String(encoder.encode("send by chy".getBytes())));
//
//        MQTT mqtt = new MQTT();
//        mqttConfigService.configure(mqtt);
//        BlockingConnection connection = mqtt.blockingConnection();
//        connection.connect();
//        connection.publish(sendTopic, JSONObject.toJSON(linkedHashMap).toString().getBytes(), QoS.AT_LEAST_ONCE, true);
//
//        connection.subscribe(sendTopics);
//        Message message = connection.receive();
//        connection.disconnect();
//
//        return message!=null;
    }

//	@Autowired
//	private StringRedisTemplate strRedis;
//	@Autowired
//	private RedisKeyValueTemplate rkvt;
//
//
//	@RequestMapping("/test1")
//	public String test1() {
//		ReturnStr returnStr = new ReturnStr();
//		strRedis.opsForValue().set("cache2", "2redis !!!");
//		return returnStr.setTMessage(1,"成功",strRedis.opsForValue().get("cache"));
//		//return ResultUtil.success(strRedis.opsForValue().get("cache"));
//	}
//
//	@RequestMapping("/test2")
//	public String test2() {
//		ReturnStr returnStr = new ReturnStr();
//		Universaldata universaldata = new Universaldata();
//		universaldata.setTime(String.valueOf(new Date()));
//		universaldata.setDevtype("2");
//		universaldata.setDeveui("12345");
//		universaldata.setData("haidongdong");
//		System.out.println(universaldata.toString());
//		String key = universaldata.getDeveui()+"-"+universaldata.getDevtype();
//		System.out.println(key);
//		//rkvt.insert(key,"12345");
//		User user = new User();
//		user.setCeshi("123");
//		user.setHaidongdong("789");
//		System.out.println(user.toString());
//		rkvt.insert(1,user);
//		return returnStr.setTMessage(1,"成功", JSONObject.toJSONString(rkvt.findById(key, Universaldata.class)));
//	}
//


}
