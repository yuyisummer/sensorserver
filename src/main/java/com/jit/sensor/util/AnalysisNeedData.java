package com.jit.sensor.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.jit.sensor.entity.TMessage;
import com.jit.sensor.entity.Universaldata;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class AnalysisNeedData implements ApplicationContextAware {
    private static Map<String, Map<String, String>> needdatalist =
            new HashMap<String, Map<String, String>>() {
            };
    //启动类set入，调用下面set方法
    private static ApplicationContext applicationContext;

    public static void setNeedData(String str, Map<String, String> map) {
        needdatalist.put(str, map);
    }

    public static Map<String, String> getNeedData(String str) {
        return needdatalist.get(str);
    }

    public static LinkedHashMap toMap(String string) {
        return JSON.parseObject(string, LinkedHashMap.class);
    }

    public static char[] bytesToHexString(byte[] src) {
        String stringBuilder = "";
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte aSrc : src) {
            int v = aSrc & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                hv = "0" + hv;
            }
            stringBuilder = stringBuilder + hv;
        }
        System.out.println("stringbuilder:" + stringBuilder);
        //将字符串变量转换为字符数组
        return stringBuilder.toCharArray();
    }

    public static String getDataUnit(String key) {
        System.out.println("key AnalysisNeedData 50:" + key);
        Map<String, Double> m = FindSensorData.getCfKey(key);
        Map<String, String> j = new HashMap<>();
        if (m == null) {
            return "";
        }
        // return  j.put(key.split("-")[2],new );

        for (Map.Entry<String, Double> entry : m.entrySet()) {
            return entry.getKey();
        }
        return "";
    }

    public static Object getClass(Class<?> t) {
        if (applicationContext != null) {
            System.out.println("BeanName:" + t.getName());
            return applicationContext.getBean(t.getSimpleName() + ".class");
        } else {
            System.out.println("application为空");
            return null;
        }
    }

    public static <T> T getBean(String name) {
        //  assertContextInjected();
        return (T) applicationContext.getBean(name);
    }

    public static <T> T getBean(Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }

    public static TMessage getWebSocketData(Universaldata universaldata) {
        Map<String, String> map = FindSensorData.getSensorInfoMap(universaldata.getDeveui(), universaldata.getDevtype());
        String mainkey = universaldata.getDeveui() + "-" + universaldata.getDevtype() + "-";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 时间戳转换日期
        String sd = sdf.format(new Date(Long.parseLong(universaldata.getTime())));
        System.out.println(sd);
        JSONObject retjson = new JSONObject();

        Map<String, Double> j = JSON.parseObject(FindSensorData.getAnalysisData(map, universaldata).toJSONString()
                , new TypeReference<Map<String, Double>>() {
                });

        Map<String, String> unit = new HashMap<>();

        for (Map.Entry<String, Double> entry : j.entrySet()) {
            String key = mainkey + entry.getKey();
            Map<String, Double> m = FindSensorData.getCfKey(key);
            for (Map.Entry<String, Double> entry1 : m.entrySet()) {
                j.put(entry.getKey(), entry.getValue() * entry1.getValue());
                unit.put(entry.getKey(), entry1.getKey());
            }
        }

        Map<String, String> finalmap = new HashMap<>();
        for (Map.Entry<String, Double> entry : j.entrySet()) {
            finalmap.put(entry.getKey(), String.valueOf(entry.getValue()));
        }
        retjson.put("data", finalmap);
        retjson.put("time", sd);
        retjson.put("unit", unit);
        return ReturnUtil.finalObject(1, "websocket成功", retjson);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }


}
