package com.jit.sensor.Service;

import com.jit.sensor.Mapper.SensordataMapper;
import com.jit.sensor.Entity.Sensordata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SensordataService {

    @Autowired
    SensordataMapper sensordataMapper;


    public boolean InsertData(Sensordata sensordata){
       return sensordataMapper.insert(sensordata)>0;
    }



}
