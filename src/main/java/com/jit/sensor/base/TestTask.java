package com.jit.sensor.base;

import com.alibaba.fastjson.JSONObject;
import com.jit.sensor.base.utils.AnalysisNeedData;
import com.jit.sensor.base.utils.FindSensorData;
import com.jit.sensor.model.DataInfo;
import com.jit.sensor.model.Idtodate;
import com.jit.sensor.model.ListDataInfo;
import com.jit.sensor.model.Universaldata;
import com.jit.sensor.service.IdtoTimeService;
import com.jit.sensor.service.UniversalDataService;
import io.netty.handler.codec.json.JsonObjectDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

//import hprose.io.HproseFormatter;

import static com.alibaba.fastjson.util.IOUtils.close;

@Component
public class TestTask {
    // http://cron.qqe2.com/
    private static final SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
    //@Scheduled(fixedRate = 3000)

    @Autowired
    private RedisKeyValueTemplate rkvt;
    static long nowtime = Long.valueOf("1530970380000");// new Date().getTime();
    private  static  int k =0;

    //0 0/3 * * * ?
    // @Scheduled(cron = "0 0 0/1 * * ?")
    @Scheduled(cron = "0 0/1 * * * ? ")
    public void reportTime() {
        System.out.println("now time is " + df.format(new Date()));
        //  long nowtime =Long.valueOf("1530951760000") ;// new Date().getTime();

        long lasttime = nowtime -86400000;
//        if(lasttime < Long.valueOf("1530950400000"))
//            return;

        if(k>5)
           return;
        Map<String, List<DataInfo>> datainfomap = new HashMap<>();
        Map<String,qiuzhi> averagemap = new HashMap<>();




        UniversalDataService universalDataService = AnalysisNeedData.getBean(UniversalDataService.class);
        List<Universaldata> list = universalDataService.SelectIntervalData(String.valueOf(nowtime), String.valueOf(lasttime));
        IdtoTimeService idtoTimeService = AnalysisNeedData.getBean(IdtoTimeService.class);
        int id = idtoTimeService.selectMaxId();
        Idtodate idtodate = new Idtodate();
        idtodate.setId(id + 1);
        idtodate.setDate(String.valueOf(nowtime));
        if (idtoTimeService.Insert(idtodate)) {
            System.out.println("id与当前自动执行redis操作时间成功");
        } else {
            System.out.println("id与当前自动执行redis操作时间失败");
        }

        for (int i = 0; i < list.size(); i++) {
            Universaldata universaldata = list.get(i);

            Map<String, String> map = FindSensorData.GetSensorInfoMap(universaldata.getDeveui(), universaldata.getDevtype());
            JSONObject jsonObject = FindSensorData.getAnalysisData(map, universaldata);
            System.out.println("返回的JSONobject：" + jsonObject);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (entry.getKey().equals("all") || entry.getKey().equals("Remove"))
                    continue;
                String key2 =  universaldata.getDeveui() + "-" + universaldata.getDevtype() +"-" ;
                String key = key2 + id;

                DataInfo dataInfo = new DataInfo();
                dataInfo.setData(jsonObject.getDouble(entry.getKey()));
                dataInfo.setTime(universaldata.getTime());
                List<DataInfo> list1 = null;
                list1 = datainfomap.get(key);

                qiuzhi q =null;
                System.out.println("79:entry.getKey:"+entry.getKey());
                q = averagemap.get(key2+entry.getKey());

                if(q!=null){
                    q .setInfonum(q.getInfonum()+1);
                    q.setInfosum( (q.getInfosum()+jsonObject.getDouble(entry.getKey())));
                    averagemap.put(key2+entry.getKey(),q);
                }else {
                    q = new qiuzhi();
                    q.setInfonum(1);
                    q.setInfosum(jsonObject.getDouble(entry.getKey()));
                    averagemap.put(key2+entry.getKey(),q);
                  //  System.out.println("91:averagemap.put:"+entry.getKey()+" "+q);
                }

                if (list1 != null) {
                    list1.add(dataInfo);
                    datainfomap.put(key, list1);
                } else {
                    List<DataInfo> list2 = new ArrayList<>();
                    list2.add(dataInfo);
                    datainfomap.put(key, list2);
                }
            }
        }
        System.out.println("ceshi:" + datainfomap.size());
        System.out.println("list数组遍历结束\n\n\n");

        for (Map.Entry<String, List<DataInfo>> entry : datainfomap.entrySet()) {
            ListDataInfo listDataInfo = new ListDataInfo();

            System.out.println("entry.getKey()" + entry.getKey());

            System.out.println("这个时间段的平均值(应该是有多个)为：");
            LinkedList<String> averagename = new LinkedList<>();
            LinkedList<Double> average = new LinkedList<>();

            String[] str = entry.getKey().split("-");
            String key = str[0]+"-"+str[1];
            System.out.println("当前准备存放的是哪个板子的哪个传感器:"+key);

            for(Map.Entry<String,qiuzhi> entry1:averagemap.entrySet()){
                    if(entry1.getKey().indexOf(key)<0)
                        continue;
                    averagename.add(entry1.getKey());
                    qiuzhi q =  entry1.getValue();
                    average.add(q.getInfosum()/q.getInfonum());
                    System.out.println("值："+entry1.getKey()+" 平均:"+(q.getInfosum()/q.getInfonum()));
                    System.out.println("和为:"+q.getInfosum());
                System.out.println("个数为:"+q.getInfonum());
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("averagename",averagename);
            jsonObject.put("average",average);
            jsonObject.put ("datalist",entry.getValue());
            listDataInfo.setList(jsonObject.toJSONString());

            rkvt.insert(entry.getKey(), listDataInfo);
            System.out.println("插入数据");
        }
        System.out.println("redis存放结束");
        datainfomap = null;
        nowtime -= 86400000;
        k++;
    }


    public static byte[] serializeList(List<?> list) {
        if (list == null || list.size() == 0) {
            return null;
        }
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        byte[] bytes = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            for (Object obj : list) {
                oos.writeObject(obj);
            }
            bytes = baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(oos);
            close(baos);
        }
        return bytes;
    }

    class qiuzhi{
        public int getInfonum() {
            return infonum;
        }

        public void setInfonum(int infonum) {
            this.infonum = infonum;
        }

        public Double getInfosum() {
            return infosum;
        }

        public void setInfosum(Double infosum) {
            this.infosum = infosum;
        }
        int infonum;
        Double infosum;
    }
}