package com.jit.sensor.api;


import com.alibaba.fastjson.JSONObject;
import com.jit.sensor.service.MqttConfig;
import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.LinkedHashMap;


@RestController
@RequestMapping("redis")
public class RedisController {

    @PostMapping("/testDownData")
    public Object test(@RequestBody JSONObject jsonObject) throws Exception {
        String sendTopic = "application/2/device/004a770066003289/tx";
//        MqttSet mqttSet;
//        mqttSet = MqttClient.getMqttSet();
//        mqttSet.builder();
        MQTT mqtt = new MQTT();
        MqttConfig mqttConfig = new MqttConfig();
        mqttConfig.configure(mqtt);
        BlockingConnection connection = mqtt.blockingConnection();
        connection.connect();

        Base64.Encoder encoder = Base64.getEncoder();
        LinkedHashMap<Object, Object> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("reference", "abcd1234");
        linkedHashMap.put("confirmed", true);
        linkedHashMap.put("fPort", 10);
        linkedHashMap.put("data", new String(encoder.encode(jsonObject.getString("message").getBytes())));

        connection.publish(sendTopic, JSONObject.toJSON(linkedHashMap).toString().getBytes(),
                QoS.AT_LEAST_ONCE, true);
        System.out.println("===========消息发布成功============");
        connection.disconnect();
        return "test";
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
