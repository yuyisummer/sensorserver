package com.jit.sensor.base.utils;

import com.jit.sensor.model.Sensorinfo;
import com.jit.sensor.service.SensorInfoService;

public class FindSensorInfo {
    private  static Sensorinfo sensorinfo;
    public static  Sensorinfo find(String deveui,String devtype){
        SensorInfoService sensorInfoService =  AnalysisNeedData.getBean(SensorInfoService.class);
        System.out.println("ceshi"+sensorInfoService);
        return  sensorInfoService.selectByNeedData(deveui,devtype);
    }
}
