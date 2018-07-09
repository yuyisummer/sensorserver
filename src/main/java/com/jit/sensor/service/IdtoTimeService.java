package com.jit.sensor.service;

import com.jit.sensor.mapper.IdtodateMapper;
import com.jit.sensor.model.Idtodate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IdtoTimeService {
    @Autowired
    IdtodateMapper idtodateMapper;

    public int selectMaxId(){
        return  idtodateMapper.selectMaxId();
    }

    public boolean Insert(Idtodate idtodate){
        return  idtodateMapper.insert(idtodate)>0;
    }

    public List<Idtodate> selectDayId(String nowtime,String lasttime){
        return  idtodateMapper.selectDayId(nowtime,lasttime);
    }
}
