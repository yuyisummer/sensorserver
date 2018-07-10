package com.jit.sensor.Controller;


import com.alibaba.fastjson.JSONObject;
import com.jit.sensor.Service.MqttConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author chy
 */
@RestController
@RequestMapping("/MQTT")
public class MQTTConfigController {
    @Autowired
    private MqttConfigService mqttConfigService;

    @PostMapping("/subscription")
    public Object addSubscription(@RequestBody JSONObject jsonObject) {
        return mqttConfigService.uploadData(jsonObject);
    }

    @GetMapping("/subscribtion")
    public Object updateSubscribtion(@RequestParam("Topic") String topic) {
        return null;
    }

    @PutMapping("/subscribtion")
    public Object updateSubcribtion(@RequestBody JSONObject jsonObject) {
        return null;
    }

    @DeleteMapping("/subscribtion")
    public Object deleteSubscribtion(@RequestBody JSONObject jsonObject) {
        return null;
    }

    @PostMapping("/publishment")
    public Object addPublishment(@RequestBody JSONObject jsonObject) throws Exception {
        return mqttConfigService.downloadData(jsonObject);
    }
}
