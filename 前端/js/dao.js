const Influx = require('influx');

const influx = new Influx.InfluxDB({
    host: 'localhost', // InfluxDB服务器地址
    port: 8086,        // InfluxDB端口
    database: 'history', // 要连接的数据库名

});



// 查询数据示例
influx.query('SELECT * FROM testdata')
    .then(results => {
       // console.log(results);
        // 在这里处理查询结果，例如将其发送到前端
    })
    .catch(error => {
        console.error('Error fetching data from InfluxDB', error);
    });



influx.query('SELECT last(time) FROM "testdata" ')
    .then(results => {
        //console.log(results)
        // 提取所有的 last_data 值
        const lastDataValues = results.groupRows.reduce((acc, groupRow) => {
            // 遍历每个分组中的行
            groupRow.rows.forEach(row => {
                // 提取 last_data 值
                if (row.last_data !== undefined) {
                    acc.push(row.time);
                }

            });

            return acc;
        }, []); // 初始化累加器为一个空数组
        //console.log(lastDataValues)
    });



influx.query('SELECT last(*) FROM "testdata"')
    .then(results => {
        // 遍历查询结果
        for (const result of results) {
            // 获取每个结果中的表
            for (const table of result) {
                // 获取表中的所有记录
                for (const record of table.getRecords()) {
                    // 获取时间戳的值
                    const time = record.getTime();

                    // 打印或处理时间戳值
                    console.log(time);

                    // 如果你还想获取其他字段的值，可以使用 record.getValue('fieldName')
                    // 例如，获取名为 'value' 的字段的值：
                    const value = record.getValue('value'); // 假设 'value' 是字段名
                    console.log(value);
                }
            }
        }
    })
    .catch(error => {
        console.error('Error fetching data from InfluxDB:', error);
    });



influx.query('SELECT last(*) FROM testdata ')
    .then(results => {
        //console.log(results);

        // 初始化累加器
        const lastDataAcc = [];
        const lastTagsAcc = []; // 用于存储标签的数组
        const TimeAcc = [];

        // 遍历查询结果
        results.groupRows.forEach(groupRow => {
            // groupRow.tags 包含该分组的所有标签
            const tags = groupRow.tags;
            // 可以将tags对象转换为字符串，以便存储或处理
            const tagsString = JSON.stringify(tags);
            lastTagsAcc.push(tagsString); // 将标签对象转换为字符串并添加到数组中

            console.log(lastTagsAcc);

            groupRow.rows.forEach(row => {
                // 提取 last_data 值
                if (row.last_data !== undefined) {
                    lastDataAcc.push(row.last_data);
                }

                // 提取时间的小时和分钟部分
                if (row.time !== undefined) {
                    const date = new Date(row.time);
                    const hour = date.getHours();
                    const minute = date.getMinutes();
                    const formattedTime = `${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}`;
                    TimeAcc.push(formattedTime);
                }
            });
        });

        // 现在你可以使用 lastDataAcc, lastTagsAcc, 和 TimeAcc 这些数组了
        // 例如，将它们作为响应返回给客户端

    })
    .catch(error => {
        console.error('Error fetching data from InfluxDB', error);

    });

/*
// 写入数据示例
const point = {
    measurement: 'your_measurement',
    fields: {
        value: 42
    },
    tags: {
        location: 'here'
    }
};

influx.writePoints([point])
    .then(() => {
        console.log('Data written to InfluxDB');
    })
    .catch(error => {
        console.error('Error writing to InfluxDB', error);
    });*/
