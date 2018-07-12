package com.jit.sensor.service;

import com.jit.sensor.entity.Sensorinfo;
import com.jit.sensor.mapper.SensorinfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SensorInfoService {
    @Autowired
    SensorinfoMapper sensorinfoMapper;

    public boolean insert(Sensorinfo sensorinfo) {
        return sensorinfoMapper.insert(sensorinfo) > 0;
    }

    public Sensorinfo selectByNeedData(String deveui, String devtype) {
        return sensorinfoMapper.selectByNeedData(deveui, devtype);
    }
}