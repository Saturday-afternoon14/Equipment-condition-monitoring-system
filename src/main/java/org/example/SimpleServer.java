package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SimpleServer {
    public static void main(String[] args) throws IOException {
        connectDatabase getdata = new connectDatabase();
        getdata.getConn();
        //getdata.selectdata();
        //String data = getdata.getsecondColumnValues();

        AverageCalculator calculator = new AverageCalculator();


        int port = 60000;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("服务器启动，监听端口 " + port);

        List<Socket> clients = new ArrayList<>(); // 保存所有客户端连接

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("客户端连接成功");
            clients.add(clientSocket); // 将新客户端添加到列表中

            new Thread(() -> {
                try (
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
                ) {
                    String inputLine;
                    while (true) {
                        RandomXlsReader xmlReader = new RandomXlsReader();
                        xmlReader.readRandomColumnAndAddNoise();
                        List<Double> result = xmlReader.getResult();


                        double average = calculator.calculateAverage(result);

                        //System.out.println(result);

                        Thread.sleep(1000);
                        out.println(result);


                        inputLine = in.readLine();

                        getdata.insertPoint("history","testdata", (float) average,inputLine,System.currentTimeMillis());

                        //Thread.sleep(1000);
                        if(Objects.equals(inputLine, "[9]")) {
                            getdata.insertPoint("history", "Normal_data", (float) average,"[9]",System.currentTimeMillis());
                        }else{
                            getdata.insertPoint("history", "Anomalous_data", (float) average,inputLine,System.currentTimeMillis());
                        }

                        System.out.println("收到客户端消息: " + inputLine);
                        //int read = System.in.read();


                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    clients.remove(clientSocket); // 如果连接关闭，从列表中移除
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
            getdata.close();
        }
    }
}
