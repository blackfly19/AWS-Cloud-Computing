package com.amazonaws.webapp;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RegisterController {

    private static final int PORT = 8080; // use port 8080 for only local testing, need to replace as port 80 or 443 when place it onEC2
    private final RegisterService registerService = new RegisterService();

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/register", new RegisterHandler());
        server.start();
        System.out.println("Server started at http://localhost:" + PORT + "/register");
    }

    class RegisterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();

            if (method.equals("GET")) {
                InputStream is = getClass().getResourceAsStream("/register.html");
                byte[] response = toByteArray(is);
                exchange.getResponseHeaders().set("Content-Type", "text/html");
                exchange.sendResponseHeaders(200, response.length);
                exchange.getResponseBody().write(response);
                exchange.getResponseBody().close();

            } else if (method.equals("POST")) {
                String body = new String(toByteArray(exchange.getRequestBody()), StandardCharsets.UTF_8);
                Map<String, String> params = parseForm(body);

                String email    = params.get("email");
                String username = params.get("username");
                String password = params.get("password");

                String error = registerService.register(email, username, password);

                String response;
                if (error != null) {
                    response = error;
                    exchange.sendResponseHeaders(400, response.getBytes().length);
                } else {
                    response = "success";
                    exchange.sendResponseHeaders(200, response.getBytes().length);
                }

                exchange.getResponseBody().write(response.getBytes());
                exchange.getResponseBody().close();
            }
        }
    }

    private Map<String, String> parseForm(String body) throws UnsupportedEncodingException {
        Map<String, String> params = new HashMap<>();
        for (String pair : body.split("&")) {
            String[] kv = pair.split("=");
            if (kv.length == 2) {
                params.put(
                        URLDecoder.decode(kv[0], "UTF-8"),
                        URLDecoder.decode(kv[1], "UTF-8")
                );
            }
        }
        return params;
    }

    private byte[] toByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] chunk = new byte[4096];
        int n;
        while ((n = is.read(chunk)) != -1) {
            buffer.write(chunk, 0, n);
        }
        return buffer.toByteArray();
    }

    public static void main(String[] args) throws IOException {
        new RegisterController().start();
    }
}