import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class testSimpleServer {
    public static void main(String[] args) throws IOException {
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
                            out.println("服务器回复: " + "sad");
                            System.out.println(1);
                            inputLine = in.readLine();

                            System.out.println("收到客户端消息: " + inputLine);
                            System.out.println(2);
                        }

                } catch (IOException e) {
                    e.printStackTrace();
                    clients.remove(clientSocket); // 如果连接关闭，从列表中移除
                }
            }).start();
        }
    }

}

