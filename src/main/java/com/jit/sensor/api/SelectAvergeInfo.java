package com.jit.sensor.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.jit.sensor.base.utils.AnalysisNeedData;
import com.jit.sensor.base.utils.MonthTime;
import com.jit.sensor.base.utils.ReturnUtil;
import com.jit.sensor.base.utils.ThisTime;
import com.jit.sensor.model.*;
import com.jit.sensor.service.IdtoTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.security.pkcs11.Secmod;

import javax.xml.crypto.Data;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/SelectAverageInfo")
public class SelectAvergeInfo {

    @Autowired
    IdtoTimeService idtoTimeService;
    @Autowired
    private RedisKeyValueTemplate rkvt;

    @Autowired
    private StringRedisTemplate strRedis;

    @PostMapping("selectInfo")
    public TMessage SelectInfo(@RequestBody AverageInfo averageInfo) {
        long nowtime = Long.valueOf(averageInfo.getNowtime());
        long lasttime = Long.valueOf(averageInfo.getLasttime());
        List<String> keys = averageInfo.getDatatype();
        String mainkey = averageInfo.getDeveui()+"-"+averageInfo.getDevtype()+"-";
        long interval = nowtime - lasttime;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        System.out.println("nowtime:" + simpleDateFormat.format(nowtime));
        System.out.println("lasttime:" + simpleDateFormat.format(lasttime));
        System.out.println("interval:" + interval);
        //间隔时间为1秒
        long jiange = 1000;
        //小于1天
        if (interval <= 86400000) {
            //间隔时间为小时
            jiange = jiange * 60 * 60;
          //  simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        }
        //小于1.3个月，按40天来算
        else if (interval <=Long.valueOf("3456000000")) {
            //间隔时间为天
           // simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd ");
            jiange = jiange * 60 * 60 * 24;
        }
        //小于6个月
        else if (interval <= Long.valueOf("15552000000")) {
            //间隔时间为旬
            jiange = jiange * 60 * 60 * 24 * 10;
        } else {
            //间隔时间为月(注意大月和小月)
            MonthTime monthTime = new MonthTime(averageInfo.getNowtime());
            String monthym = MonthTime.thisMonthEnd();
            System.out.println("SelectAvergeInfo 72:"+monthym);
            try {
               Date i = simpleDateFormat.parse(monthym);
                averageInfo.setNowtime(String.valueOf(i.getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
          //  averageInfo.setNowtime(monthym);
             monthTime = new MonthTime(averageInfo.getLasttime());
            System.out.println("SelectAvergeInfo 81:"+averageInfo.getLasttime());
            String monthyc = MonthTime.thisMonth();
            System.out.println("SelectAvergeInfo 82:"+monthyc);
            try {
                Date i = simpleDateFormat.parse(monthyc);
                averageInfo.setLasttime(String.valueOf(i.getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            JSONObject j =MonthCustomizeDate.getConversionDate(averageInfo);

            return ReturnUtil.finalObject(1,"获取成功",j);

        }

        JSONObject j = ConversionDate.getConversionDate(jiange, averageInfo);

        return ReturnUtil.finalObject(1,"获取成功",j);
    }

}
