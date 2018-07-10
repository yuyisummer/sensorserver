package com.jit.sensor.Controller;

import com.alibaba.fastjson.JSONObject;
import com.jit.sensor.Util.AnalysisNeedData;
import com.jit.sensor.Util.FindSensorInfo;
import com.jit.sensor.Util.ReturnStr;
import com.jit.sensor.Util.ReturnUtil;
import com.jit.sensor.Entity.Sensorinfo;
import com.jit.sensor.Entity.TMessage;
import com.jit.sensor.Entity.Universaldata;
import com.jit.sensor.Service.SensorInfoService;
import com.jit.sensor.Service.UniversalDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Date;
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
    @PostMapping("SelectInfo")
    public TMessage SelectInfo(String deveui, String devtype) {

        ReturnStr returnStr = new ReturnStr();

        Map<String, String> map = null;
        String key = deveui + "-" + devtype;
        map = AnalysisNeedData.getNeedData(key);
        if (map == null) {
            System.out.println("map中没有对应解析");
            System.out.println("deveui:" + deveui + " devtype:" + devtype);
            //  Sensorinfo sensorinfo = sensorInfoService.selectByNeedData(deveui, devtype);
            Sensorinfo sensorinfo = FindSensorInfo.find(deveui, devtype);
            String data = null;
            try {
                data = sensorinfo.getDatalength();
                System.out.println("data:" + data);
            } catch (Exception e) {
                //  return returnStr.setTMessage(1, "数据库中没有对应信息", null);
            }

            map = AnalysisNeedData.toMap(data);

            for (Map.Entry<String, String> entry : map.entrySet()) {
                System.out.println("key:" + entry.getKey() + " value:" + entry.getValue());
            }


            AnalysisNeedData.SetNeedData(key, map);
        }
        Universaldata universaldata = null;

        universaldata = universalDataService.SelectLastData(deveui, devtype);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sd = sdf.format(new Date(Long.parseLong(universaldata.getTime()))); // 时间戳转换日期
        System.out.println(sd);
        Decoder decoder = Base64.getDecoder();

        JSONObject jsonObject = new JSONObject();
        byte[] decode = decoder.decode(universaldata.getData());
        char[] list = AnalysisNeedData.bytesToHexString(decode);
        System.out.println("list.length:" + list.length);
        int seek = 6;
        for (int i = 0; i < decode[2]; ) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                System.out.println("接下来要解析的数据：" + entry.getKey());
                if (entry.getKey().equals("all")) {
                    continue;
                } else if (entry.getKey().equals("Remove")) {
                    seek += Integer.valueOf(entry.getValue()) * 2;
                    i += Integer.valueOf(entry.getValue());
                    continue;
                }
                String parameter = entry.getKey();

                int zijie = Integer.valueOf(entry.getValue());
                System.out.println("字节：" + zijie);
                int ncifang = zijie * 2;
                String str = null;
                int num = 0;
                for (int j = 0; j < zijie * 2; j++) {
                    int zhi = 0;
                    if (list[seek + j] >= '0' && list[seek + j] <= '9') {
                        zhi = list[seek + j] - '0';
                    } else {
                        zhi = list[seek + j] - 'a' + 10;
                    }
                    System.out.println("list[seek+j]" + list[seek + j] + " 位置:" + (seek + j) + " zhi:" + zhi);
                    num += zhi * Math.pow(16, ncifang - 1);
                    ncifang--;
                }
                System.out.println("num:" + num);
                seek += zijie * 2;
                i += zijie;
                jsonObject.put(entry.getKey(), num);
            }
        }

        JSONObject retjson = new JSONObject();
        retjson.put("data", jsonObject);
        retjson.put("time", sd);


        return ReturnUtil.finalObject(1, "获取最近一条数据", retjson);

    }
}
