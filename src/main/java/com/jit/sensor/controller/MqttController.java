package com.jit.sensor.controller;

import com.alibaba.fastjson.JSONObject;
import com.jit.sensor.entity.TResult;
import com.jit.sensor.entity.TResultCode;
import com.jit.sensor.service.MqttConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author chy
 */
@RestController
@RequestMapping("/mqtt")
public class MqttController {
    @Autowired
    private MqttConfigService mqttService;

    /**
     * subscription:频道订阅
     */
    @PostMapping("/subscription")
    public Object addSubscription(@RequestBody JSONObject jsonObject) {
        return mqttService.addSubscription(jsonObject);
    }

    @GetMapping("/subscription")
    public Object getSubscribtion() {
        return mqttService.getSubscription();
    }

    @DeleteMapping("/subscription")
    public Object deleteSubscribtion(@RequestBody JSONObject jsonObject) {
        return mqttService.deleteSubscription(jsonObject);
    }

    /**
     * 设置传感器上传周期
     */
    @PostMapping("/uploadcycle")
    public Object updateSensorCycle(@RequestBody JSONObject jsonObject) {
        try {
            return mqttService.updateSensorCycle(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            return TResult.failure(TResultCode.PUBLISH_EXCEPTION);
        }
    }

    /**
     * 获取继电器八路状态
     */
    @GetMapping("/relay")
    public Object getSensorStatus() {
        try {
            return mqttService.getRelayStatus();
        } catch (Exception e) {
            e.printStackTrace();
            return TResult.failure(TResultCode.PUBLISH_EXCEPTION);
        }
    }

    /**
     * 设置继电器状态
     */
    @PostMapping("/relay")
    public Object setSensorStatus(@RequestBody JSONObject jsonObject) {
        try {
            return mqttService.setRelayStatus(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            return TResult.failure(TResultCode.PUBLISH_EXCEPTION);
        }
    }
}