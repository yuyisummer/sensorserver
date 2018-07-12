package com.jit.sensor.entity;

public enum TResultCode {
    /* 成功状态码 */
    SUCCESS(1, "成功"),
    FAILURE(2, "失败"),

    /*失败状态码
     * 1000-2000
     * */
    SUBSCRIPTION_EXISTS(1000, "添加频道失败，频道已订阅"),
    UNSUBSCRIPTION_NOT_EXISTS(1001, "频道未订阅，取消订阅失败"),
    SUBSCRIPTION_EMPTY(1002, "频道为空");

    /*
     * 成功状态码
     * 2000-3000
     * */

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
