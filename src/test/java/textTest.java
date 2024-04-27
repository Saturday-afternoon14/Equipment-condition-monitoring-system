import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class textTest {

    private InfluxDB influxDB;
    @Before
    public void getConn(){
        //获取链接
        influxDB = InfluxDBFactory.connect("http://localhost:8086");
    }
    @After
    public void close(){
        //关闭
        influxDB.close();

    }
    @Test
    public void testDataBase(){

        influxDB.query(new Query("create database logs"));

        influxDB.setDatabase("history");

        QueryResult showDatabases = influxDB.query(new Query("show databases"));
        System.out.println(showDatabases);

        //influxDB.query(new Query("drop database logs"));

        //判断库是否存在
        boolean logs = influxDB.databaseExists("logs");
        System.out.println(logs);
    }

    @Test
    public void testMeasurement(){
        influxDB.setDatabase("history");
        QueryResult showMeasurements = influxDB.query(new Query("show measurements"));
        System.out.println(showMeasurements);
    }

    @Test
    public void testQuery(){
        influxDB.setDatabase("history");
        Point point1 = Point.measurement("testdata")
                .addField("data", 0.015531632)
                .build();
        influxDB.write(point1);
    }


    @Test
    public void testInsert(){
        influxDB.setDatabase("history");
        //创建point
        Point point = Point.measurement("actual")
                .tag("id", "11")
                .tag("origin", "master")
                .addField("wind", 23.23)
                .addField("power", 11.11)
                .addField("cpu", 110)
                .build();
        //influxDB.write(point);




        QueryResult query = influxDB.query(new Query("select * from testdata"));
        System.out.println(query);
        //批量插入,最多1000条
        //BatchPoints batchPoints = BatchPoints.builder().points(point, point).build();
        List<QueryResult.Result> results = query.getResults();
        for (QueryResult.Result result : results) {
            List<QueryResult.Series> series = result.getSeries();
            for (QueryResult.Series serie : series) {
                List<String> columns = serie.getColumns();
                List<List<Object>> values = serie.getValues();
                for (int i = 0; i < values.size(); i++) {
                    List<Object> points = values.get(i);
                    for (int j = 0; j < points.size(); j++) {
                        System.out.println("columns:"+columns.get(j)+"  "+"value:"+points.get(j));
                    }
                    System.out.println();

                }
            }

        }
    }
    public List<Object> secondColumnValues = new ArrayList<>();



    @Test
    public void testIn() {

        influxDB.setDatabase("history");
        for (int i = 0; i < 2048; i++) {
            Point point = Point.measurement("demo")
                    .addField("data", i).build();
            //influxDB.write(point);

        }
        QueryResult query = influxDB.query(new Query("select last(*) from testdata"));
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
                        System.out.println("columns:"+columns.get(j)+"  "+"value:"+points.get(j));

                    }
                }
            }
        }
        System.out.println(secondColumnValues);
    }

    @Test
    public void testIn2() {
        influxDB.setDatabase("history");
        QueryResult query = influxDB.query(new Query("select * from testdata where time>='2024-04-25T00:00:00Z' and time<='2024-04-25T23:59:59Z' "));
        System.out.println(query);
        List<QueryResult.Result> results = query.getResults();
        for (QueryResult.Result result : results) {
            List<QueryResult.Series> series = result.getSeries();
            for (QueryResult.Series serie : series) {
                List<String> columns = serie.getColumns();
                List<List<Object>> values = serie.getValues();



                for (int i = 0; i < values.size(); i++) {
                    List<Object> points = values.get(i);


                    // 假设时间戳是ISO 8601格式的字符串
                    String timestampStr = (String) points.get(0);
                    // 解析字符串为Instant对象
                    DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
                    Instant instant = Instant.from(formatter.parse(timestampStr));
                    // 获取UTC时间戳（毫秒）
                    long utcTimestamp = instant.toEpochMilli();
                    // 转换为本地时间
                    ZonedDateTime localDateTime = instant.atZone(ZoneId.of("Asia/Shanghai"));
                    System.out.println("Local Time: " + localDateTime);


                    for (int j = 1; j < points.size(); j++) {
                        System.out.println("columns:"+columns.get(j)+"  "+"value:"+points.get(j));
                    }
                    System.out.println();

                }
            }

        }

    }


    @Test
    public void testIn3() {
        influxDB.setDatabase("history");
        // 本地时间
        ZonedDateTime localDateTime = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
        // 转换为UTC时间
        Instant utcInstant = localDateTime.toInstant();
        long utcTimestamp = utcInstant.toEpochMilli();

        // 创建并写入数据点
        Point point = Point.measurement("temperature")
                .time(utcTimestamp, TimeUnit.MILLISECONDS)
                .addField("value", 25.6)
                .build();
        //influxDB.write(point);

        String query = "SELECT * FROM temperature";
        QueryResult result = influxDB.query(new Query(query));

        // 遍历结果并转换时间为本地时间
        for (QueryResult.Result res : result.getResults()) {
            for (QueryResult.Series series : res.getSeries()) {
                List<List<Object>> values = series.getValues();
                for (List<Object> value : values) {
                    // 假设时间戳是ISO 8601格式的字符串
                    String timestampStr = (String) value.get(0);
                    // 解析字符串为Instant对象
                    DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
                    Instant instant = Instant.from(formatter.parse(timestampStr));
                    // 获取UTC时间戳（毫秒）
                    long utcTimestamp1 = instant.toEpochMilli();
                    // 转换为本地时间
                    ZonedDateTime localDateTime1 = instant.atZone(ZoneId.of("Asia/Shanghai"));
                    System.out.println("Local Time: " + localDateTime1);
                }
            }
        }
    }



}