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
        return mqttConfigService.addSubscription(jsonObject);
    }

    /**
     * TODO:获得频道
     */
    @GetMapping("/subscription")
    public Object getSubscribtion(@RequestParam("Topic") String topic) {
        return null;
    }

    /**
     * TODO：更新频道信息
     */
    @PutMapping("/subscription")
    public Object updateSubcribtion(@RequestBody JSONObject jsonObject) {
        return null;
    }

    /**
     * TODO：取消订阅频道
     */
    @DeleteMapping("/subscription")
    public Object deleteSubscribtion(@RequestBody JSONObject jsonObject) {
        return null;
    }

    @PostMapping("/publishment")
    public Object addPublishment(@RequestBody JSONObject jsonObject) throws Exception {
        return mqttConfigService.downloadData(jsonObject);
    }
}
