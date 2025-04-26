import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

            ArrayList<String> headers = new ArrayList<>();
            String line;

            while ((line = input.readLine()) != null && !line.isEmpty()) {
                headers.add(line);
            }

            System.out.println("Request: " + headers.getFirst());
            String[] parseLine = headers.getFirst().split(" ");
            if(headers.size() < 3) return;
            String userAgent = headers.get(3);
            String[] parseUserAgent = userAgent.split(":");
            String metod = parseLine[0];
            String path = parseLine[1];
            String[] endPoint = path.split("/");


            if(Objects.equals(path, "/")){
                httpResponse = "HTTP/1.1 200 OK\r\n\r\n";
            } else if (endPoint.length > 2  || Objects.equals(path, "/user-agent")) {
                if(Objects.equals(endPoint[1], "echo")) {
                    String body = endPoint[2];
                    int contentLength = body.getBytes(StandardCharsets.UTF_8).length;
                    httpResponse = "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: text/plain\r\n" +
                            "Content-Length: " + contentLength + "\r\n" +
                            "\r\n" +
                            body;
                }
                if(parseUserAgent.length > 0 && Objects.equals(endPoint[1], "user-agent")) {
                    String body = parseUserAgent[1].trim();
                    int contentLength = body.getBytes(StandardCharsets.UTF_8).length;
                    httpResponse = "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: text/plain\r\n" +
                            "Content-Length: " + contentLength + "\r\n" +
                            "\r\n" +
                            body;
                }

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
