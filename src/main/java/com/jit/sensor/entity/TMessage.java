package com.jit.sensor.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TMessage {
    @Override
    public String toString() {
        return "TMessage{" +
                "Code=" + Code +
                ", Message='" + Message + '\'' +
                ", Time='" + Time + '\'' +
                ", Data=" + Data +
                '}';
    }

    public Integer Code;
    public String Message;
    public String Time;
    public Object Data;


}
