package com.jit.sensor.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.jit.sensor.entity.Sensorinfo;
import com.jit.sensor.entity.Universaldata;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FindSensorData {
    private static Map<String, Map<String, Double>> cf = new HashMap<>();


    public static Map<String, String> GetSensorInfoMap(String deveui, String devtype) {
        String key = deveui + "-" + devtype;
        Map<String, String> map = AnalysisNeedData.getNeedData(key);
        if (map == null) {
            System.out.println("map中没有对应解析");
            System.out.println("deveui:" + deveui + " devtype:" + devtype);
            Sensorinfo sensorinfo = FindSensorInfo.find(deveui, devtype);
            String data = null;
            try {
                data = sensorinfo.getDatalength();
                System.out.println("data:" + data);
            } catch (Exception e) {
                //  return returnStr.setTMessage(1, "数据库中没有对应信息", null);
            }

            map = AnalysisNeedData.toMap(data);
            Map<String, Map<String, Double>> mappeizhi = JSON.parseObject(sensorinfo.getCf(), new TypeReference<Map<String, Map<String, Double>>>() {
            });
            for (Map.Entry<String, String> entry : map.entrySet()) {
                System.out.println("key:" + entry.getKey() + " value:" + entry.getValue());
                String str = sensorinfo.getDeveui() + "-" + sensorinfo.getDevtype() + "-" + entry.getKey();
                if (cf.get(str) == null) {
                    System.out.println("key:" + str + "   系数因子放入cf-map中  value:" + JSONObject.toJSONString(mappeizhi.get(entry.getKey())));
                    cf.put(str, mappeizhi.get(entry.getKey()));
                }
            }


            AnalysisNeedData.SetNeedData(key, map);
        }
        return map;
    }

    public static Map<String, Double> getCfKey(String key) {
        Map<String, Double> m = cf.get(key);
        if (m == null) {
            String[] s = key.split("-");
            GetSensorInfoMap(s[0], s[1]);
        }
        return cf.get(key);
    }

    public static JSONObject getCfData(String key) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(key, cf.get(key));
        return jsonObject;
    }

    public static JSONObject getAnalysisData(Map<String, String> map, Universaldata universaldata) {
        JSONObject jsonObject = new JSONObject();
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            byte[] decode = decoder.decodeBuffer(universaldata.getData());
            char[] list = AnalysisNeedData.bytesToHexString(decode);
            System.out.println("list.length:" + list.length);
            int seek = 6;
            for (int i = 0; i < decode[2]; ) {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println("接下来要解析的数据：" + entry.getKey());
                    if ("all".equals(entry.getKey())) {
                        continue;
                    } else if ("Remove".equals(entry.getKey())) {
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
                        if (list[seek + j] >= '0' && list[seek + j] <= '9')
                            zhi = list[seek + j] - '0';
                        else
                            zhi = list[seek + j] - 'a' + 10;
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
