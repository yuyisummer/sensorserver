package com.jit.sensor.controller;


import com.alibaba.fastjson.JSONObject;
import com.jit.sensor.service.MqttConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author chy
 */
@RestController
@RequestMapping("/MQTT")
public class MqttController {
    @Autowired
    private MqttConfigService mqttConfigService;

    @PostMapping("/subscription")
    public Object addSubscription(@RequestBody JSONObject jsonObject) {
        return mqttConfigService.addSubscription(jsonObject);
    }

    @GetMapping("/subscription")
    public Object getSubscribtion() {
        return mqttConfigService.getSubscription();
    }

    @DeleteMapping("/subscription")
    public Object deleteSubscribtion(@RequestBody JSONObject jsonObject) {
        return mqttConfigService.deleteSubscription(jsonObject);
    }

    @PostMapping("/publishment")
    public Object addPublishment(@RequestBody JSONObject jsonObject) throws Exception {
        return mqttConfigService.downloadData(jsonObject);
    }
}
