package org.example;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class connectDatabase {
    private InfluxDB influxDB;
    public List<Object> secondColumnValues = new ArrayList<>();

    public void getConn(){
        //获取链接
        influxDB = InfluxDBFactory.connect("http://localhost:8086");
    }

    public void close(){
        //关闭
        influxDB.close();

    }

    public void insertPoint(String db, String measurement, float data,String tag){
        influxDB.setDatabase(db);
        // 本地时间
        ZonedDateTime localDateTime = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
        // 转换为UTC时间
        Instant utcInstant = localDateTime.toInstant();
        long utcTimestamp = utcInstant.toEpochMilli();

        Point point = Point.measurement(measurement)
                .tag("Device_number","001")
                .addField("state",tag)
                .addField("data", data)
                .time(utcTimestamp, TimeUnit.MILLISECONDS)
                .build();
        influxDB.write(point);
    }





    public void selectdata() {

        influxDB.setDatabase("history");
        for (int i = 0; i < 2048; i++) {
            Point point = Point.measurement("demo")
                    .addField("data", i).build();
            //influxDB.write(point);

        }
        QueryResult query = influxDB.query(new Query("select * from demo"));
        //System.out.println(query);
        List<QueryResult.Result> results = query.getResults();
        Object[] secondColumnArray = null;
        for (QueryResult.Result result : results) {
            List<QueryResult.Series> series = result.getSeries();
            for (QueryResult.Series serie : series) {
                List<String> columns = serie.getColumns();
                List<List<Object>> values = serie.getValues();
                for (List<Object> row : values) {

                    if (row.size() > 1) {
                        Object secondColumnValue = row.get(1); // 获取第二列的值，索引为1
                        secondColumnValues.add(secondColumnValue); // 将值添加到数组中

                    }
                }

                /* 将List转换为数组
                secondColumnArray = secondColumnValues.toArray(new Object[0]);

                // 打印数组
                System.out.println(Arrays.toString(secondColumnArray));*/
                for (int i = 0; i < values.size(); i++) {
                    List<Object> points = values.get(i);
                    for (int j = 0; j < points.size(); j++) {
                        //System.out.println("columns:"+columns.get(j)+"  "+"value:"+points.get(j));

                    }
                }
            }
        }
        //System.out.println(secondColumnValues);
    }

    public String getsecondColumnValues(){
        return secondColumnValues.toString();
    }
}
