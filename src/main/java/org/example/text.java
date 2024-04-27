package org.example;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;


public class text {

    private InfluxDB influxDB;

    public void getConn(){
        //获取链接
        influxDB = InfluxDBFactory.connect("http://169.254.60.171:8086");
    }

    public void close(){
        //关闭
        influxDB.close();

    }

    public void testDataBase(){
        influxDB.query(new Query("create database logs"));
    }
}
