package com.jit.sensor.entity;

public enum TResultCode {
    /* 成功状态码 */
    SUCCESS(1, "成功"),
    FAILURE(2, "失败"),

    /*失败状态码
     * */
    SUBSCRIPTION_EXISTS(1000, "添加频道失败，频道已订阅"),
    UNSUBSCRIPTION_NOT_EXISTS(1001, "频道未订阅，取消订阅失败"),
    SUBSCRIPTION_EMPTY(1002, "频道为空"),
    DOWNLOADDATA_BROKEN(1003, "下行数据未接收到或有误"),
    PUBLISH_EXCEPTION(1004, "publish异常"),
    CHANGE_RELAY_STATUS_FAILURE(1005, "继电器已处于目标状态");


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
