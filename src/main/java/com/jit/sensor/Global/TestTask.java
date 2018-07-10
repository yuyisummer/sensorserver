package com.jit.sensor.Global;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class TestTask {
    // http://cron.qqe2.com/
    private static final SimpleDateFormat df= new SimpleDateFormat("HH:mm:ss");
    //@Scheduled(fixedRate = 3000)
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void reportTime() {
        System.out.println("now time is "+df.format(new Date()));
    }
}