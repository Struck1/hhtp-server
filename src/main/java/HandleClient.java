import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class HandleClient {
    public static void handleClient(Socket socket, String[] args) throws IOException {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            HttpRequest request = HttpRequest.parse(input);
            if (request == null) return;
            String response = createResponse(request, args);
            output.write(response);
            output.flush();
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }

    private static String createResponse(HttpRequest request, String[] args) throws IOException {
        if (Objects.equals(request.path, "/")) {
            return "HTTP/1.1 200 OK\r\n\r\n";
        } else if (Objects.equals(request.path, "/user-agent")) {
            int contentLength = request.headerValue.getBytes(StandardCharsets.UTF_8).length;
            return "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: text/plain\r\n" +
                    "Content-Length: " + contentLength + "\r\n" +
                    "\r\n" +
                    request.headerValue;

        } else if (request.path.startsWith("/echo/")) {
            int contentLength = request.endPoint.getBytes(StandardCharsets.UTF_8).length;
            return "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: text/plain\r\n" +
                    "Content-Length: " + contentLength + "\r\n" +
                    "\r\n" +
                    request.endPoint;
        } else if (request.path.startsWith("/files/")) {
            String[] path = request.path.split("/");
            if (path.length >= 3) {
                File file = new File(args[1], path[2]);
                try {
                    if (file.exists()) {
                        byte[] bytes = Files.readAllBytes(file.toPath());
                        String content = new String(bytes);
                        return "HTTP/1.1 200 OK\r\nContent-Type: application/octet-stream\r\nContent-Length: " + bytes.length + "\r\n\r\n" + content;
                    } else {
                        return "HTTP/1.1 404 Not Found\r\n\r\n";
                    }
                } catch (IOException e) {
                    System.err.println("Read File Error: " + e.getMessage());
                }
            }

        }
        return "HTTP/1.1 404 Not Found\r\n\r\n";
    }
}
