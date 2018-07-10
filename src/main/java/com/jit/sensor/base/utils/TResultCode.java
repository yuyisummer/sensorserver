package com.jit.sensor.base.utils;

import java.util.ArrayList;
import java.util.List;

public enum TResultCode {
    /* 成功状态码 */
    SUCCESS(1, "成功"),
    FAILURE(2, "失败");

    private Integer code;

    private String message;

    TResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
