package com.jit.sensor.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
public class DataInfo {
    String time;
    Double data;
}
