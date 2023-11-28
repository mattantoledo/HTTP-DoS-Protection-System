package http;

import java.io.IOException;

import java.net.URI;
import java.net.http.*;

import java.util.Random;
import java.util.Scanner;

public class MyClient {

    private static final String BASE_URL = "http://localhost:8080";
    private static final Random random = new Random();

    //HttpClient is the class in Java I used for sending requests
    private static final HttpClient client = HttpClient.newHttpClient();

    //This is raised when a key is pressed to stop all active threads
    //Volatile means all threads can read safely
    //Writing is only on the main and happens once (false -> true)
    private static volatile boolean stopFlag = false;

    //Send HTTP request to a server with simulated HTTP client identifier as a query parameter
    public static void sendHttpRequest(int clientId) {

        String url = String.format("%s/?clientId=%d",BASE_URL, clientId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.format("Client %d, Status Code: %d\n", clientId, response.statusCode());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Sleep for some random time (0.5 - 2.5 seconds)
    private static void sleepRandom() {
        try {
            Thread.sleep(500 + random.nextInt(2000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //This is the function that is executed once by every client (different thread)
    private static void simulateClient(int numberOfClients) {
        int clientId;

        //Keep on sending requests with some id and wait, until signal arrives to stop.
        //When need to stop, previous requests will be finished well but not starting new ones.
        while (!stopFlag) {
            clientId = random.nextInt(numberOfClients) + 1;
            sendHttpRequest(clientId);
            sleepRandom();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        //The user enters the number of HTTP clients to simulate
        System.out.print("Enter the number of clients: ");
        int numberOfClients = scanner.nextInt();

        //Creating a thread for every client
        Thread[] threads = new Thread[numberOfClients];

        //Starting all threads
        for (int i = 0; i < numberOfClients; i++) {
            threads[i] = new Thread(() -> simulateClient(numberOfClients));
            threads[i].start();
        }

        //Enter is pressed to stop the client
        scanner.nextLine(); //This is from the scanner.nextInt()
        scanner.nextLine();

        //Raise the flag to stop all threads
        stopFlag = true;

        //Wait for all threads to finish well
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Print exit status
        System.out.println("Client is done");
    }
}