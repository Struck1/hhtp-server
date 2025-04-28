import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class HttpRequest {
    public String metod;
    public String path;
    public String version;
    public String headerValue;
    public String endPoint;

    public static HttpRequest parse(BufferedReader in) throws IOException {
        ArrayList<String> parseIn = new ArrayList<>();
        String requestLine;

        while ((requestLine = in.readLine()) != null && !requestLine.isEmpty()) {
            parseIn.add(requestLine);
        }

        String[] part = parseIn.getFirst().split(" ");
        if (part.length < 3) return null;

        HttpRequest request = new HttpRequest();
        request.metod = part[0];
        request.path = part[1];
        request.version = part[2];

        String agentValue = findUserAgentValue(parseIn);

        if(agentValue != null) {request.headerValue = agentValue;}

        String[] endPoint = request.path.split("/");
        if(endPoint.length > 2) {
            request.endPoint = endPoint[2];
        }

        return request;
    }

    public static String findUserAgentValue(ArrayList<String> list) {
        for (String header : list) {
            if (header.startsWith("User-Agent:")) {
                 return header.split(":")[1].trim();
            }
        }
        return null;
    }

}
