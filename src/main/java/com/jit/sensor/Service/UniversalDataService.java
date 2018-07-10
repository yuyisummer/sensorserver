package com.jit.sensor.Service;

import com.jit.sensor.Mapper.UniversaldataMapper;
import com.jit.sensor.Entity.Universaldata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UniversalDataService {

    @Autowired
    UniversaldataMapper universaldataMapper;

    public boolean insertdata(Universaldata universaldata){
        return  universaldataMapper.insert(universaldata)>0;
    }

    public Universaldata SelectLastData(String deveui,String devtype){
        return universaldataMapper.selectLastInfo(deveui,devtype);
    }
}
