package com.jit.sensor.api;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.jit.sensor.base.utils.AnalysisNeedData;
import com.jit.sensor.base.utils.MonthTime;
import com.jit.sensor.base.utils.ThisTime;
import com.jit.sensor.model.AverageInfo;
import com.jit.sensor.model.Idtodate;
import com.jit.sensor.service.IdtoTimeService;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MonthCustomizeDate {

    private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static String format = "yyyy-MM-dd HH:mm:ss";

    private  static  int biaozhi = 1;

    //   @Autowired
    //  private StringRedisTemplate strRedis;

    public static JSONObject getConversionDate( AverageInfo ag) {
        String deveui = ag.getDeveui();
        String devtype = ag.getDevtype();
        List<String> list1 = ag.getDatatype();
        String mainkey = deveui+"-"+devtype+"-";
        NumberFormat nf= NumberFormat.getInstance();  //创建格式化类nf
        nf.setMaximumFractionDigits(2);    //数值2表示保留2位小数


        IdtoTimeService idtoTimeService = AnalysisNeedData.getBean(IdtoTimeService.class);
        StringRedisTemplate strRedis = AnalysisNeedData.getBean(StringRedisTemplate.class);
        //时间进行初始化
        df = getFormat();
        Long nowtime = Long.valueOf(ag.getNowtime());
        Long lasttime = Long.valueOf(ag.getLasttime());


        //需要的是来自哪个板子哪个传感器的感知信息
        String str = deveui + "-" + devtype + "-";

        Map<String, LinkedList<Double>> datamap = new HashMap<>();

        LinkedList<String> datelist = new LinkedList<>();



        for (long i = lasttime ;i <= nowtime; ) {
            MonthTime m = new MonthTime(String.valueOf(i));
            String zs  = MonthTime.thisMonthEnd();
            System.out.println("当前运行到："+ ThisTime.zhuanzheng(i));
            long lt = new Long(i);
            Date date = new Date(lt);
            String strdate = df.format(date);
            System.out.println("df.format 52:"+strdate);
            datelist.add(strdate.split(" ")[biaozhi]);

            System.out.println("起始时间 zs："+zs);
            System.out.println("起始时间 i："+String.valueOf(i));
            SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date i1 = s.parse(zs);
                zs =String.valueOf(i1.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            List<Idtodate> l = idtoTimeService.selectDayId
                    (zs, String.valueOf(i));
            System.out.println("起始时间："+ThisTime.zhuanzheng(Long.valueOf(String.valueOf(i)))+"-"+ThisTime.zhuanzheng(Long.valueOf(zs)));
            int num = l.size();
            System.out.println("每个时间段里需要的数据量："+num);
              Map<String, Double> map1 = new HashMap<>();
            //  Map<String,Double> average = new HashMap<>();
            for (int j = 0; j < num; j++) {
                String key = str + l.get(j).getId();
                String s1 = strRedis.opsForValue().get(key);
                Map<String, Double> map = JSONObject.parseObject(JSONObject.parseObject(s1).getString("value"), new TypeReference<Map<String, Double>>() {
                });
                for (int c = 0; c < list1.size(); c++) {
                    String key1 = list1.get(c);
                    if (map1.get(key1) != null) {
                        map1.put(key1, Double.valueOf(map1.get(key1)) + Double.valueOf(map.get(key1)));
                    } else {
                        map1.put(key1, map.get(key1));
                    }
                }
            }
            for(Map.Entry<String,Double> entry:map1.entrySet()){
                LinkedList<Double> list2 = datamap.get( entry.getKey());
                if(list2 == null) {
                    list2 = new LinkedList<>();
                }
                try {
                    list2.add(entry.getValue()/num);
                } catch (Exception e){
                    list2.add(0.0);
                }

                //  list2.add(entry.getValue()/num);
                datamap.put(entry.getKey(),list2);

            }
            map1 = null;

            try {
                i = df.parse( MonthTime.nextMonth()).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        Map<String, LinkedList<String>> finaldata = new HashMap<>();

        for (Map.Entry<String, LinkedList<Double>> entry : datamap.entrySet()) {
            LinkedList<String> linkedList = new LinkedList<>();
            LinkedList<Double> linkedList1 = entry.getValue();
            for(int i= 0;i<linkedList1.size();i++){
                linkedList.add(nf.format(linkedList1.get(i)));
            }
            finaldata.put(entry.getKey(),linkedList);
        }
        Map<String,String> unit = new HashMap<>();
        for(int i = 0;i<list1.size();i++){
            unit.put(list1.get(i), AnalysisNeedData.getDataUnit(mainkey+list1.get(i)));
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data",finaldata);
        jsonObject.put("time",datelist);
        jsonObject.put("unit",unit);

        return jsonObject;

    }

    //规格format
    public static SimpleDateFormat getFormat() {
            //起始时间规格成天
            biaozhi = 0;
            return new SimpleDateFormat("yyyy-MM");
        }
    }



