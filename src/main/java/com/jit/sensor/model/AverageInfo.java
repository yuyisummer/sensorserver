package com.jit.sensor.model;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AverageInfo {
    String deveui;
    String devtype;
    List<String> datatype;
    String nowtime;
    String lasttime;
}
