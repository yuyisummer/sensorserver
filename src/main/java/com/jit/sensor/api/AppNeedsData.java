package com.jit.sensor.api;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.jit.sensor.base.utils.MonthTime;
import com.jit.sensor.base.utils.ReturnUtil;
import com.jit.sensor.base.utils.ThisTime;
import com.jit.sensor.model.AppDataInfo;
import com.jit.sensor.model.AverageInfo;
import com.jit.sensor.model.TMessage;
import com.jit.sensor.model.WebDataInfo;
import com.jit.sensor.service.IdtoTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/AppNeeds")
public class AppNeedsData {
    @Autowired
    IdtoTimeService idtoTimeService;
    AverageInfo averageInfo;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @PostMapping("/LastTime")
    public TMessage getLastTimeDate(@RequestBody AppDataInfo a) {
        //基本时间间隔为1小时
          Long jg =Long.valueOf("3600000");
        //  System.out.println("long jg:"+String.valueOf(jg));
        String type = a.getType();
        Long nowtime = Long.valueOf("1530720000000");
        //Long nowtime = ThisTime.lastonehour();
        Long lastime = Long.valueOf("999999999999");
        switch (type) {
            case "1":
                //一天
                lastime = nowtime - 86400000;
                //jg = Long.valueOf("3600000");
                break;
            case "2":
                //一周
                lastime = nowtime - 604800000;
                jg = jg*24;
                break;
            case "3":
                //一月
                //Long.valueOf("1530970380000");
                int num = ThisTime.getNumberDay(Long.valueOf("1530720000000"));
                jg = jg*24;
                lastime = nowtime - Long.valueOf("86400000") * num;
                break;
            case "4":
                 new MonthTime(String.valueOf(nowtime));
                String monthym = MonthTime.thisMonthEnd();
                Date i = null;
                try {
                    i = simpleDateFormat.parse(monthym);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String monthyc =MonthTime.LastthisMonth();
                Date i1 = null;
                try {
                    i1 = simpleDateFormat.parse(monthyc);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                System.out.println(" 1+:"+i.getTime()+" "+i1.getTime());
                inityear(String.valueOf(i.getTime()),String.valueOf(i1.getTime()),a);
                JSONObject j =MonthCustomizeDate.getConversionDate(averageInfo);

                return ReturnUtil.finalObject(1,"获取成功",j);
        }
        init(nowtime, lastime, a);
        JSONObject j = ConversionDate.getConversionDate(jg, averageInfo);
        return ReturnUtil.finalObject(1, "获取成功", j);
    }

    public void init(Long nowtime, Long lastime, AppDataInfo a) {
        averageInfo = new AverageInfo();
        averageInfo.setDevtype(a.getDevtype());
        averageInfo.setDeveui(a.getDeveui());
        averageInfo.setDatatype(a.getDatatype());
        averageInfo.setNowtime(String.valueOf(nowtime));
        averageInfo.setLasttime(String.valueOf(lastime));
    }

    public void inityear(String nowtime, String lastime, AppDataInfo a) {
        averageInfo = new AverageInfo();
        averageInfo.setDevtype(a.getDevtype());
        averageInfo.setDeveui(a.getDeveui());
        averageInfo.setDatatype(a.getDatatype());
        averageInfo.setNowtime(nowtime);
        averageInfo.setLasttime(lastime);
    }

}
