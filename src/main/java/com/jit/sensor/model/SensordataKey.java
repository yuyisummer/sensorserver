package com.jit.sensor.model;

public class SensordataKey {



    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sensordata.deveui
     *
     * @mbggenerated
     */
    private String deveui;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sensordata.date
     *
     * @mbggenerated
     */
    private String date;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sensordata.deveui
     *
     * @return the value of sensordata.deveui
     *
     * @mbggenerated
     */
    public String getDeveui() {
        return deveui;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sensordata.deveui
     *
     * @param deveui the value for sensordata.deveui
     *
     * @mbggenerated
     */
    public void setDeveui(String deveui) {
        this.deveui = deveui == null ? null : deveui.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sensordata.date
     *
     * @return the value of sensordata.date
     *
     * @mbggenerated
     */
    public String getDate() {
        return date;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sensordata.date
     *
     * @param date the value for sensordata.date
     *
     * @mbggenerated
     */
    public void setDate(String date) {
        this.date = date == null ? null : date.trim();
    }
}