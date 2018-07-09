package com.jit.sensor.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Entity
@Setter
@Getter
public class ListDataInfo  {
    @Id
   String  list ;

}
