import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        // You can use print statements as follows for debugging, they'll be visible when running tests.
        System.out.println("Logs from your program will appear here!");

        // Uncomment this block to pass the first stage
        //
        try {
            ServerSocket serverSocket = new ServerSocket(4221);
            //
            //   // Since the tester restarts your program quite often, setting SO_REUSEADDR
            //   // ensures that we don't run into 'Address already in use' errors
            //
            String httpResponse = "";
            serverSocket.setReuseAddress(true);
            System.out.println("wait new connection");
            Socket client = serverSocket.accept(); // Wait for connection from client.

            BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));


            String line = input.readLine();
            System.out.println("Request: " + line);
            String[] parseLine = line.split(" ");
            String metod = parseLine[0];
            String path = parseLine[1];
            String[] endPoint = path.split("/");


            if (Objects.equals(endPoint[1], "echo")) {
                httpResponse = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: " + endPoint[2].getBytes(StandardCharsets.UTF_8).length +"\r\n";

            } else {
                httpResponse = "HTTP/1.1 404 Not Found\r\n\r\n";
            }

            output.write(httpResponse);
            output.flush();

            System.out.println("accepted new connection");
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
