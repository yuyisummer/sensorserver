package com.jit.sensor.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.jit.sensor.base.utils.*;
import com.jit.sensor.model.Sensorinfo;
import com.jit.sensor.model.TMessage;
import com.jit.sensor.model.Universaldata;
import com.jit.sensor.service.SensorInfoService;
import com.jit.sensor.service.UniversalDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/SensorInfo")
@Api(value = "/SensorInfo", tags = {"数据查询接口"})
public class SensorInfo {
    @Autowired
    SensorInfoService sensorInfoService;
    @Autowired
    UniversalDataService universalDataService;

    @ApiOperation(value = "查询数据库中最新数据", notes = "通过参数查询对应传感器上传的最近的一条数据")
    @PostMapping(value = "SelectInfo",consumes = "application/json")
    public TMessage SelectInfo(@RequestBody Sensorinfo sensorinfo ){
    //public TMessage SelectInfo(String deveui, String devtype) {
        System.out.println("Sensorinfo:"+sensorinfo.getDeveui());
        System.out.println("Sensorinfo:"+sensorinfo.getDevtype());
        String mainkey = sensorinfo.getDeveui()+"-"+sensorinfo.getDevtype()+"-";
        Map<String, String> map = null;
        String deveui = sensorinfo.getDeveui();
        String devtype = sensorinfo.getDevtype();
        map = FindSensorData.GetSensorInfoMap(deveui, devtype);
        Universaldata universaldata = null;
        universaldata = universalDataService.SelectLastData(deveui, devtype);
        System.out.println("最近一条数据的时间："+universaldata.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sd = sdf.format(new Date(Long.parseLong(universaldata.getTime()))); // 时间戳转换日期
        System.out.println(sd);
        JSONObject retjson = new JSONObject();

       Map<String,Double> j = JSON.parseObject( FindSensorData.getAnalysisData(map, universaldata).toJSONString()
       ,new TypeReference<Map<String,Double>>(){});

       Map<String,String> unit = new HashMap<>();

       for(Map.Entry<String,Double> entry:j.entrySet()){
            String key = mainkey+entry.getKey();
            Map<String,Double> m = FindSensorData.getCfKey(key);
           System.out.println("entry.getkey:"+entry.getKey());
           System.out.println("entry.getvalue:"+entry.getValue());
            for(Map.Entry<String,Double> entry1:m.entrySet()){
                System.out.println("entry1.getkey:"+entry1.getKey());
                System.out.println("entry1.getvalue:"+entry1.getValue());
               j.put(entry.getKey(),entry.getValue()*entry1.getValue());
               unit.put(entry.getKey(),entry1.getKey());
            }
       }

        Map<String,String> finalmap = new HashMap<>();
        for(Map.Entry<String,Double> entry:j.entrySet()){
            finalmap.put(entry.getKey(), String.valueOf(entry.getValue()));
        }

       // retjson.put("data", FindSensorData.getAnalysisData(map, universaldata));
       // retjson.put("data", j);
        retjson.put("data", finalmap);
        retjson.put("time", sd);
        retjson.put("unit",unit);
        return ReturnUtil.finalObject(1, "获取最近一条数据", retjson);

    }
}
