package com.manurukavina.iqe;

import java.io.*;
import java.net.*;
import com.sun.net.httpserver.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

/**
 * The Kernel class initializes and starts an HTTP server with configurable
 * options retrieved from the {@code Runtime} class.
 */
public class Kernel {

    /**
     * The main entry point of the application. It initializes the runtime
     * settings, sets up the HTTP server, and starts it with the specified
     * configuration.
     *
     * @param args Command-line arguments used to configure the server.
     */
    public static void main(String[] args) {
        Runtime runtime = new Runtime();
        runtime.create(args);
        Debug debugger = new Debug(runtime);

        try {
            // Create an HTTP server bound to the configured port and backlog size.
            HttpServer server = HttpServer.create(
                    new InetSocketAddress(runtime.getPort()),
                    runtime.getBacklog());
            
            // Set up a context handler for incoming HTTP requests.
            server.createContext("/", new Client(runtime));

            // Configure thread pool if specified, otherwise run synchronously.
            if (runtime.getThreadPoolSize() > 0) {
                ExecutorService executor = Executors.newFixedThreadPool(runtime.getThreadPoolSize());
                server.setExecutor(executor);
                debugger.info("Thread pool set with " + runtime.getThreadPoolSize() + " threads.");
            } else {
                server.setExecutor(null);
                debugger.info("Running in synchronous mode (no thread pool). ");
            }

            // Start the server and log its status.
            server.start();
            debugger.info("Server is listening on http://localhost:" + runtime.getPort());
            System.out.println("Server is listening on http://localhost:" + runtime.getPort());

        } catch (IOException e) {
            // Handle server startup failures.
            debugger.error("Could not start server on port " + runtime.getPort() + ": " + e.getMessage());
            System.err.println("Could not start server on port " + runtime.getPort() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
