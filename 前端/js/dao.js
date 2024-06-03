const Influx = require('influx');

const influx = new Influx.InfluxDB({
    host: 'localhost', // InfluxDB服务器地址
    port: 8086,        // InfluxDB端口
    database: 'history', // 要连接的数据库名

});


// 起始时间和结束时间，精确到小时
const startTime = '2024-04-25T15:00:00Z'; // 开始时间
const endTime = '2024-05-10T07:00:00Z';   // 结束时间

// 查询数据示例
influx.query(`
  SELECT * 
  FROM testdata 
  WHERE time >= '${startTime}' 
    AND time <= '${endTime}'
`)
.then(results => {
    const orderDataValues = results.map(result => {
        return {
            time: result.time,
            data: result.data,
            tag: result.state_1     };
    });
    console.log(orderDataValues)
    // 在这里处理查询结果，例如将其发送到前端
})
.catch(error => {
    console.error('Error fetching data from InfluxDB', error);
});






