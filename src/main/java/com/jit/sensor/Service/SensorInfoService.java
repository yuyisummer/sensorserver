package com.jit.sensor.Service;

import com.jit.sensor.Entity.Sensorinfo;
import com.jit.sensor.Mapper.SensorinfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SensorInfoService {
    @Autowired
    private SensorinfoMapper sensorinfoMapper;

    public boolean insert(Sensorinfo sensorinfo) {
        return sensorinfoMapper.insert(sensorinfo) > 0;
    }

    public Sensorinfo selectByNeedData(String deveui, String devtype) {
        return sensorinfoMapper.selectByNeedData(deveui, devtype);
    }
}
