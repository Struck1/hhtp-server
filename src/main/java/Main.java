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
            serverSocket.setReuseAddress(true);

            while (true) {
                System.out.println("wait new connection");
                Socket client = serverSocket.accept(); // Wait for connection from client.

                new Thread(() -> {
                    try {
                        BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

                        ArrayList<String> headers = new ArrayList<>();
                        String line;


                        while ((line = input.readLine()) != null && !line.isEmpty()) {
                            headers.add(line);
                        }

                        System.out.println("Request: " + headers.get(0));
                        String[] parseLine = headers.get(0).split(" ");
                        String userAgent = null;


                        for (String header : headers) {
                            if (header.startsWith("User-Agent:")) {
                                userAgent = header.split(":")[1].trim();
                            }
                        }

                        String path = parseLine[1];
                        String[] endPoint = path.split("/");

                        String httpResponse;

                        if (Objects.equals(path, "/")) {
                            httpResponse = "HTTP/1.1 200 OK\r\n\r\n";
                        } else if (endPoint.length > 2 || Objects.equals(path, "/user-agent")) {
                            if (Objects.equals(endPoint[1], "echo")) {
                                String body = endPoint[2];
                                int contentLength = body.getBytes(StandardCharsets.UTF_8).length;
                                httpResponse = "HTTP/1.1 200 OK\r\n" +
                                        "Content-Type: text/plain\r\n" +
                                        "Content-Length: " + contentLength + "\r\n" +
                                        "\r\n" +
                                        body;
                            } else if (userAgent != null && Objects.equals(endPoint[1], "user-agent")) {
                                int contentLength = userAgent.getBytes(StandardCharsets.UTF_8).length;
                                httpResponse = "HTTP/1.1 200 OK\r\n" +
                                        "Content-Type: text/plain\r\n" +
                                        "Content-Length: " + contentLength + "\r\n" +
                                        "\r\n" +
                                        userAgent;
                            } else {
                                httpResponse = "HTTP/1.1 404 Not Found\r\n\r\n";
                            }
                        } else {
                            httpResponse = "HTTP/1.1 404 Not Found\r\n\r\n";
                        }

                        // Yanıtı istemciye yazma
                        output.write(httpResponse);
                        output.flush();

                        System.out.println("Response sent to client.");

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            client.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                ).start();

            }

        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
