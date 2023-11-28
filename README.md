# HTTP DoS Protection System

This repository contains a simple HTTP Denial-of-Service (DoS) protection system implemented in Java. The system consists of two components: a client and a server.

## Client (MyClient)

The client simulates multiple HTTP clients, each running in a separate thread, sending requests to the server with simulated client identifiers as query parameters. The client continues to send requests at random intervals until the user initiates a graceful shutdown.

### Usage

1. Enter the number of clients to simulate.
2. Each client runs in a separate thread, sending HTTP requests to the server.
3. Press Enter to stop the client gracefully.

## Server (MyServer)

The server listens for incoming HTTP requests and handles each request in a separate thread. It enforces a maximum request threshold per client within a 5-second time frame. The server responds with either a 200 (OK) or a 503 (Service Unavailable) status code based on the client's request frequency.

### Usage

1. The server runs in a separate thread.
2. Press Enter to stop the server gracefully.

## Dependencies

The code uses the Java `HttpClient` and `HttpServer` classes for sending and handling HTTP requests.

## How to Run

1. Compile and run the `MyServer` class to start the server.
2. Compile and run the `MyClient` class to simulate multiple HTTP clients.
