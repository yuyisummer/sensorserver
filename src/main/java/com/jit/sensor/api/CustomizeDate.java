package com.jit.sensor.api;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.jit.sensor.base.utils.AnalysisNeedData;
import com.jit.sensor.base.utils.FindSensorData;
import com.jit.sensor.base.utils.ReturnUtil;
import com.jit.sensor.base.utils.ThisTime;
import com.jit.sensor.model.AverageInfo;
import com.jit.sensor.model.Customize;
import com.jit.sensor.model.TMessage;
import com.jit.sensor.model.Universaldata;
import com.jit.sensor.service.UniversalDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.NumberFormat;
import java.util.*;

@RestController
@RequestMapping("/CustomizeDate")
public class CustomizeDate {
    long nowtime;
    long lasttime;
    long k = 60000;
    NumberFormat nf= NumberFormat.getInstance();  //创建格式化类nf

    @Autowired
    UniversalDataService unservice;

    @PostMapping("/getInfo")

    public TMessage GetCustomizeInfo(@RequestBody Customize c) {
        nf.setMaximumFractionDigits(2);    //数值2表示保留2位小数
        LinkedList<String> datelist = new LinkedList<>();
        String deveui = c.getDeveui();
        String devtype = c.getDevtype();
        List<String> keys = c.getDatetype();
        String mainkey = deveui + "-" + devtype + "-";
        Map<String, String> map =
                FindSensorData.GetSensorInfoMap(deveui, devtype);
        Map<String, LinkedList<Double>> finalmap = new HashMap<>();
        if (c.getType() == 1) {
            k = 60000;
            //  nowtime =  ThisTime.lastonehour();
            nowtime = Long.valueOf("1530970380000");
            System.out.println(nowtime);
            lasttime = nowtime - 3600000;
            System.out.println(lasttime);
            k = k * c.getJiange();


            for (; lasttime <= nowtime; lasttime += k) {

                //  lasttime<=   <lasttime+k
                datelist.add(ThisTime.zhuanhour(lasttime));
                List<Universaldata> list = unservice.selectCusomizeData(
                        String.valueOf(lasttime + k), String.valueOf(lasttime), deveui, devtype);
                System.out.println("list.ceshi:" + list.size());

                //    Map<String,Double> averagemap = new HashMap<>();
                Map<String, Double> mapzj = new HashMap<>();

                for (int i = 0; i < list.size(); i++) {
                    JSONObject jsonObject = FindSensorData.getAnalysisData(map, list.get(i));
                    System.out.println("返回的JSONobject：" + jsonObject);

                    for (int j = 0; j < keys.size(); j++) {
                        String key = keys.get(j);
                        Double d = jsonObject.getDouble(key);
                        if (mapzj.get(key) != null) {
                            mapzj.put(key, mapzj.get(key) + d);
                        } else {
                            mapzj.put(key, d);
                        }
                    }
                }
                for (Map.Entry<String, Double> entry : mapzj.entrySet()) {
                    LinkedList<Double> valuelink = finalmap.get(entry.getKey());
                    if (valuelink == null) {
                        valuelink = new LinkedList<>();
                    }
                    System.out.println(entry.getValue());
                    System.out.println(list.size());
                    try {

                        valuelink.add(findCf(mainkey + entry.getKey(), (entry.getValue() / list.size())));
                    } catch (Exception e) {
                        valuelink.add(0.0);
                    }

                    finalmap.put(entry.getKey(), valuelink);
                }
            }

           Map<String,String> unit = new HashMap<>();
            for(int i = 0;i<keys.size();i++){
                unit.put(keys.get(i),AnalysisNeedData.getDataUnit(mainkey+keys.get(i)));
            }




            LinkedList<String> datastr  ;
            Map<String, LinkedList<String>>cmap = new HashMap<>();

            for(Map.Entry<String,LinkedList<Double>> entry:finalmap.entrySet()){
                datastr  = new LinkedList<>();
                LinkedList<Double> list = entry.getValue();
                for(int i = 0;i<list.size();i++){
                    datastr.add(nf.format(list.get(i)));
                }
                cmap.put(entry.getKey(),datastr);
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data",cmap);
           // jsonObject.put("data",datastr);
            jsonObject.put("time",datelist);
            jsonObject.put("unit",unit);
            finalmap = null;
            return ReturnUtil.finalObject(1,"获取成功",jsonObject);
      //      System.out.println(JSONObject.toJSONString(datelist));
        }


        return null;

    }

    //        //  long nowtime =Long.valueOf("1530951760000") ;// new Date().getTime();
//
//        // long lasttime = nowtime -86400000;
    public Double findCf(String key, Double d) {
        JSONObject jsonObject = FindSensorData.getCfData(key);
        System.out.println("系数因子：" + jsonObject.toJSONString());
        Map<String, Map<String, Double>> it = JSON.parseObject(jsonObject.toJSONString(), new TypeReference<Map<String, Map<String, Double>>>() {
        });
        Map<String, Double> it1 = it.get(key);
        Double average = 0.0;
        if (it1 != null)
            for (Map.Entry<String, Double> entry2 : it1.entrySet()) {
                average = d * entry2.getValue();
            }
        return average;
    }

}
