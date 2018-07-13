package com.jit.sensor.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jit.sensor.model.Sensorinfo;
import com.jit.sensor.model.TMessage;
import com.jit.sensor.service.SensorInfoService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/sensor")
@Api(value = "/sensor",tags = {"配置操作接口"})
public class SensorProfile {
    TMessage tMessage;
    @Autowired
    SensorInfoService sensorInfoService;



    @ApiOperation(value="插入传感器配置文件", notes="通过解析JSONObject转为对象插入数据库中")
    @PostMapping(value = "insertprofile")
 //   @ApiParam(value ="json", required = true,example ="123")
//    public String InsertProfile(@RequestBody  JSONObject json){

    public String InsertProfile(@RequestBody Sensorinfo sensorinfo){

     //   Sensorinfo sensorinfo = null;
     //   System.out.println("json:"+json);
        try {

         //   sensorinfo = JSONObject.parseObject(json.toJSONString(), Sensorinfo.class);
           // sensorinfo.setDatalength(String.valueOf(json.getJSONObject("datalength")));

            System.out.println("datalength:"+sensorinfo.getDatalength());
        } catch (Exception e) {
            return setTMessage(0, "对象解析失败", null);
        }

        if (sensorInfoService.insert(sensorinfo)) {
            return setTMessage(1, "传感器配置文件插入成功", new JSONObject());
        } else {
            return setTMessage(0, "传感器配置文件插入失败", new JSONObject());
        }
    }



    public String setTMessage(int i, String str, JSONObject json) {
        tMessage = new TMessage();
        tMessage.Code = i;
        tMessage.Message = str;
        tMessage.Time = String.valueOf(new Date());
        tMessage.Data = json;
        return JSON.toJSONString(tMessage);
    }


}
