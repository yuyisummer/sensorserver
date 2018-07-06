package com.jit.sensor.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.annotation.Id;

import javax.persistence.Entity;
import java.io.Serializable;
@Getter
@Setter
@Entity
public class User {
    @Id
    private String ceshi;
    private String haidongdong;

    @Override
    public String toString() {
        return "User{" +
                "ceshi='" + ceshi + '\'' +
                ", haidongdong='" + haidongdong + '\'' +
                '}';
    }
}