package com.jit.sensor.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import javax.persistence.Entity;

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