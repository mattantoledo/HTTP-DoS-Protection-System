package http;

import com.sun.net.httpserver.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

public class MyServer {

    private static final int PORT = 8080;

    //HttpServer is the class in Java I used for sending requests
    //No external dependency - part of jdk: com.sun.net.httpserver
    private HttpServer server;

    //Starts listening for incoming HTTP requests
    private void runServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", new MyHandler());
        server.start();
    }

    //stop the server, this blocks for 1 seconds or until all handlers finish (which comes first)
    //Thought about if to write 0 or 1 or other solution
    private void stopServer() {
        if (server != null) {
            server.stop(1);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MyServer server = new MyServer();

        //The server runs in another thread so that the program can listen to the enter press for finish
        Thread serverThread = new Thread(() -> {
            try {
                server.runServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        //running the server thread
        serverThread.start();

        //Enter is pressed to stop the server
        System.out.println("Press Enter to stop the server.");
        scanner.nextLine();

        //Stop the server
        server.stopServer();

        System.out.println("Server is done");
    }


    //This is a handler invoked every time the server receives a new request
    //The field requestsData is "global" and is created once, while handle(exchange) is called every time from a different thread
    static class MyHandler implements HttpHandler {

        //This is the data structure holding the data of previous requests
        private final MyRequestsInfo requestsData = new MyRequestsInfo();

        public void handle(HttpExchange exchange) throws IOException {

            //Extract the client id from the url of the request
            int clientId = extractClientId(exchange.getRequestURI().toString());

            //Add the request to our structure and get the response code
            int responseCode = requestsData.addRequest(clientId);

            //I used this for testing
            requestsData.printAllRequests(clientId);
            System.out.format("client id = %d, response code = %d\n", clientId, responseCode);

            //send the response to the client and close
            exchange.sendResponseHeaders(responseCode, 0);
            exchange.close();
        }

        private int extractClientId(String requestUrl) {

            int clientId = -1;

            /*
            requestUrl = "/?clientId=3"
            keyValue = ["/?clientId", "3"]
            keyValue[1] = "3"
             */
            String[] keyValue = requestUrl.split("=");

            try {
                clientId = Integer.parseInt(keyValue[1]);
            } catch (NumberFormatException e) {
                System.out.println("Error in extracting the client id");
            }
            return clientId;
        }
    }
}
