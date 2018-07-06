package com.jit.sensor.service;

import com.jit.sensor.mapper.SensordataMapper;
import com.jit.sensor.model.Sensordata;
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
