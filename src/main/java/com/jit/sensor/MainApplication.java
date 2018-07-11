package com.jit.sensor;

import com.jit.sensor.Global.MqttClient;
import com.jit.sensor.Util.AnalysisNeedData;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@MapperScan("com.jit.sensor.Mapper")
@SpringBootApplication
@EnableScheduling
@EnableSwagger2
public class MainApplication {
    public static MqttClient mqttClient;

    public static void main(String args[]) throws Exception {
      //  SpringApplication.run(MainApplication.class, args);
        ConfigurableApplicationContext applicationContext = SpringApplication.run(MainApplication.class, args);
        new AnalysisNeedData().setApplicationContext(applicationContext);
        mqttClient = new MqttClient(applicationContext);
        mqttClient.init();
    }
}
