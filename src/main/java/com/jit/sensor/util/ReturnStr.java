package com.jit.sensor.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jit.sensor.entity.TMessage;

import java.util.Date;

public class ReturnStr {
     TMessage tMessage;
    public  String setTMessage(int i, String str, JSONObject json) {
        tMessage = new TMessage();
        tMessage.Code = i;
        tMessage.Message = str;
        tMessage.Time = String.valueOf(new Date());
        tMessage.Data = json;
        return JSON.toJSONString(tMessage);
    }

    public  TMessage setTMessageObject(int i, String str, JSONObject json) {
        tMessage = new TMessage();
        tMessage.Code = i;
        tMessage.Message = str;
        tMessage.Time = String.valueOf(new Date());
        tMessage.Data = json;
        return tMessage;
    }
}
