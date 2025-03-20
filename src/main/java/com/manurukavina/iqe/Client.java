package com.manurukavina.iqe;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Handles HTTP requests for the server, processing GET and POST requests
 * and responding accordingly.
 */
public class Client implements HttpHandler {
    private static final String CONTENT_TYPE_TEXT = "text/plain; charset=UTF-8";
    private static final String CONTENT_TYPE_HTML = "text/html; charset=UTF-8";
    private static final String APP_HTML_RESOURCE = "app.html";

    private final Runtime runtime;
    private final Debug debugger;

    /**
     * Constructs a Client instance with a given runtime configuration.
     *
     * @param runtime The runtime instance containing configuration settings.
     */
    public Client(Runtime runtime) {
        this.runtime = runtime;
        this.debugger = new Debug(runtime);
    }

    /**
     * Handles incoming HTTP requests, directing them based on request method.
     *
     * @param exchange The HTTP exchange containing the request and response.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        try {
            switch (method) {
                case "POST" -> handlePostRequest(exchange);
                case "GET" -> handleGetRequest(exchange);
                default -> handleUnsupportedMethod(exchange, method);
            }
        } catch (Exception e) {
            handleServerError(exchange, e);
        } finally {
            exchange.close();
        }
    }

    /**
     * Handles HTTP POST requests by reading the request body and sending a
     * response.
     *
     * @param exchange The HTTP exchange object.
     * @throws IOException If an I/O error occurs.
     */
    private void handlePostRequest(HttpExchange exchange) throws IOException {
        String requestBody = readRequestBody(exchange.getRequestBody());
        debugger.info("Received POST request: " + requestBody);

        JSONObject jsonObject = new JSONObject(requestBody);
        String qValue = jsonObject.optString("Q", null); // Default to null if key "Q" is missing

        if (qValue == null || qValue.isEmpty()) {
            sendResponse(exchange, 400, CONTENT_TYPE_TEXT, "Error: Missing or empty 'Q' parameter.");
            return;
        }

        // Parse SQL using SQL.test()
        String returnedStuff = SQL.test(qValue);

        debugger.info("Extracted Q value: " + qValue);
        sendResponse(exchange, 200, CONTENT_TYPE_TEXT, returnedStuff); // Return parsed SQL
    }

    /**
     * Handles HTTP GET requests by serving an HTML response or rejecting if
     * disabled.
     *
     * @param exchange The HTTP exchange object.
     * @throws IOException If an I/O error occurs.
     */
    private void handleGetRequest(HttpExchange exchange) throws IOException {
        if (!runtime.isGetAllowed()) {
            sendResponse(exchange, 403, CONTENT_TYPE_TEXT, "GET requests not allowed");
            return;
        }
        String path = exchange.getRequestURI().getPath();
        if (path.equals("/favicon.ico")) {
            debugger.info("FAVION PART");

        }
        debugger.info("Received GET request from " + exchange.getRemoteAddress());
        String htmlResponse = loadAndProcessHtmlTemplate();
        sendResponse(exchange, 200, CONTENT_TYPE_HTML, htmlResponse);
    }

    /**
     * Handles unsupported HTTP methods by responding with a 405 status code.
     *
     * @param exchange The HTTP exchange object.
     * @param method   The unsupported HTTP method.
     * @throws IOException If an I/O error occurs.
     */
    private void handleUnsupportedMethod(HttpExchange exchange, String method) throws IOException {
        debugger.warn("Unsupported method: " + method);
        sendResponse(exchange, 405, CONTENT_TYPE_TEXT, "Method Not Allowed");
    }

    /**
     * Handles server errors by sending a 500 Internal Server Error response.
     *
     * @param exchange The HTTP exchange object.
     * @param e        The exception that occurred.
     * @throws IOException If an I/O error occurs.
     */
    private void handleServerError(HttpExchange exchange, Exception e) throws IOException {
        String errorMessage = "Error handling request: " + e.getMessage();
        debugger.error(errorMessage);
        System.err.println(errorMessage);
        e.printStackTrace();
        sendResponse(exchange, 500, CONTENT_TYPE_HTML,
                "<html><body><h1>500 Internal Server Error</h1><p>Internal server error occurred.</p></body></html>");
    }

    /**
     * Reads the request body from an input stream.
     *
     * @param inputStream The input stream of the request body.
     * @return The request body as a string.
     * @throws IOException If an I/O error occurs.
     */
    private String readRequestBody(InputStream inputStream) throws IOException {
        try (inputStream) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    /**
     * Loads the HTML template and replaces placeholders with runtime values.
     *
     * @return The processed HTML content.
     * @throws IOException If an error occurs while loading the template.
     */
    private String loadAndProcessHtmlTemplate() throws IOException {
        try (InputStream is = Client.class.getClassLoader().getResourceAsStream(APP_HTML_RESOURCE)) {
            if (is == null) {
                throw new IOException("Resource not found: " + APP_HTML_RESOURCE);
            }

            String html = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return html.replace("%port", String.valueOf(runtime.getPort()))
                    .replace("%version", String.valueOf(runtime.getVersion()))
                    .replace("%debug", String.valueOf(runtime.isDebugMode()))
                    .replace("%backlog", String.valueOf(runtime.getBacklog()))
                    .replace("%threads", String.valueOf(runtime.getThreadPoolSize()))
                    .replace("%startTime", "N/A");
        } catch (IOException e) {
            debugger.error("Error loading " + APP_HTML_RESOURCE + ": " + e.getMessage());
            throw e;
        }
    }

    /**
     * Sends an HTTP response with the given status code, content type, and response
     * body.
     *
     * @param exchange    The HTTP exchange object.
     * @param statusCode  The HTTP status code.
     * @param contentType The content type of the response.
     * @param response    The response body.
     * @throws IOException If an I/O error occurs.
     */
    private void sendResponse(HttpExchange exchange, int statusCode, String contentType, String response)
            throws IOException {
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
            os.flush();
        }
    }
}