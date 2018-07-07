package com.jit.sensor.base.utils;

import com.alibaba.fastjson.JSONObject;
import com.jit.sensor.model.TMessage;

public class ReturnUtil {
    public static String finaldata(int i, String str, JSONObject json){
        ReturnStr returnStr = new ReturnStr();
        return  returnStr.setTMessage(i,str,json);
    }
    public static TMessage finalObject(int i, String str, JSONObject json){
        ReturnStr returnStr = new ReturnStr();
        return  returnStr.setTMessageObject(i,str,json);
    }
}
