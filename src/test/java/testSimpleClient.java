import java.io.*;
import java.net.*;
public class testSimpleClient {
    public static void main(String[] args) throws IOException {
        String host = "10.135.18.202";
        int port = 60000;

        Socket socket = new Socket(host, port);
        System.out.println("连接到服务器成功");

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        String userInput;
        while (true) {
            String response = in.readLine();System.out.println("服务器回复: " + response);
            userInput = console.readLine();

            out.println(userInput);


        }

        //socket.close();
    }

}
