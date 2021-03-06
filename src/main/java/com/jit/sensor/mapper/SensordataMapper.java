package com.jit.sensor.mapper;

import com.jit.sensor.model.Sensordata;
import com.jit.sensor.model.SensordataKey;

public interface SensordataMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sensordata
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(SensordataKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sensordata
     *
     * @mbggenerated
     */
    int insert(Sensordata record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sensordata
     *
     * @mbggenerated
     */
    int insertSelective(Sensordata record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sensordata
     *
     * @mbggenerated
     */
    Sensordata selectByPrimaryKey(SensordataKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sensordata
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(Sensordata record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sensordata
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(Sensordata record);
}