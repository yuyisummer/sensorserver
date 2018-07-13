package com.jit.sensor;

import com.alibaba.fastjson.JSONObject;
import com.jit.sensor.api.MqttClient;
import com.jit.sensor.api.ceshiMQTT;

import com.jit.sensor.base.utils.AnalysisNeedData;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;


@MapperScan("com.jit.sensor.mapper")
@SpringBootApplication
@EnableScheduling
@EnableSwagger2
public class MainApplication {
    public static void main(String args[]) {
        //  SpringApplication.run(MainApplication.class, args);
        ConfigurableApplicationContext applicationContext = SpringApplication.run(MainApplication.class, args);

        new AnalysisNeedData().setApplicationContext(applicationContext);
        MqttClient mqttClient = new MqttClient(applicationContext);
        //   ceshiMQTT ceshi =   new ceshiMQTT();
        mqttClient.init();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                ceshi cesh = AnalysisNeedData.getBean(ceshi.class);
//                cesh.reportTime();
//
//            }
//        }).start();




//        Map<String, Map<String, Double>> list = new HashMap<>();
//       Map<String,Double> ceshi1 = new HashMap<>();
//       Map<String,Double> ceshi2 = new HashMap<>();
//       ceshi1.put("%c",0.1);
//       ceshi2.put("%",0.01);
//       list.put("temp",ceshi1);
//       list.put("temp2",ceshi2);
//
//
//
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("cehsi","111");
//        jsonObject.put("value",list);
//        System.out.println("list.json: " + jsonObject.toJSONString());
    }
}
