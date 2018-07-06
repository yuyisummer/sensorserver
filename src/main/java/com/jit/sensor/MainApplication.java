package com.jit.sensor;

import com.jit.sensor.api.MqttClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;


@MapperScan("com.jit.sensor.mapper")
@SpringBootApplication
@EnableScheduling
public class MainApplication {
    public static void main(String args[]) {
      //  SpringApplication.run(MainApplication.class, args);
        ConfigurableApplicationContext applicationContext = SpringApplication.run(MainApplication.class, args);
        MqttClient mqttClient = new MqttClient(applicationContext);
       // mqttClient.setApplicationContext(applicationContext);
        mqttClient.init();
    }
}
