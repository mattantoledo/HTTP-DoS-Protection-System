package http;

import java.util.*;

public class MyRequestsInfo {

    //time frame threshold (no more than 5 requests per 5 secs)
    private static final int REQUEST_LIMIT = 5;
    private static final long TIME_FRAME = 5000;

    //This is the main data structure: map between client id and its queue of "recent" requests
    private final Map<Integer, Queue<Long>> clientRequests = new HashMap<>();

    //Called from the server to add a request of a specific client id
    public synchronized int addRequest(int clientId) {

        //This is for the first time this client sends a request
        clientRequests.putIfAbsent(clientId, new LinkedList<>());

        //This is the main part where we add a request to the queue and receive the current number of request
        Queue<Long> requests = clientRequests.get(clientId);
        long currentTime = System.currentTimeMillis();
        requests.add(currentTime);

        // Remove requests outside the time frame
        while (!requests.isEmpty() && currentTime - requests.peek() > TIME_FRAME) {
            requests.poll();
        }

        //We check if the number is under the limit and return the response code as needed
        return (requests.size() <= REQUEST_LIMIT) ? 200 : 503;
    }

    //Used for printing all requests to see the program in action
    public synchronized void printAllRequests(int clientId) {
        Queue<Long> requests = clientRequests.get(clientId);
        System.out.println("All Requests for Client " + clientId + ": " + new LinkedList<>(requests));
    }
}
