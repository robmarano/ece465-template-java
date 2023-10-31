package ece465.api.app;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;


/**
 * See https://github.com/piczmar/pure-java-rest-api/blob/step-1/src/main/java/com/consulner/api/Application.java
 *
 */
public class App {
    public static void main(String[] args) throws IOException {
        int serverPort = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);
        server.createContext("/api/hello", (exchange -> {
            String respText = "Hello!";
            exchange.sendResponseHeaders(200, respText.getBytes().length);
            OutputStream output = exchange.getResponseBody();
            output.write(respText.getBytes());
            output.flush();
            exchange.close();
        }));
        server.setExecutor(null); // creates a default executor
        server.start();
    }
}
// https://www.google.com/search?q=URL&rlz=1C5GCEM_enUS1048US1048&oq=URL&gs_lcrp=EgZjaHJvbWUyBggAEEUYOTIGCAEQRRhBMgYIAhBFGEEyBggDEEUYPDIGCAQQRRg8MgYIBRBFGDzSAQgxMzY2ajBqNKgCALACAA&sourceid=chrome&ie=UTF-8