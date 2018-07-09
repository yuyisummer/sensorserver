package com.jit.sensor.api;

import com.jit.sensor.model.AverageInfo;
import com.jit.sensor.model.Idtodate;
import com.jit.sensor.model.TMessage;
import com.jit.sensor.service.IdtoTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.List;

@RestController
@RequestMapping("/SelectAverageInfo")
public class SelectAvergeInfo {

    @Autowired
    IdtoTimeService idtoTimeService;

    @PostMapping("selectInfo")
    public TMessage SelectInfo(@RequestBody AverageInfo averageInfo) {
        long nowtime = Long.valueOf(averageInfo.getNowtime());
        long lasttime = Long.valueOf(averageInfo.getLasttime());
        long interval = nowtime - lasttime;
        //间隔时间为1秒
        long jiange = 1000;
        //小于1天
        if (interval <= 8640000) {
            //间隔时间为小时
            jiange = jiange*60*60;
        }
        //小于1.3个月，按40天来算
        else if( interval<=345600000) {
            //间隔时间为天
            jiange = jiange*60*60*24;
        }
        //小于6个月
        else  if(interval<=1555200000){
            //间隔时间为旬
            jiange = jiange*60*60*24*10;
        } else {
            //间隔时间为月(注意大月和小月)
            jiange = jiange*60*60*24*30;
        }

        //需要的是来自哪个板子哪个传感器的感知信息
        String str = averageInfo.getDeveui()+"-"+averageInfo.getDevtype();

        //通过起始时间获取对应id列表
        List<Idtodate> list = idtoTimeService.selectDayId
                (averageInfo.getNowtime(),averageInfo.getLasttime());

        System.out.println("list length:"+list.size());

        return null;
    }



    public int getNumberDay(int year,int month){
        Calendar c = Calendar.getInstance();
        c.set(year, month, 0); //输入类型为int类型
        return c.get(Calendar.DAY_OF_MONTH);
    }

}
