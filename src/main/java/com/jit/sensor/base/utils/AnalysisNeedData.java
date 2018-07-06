package com.jit.sensor.base.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.*;

public class AnalysisNeedData {
    private static Map<String,Map<String,String>> needdatalist =
            new HashMap<String,Map<String,String>>(){};

    public static  void SetNeedData(String str,Map<String,String> map){
        needdatalist.put(str,map);
    }
    public static Map<String,String> getNeedData(String str){
       return needdatalist.get(str);
    }

    public static LinkedHashMap<String,String> toMap(String jsonobject){
        LinkedHashMap<String,String> map = JSON.parseObject(jsonobject,LinkedHashMap.class);
        return map;

    }



    public static char[] bytesToHexString(byte[] src) {
        String stringBuilder = new String("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                hv = "0"+hv;
            }
            stringBuilder = stringBuilder+hv;
        }
        System.out.println("stringbuilder:"+stringBuilder);
       char[] list = stringBuilder.toString().toCharArray();
        return list ; //将字符串变量转换为字符数组
      //  return stringBuilder.toString();

    }

    }
