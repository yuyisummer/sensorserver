package com.jit.sensor.Util;

import com.jit.sensor.Entity.Sensorinfo;
import com.jit.sensor.Service.SensorInfoService;

public class FindSensorInfo {
    private  static Sensorinfo sensorinfo;
    public static  Sensorinfo find(String deveui,String devtype){
        SensorInfoService sensorInfoService =  AnalysisNeedData.getBean(SensorInfoService.class);
        System.out.println("ceshi"+sensorInfoService);
        return  sensorInfoService.selectByNeedData(deveui,devtype);
    }
}
