package com.lipkingm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        try {
            System.out.println("Creating server");
            HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 5151), 0);
            System.out.println("Created server");
            server.createContext("/fileserv/", exchange -> {
                handleFileserv(exchange);
            });

            server.createContext("/js/main.js", exchange -> {
                handleJS(exchange);
            });

            server.createContext("/css/style.css", exchange -> {
                handleCss(exchange);
            });

            server.createContext("/upload", exchange -> {
                handleUpload(exchange);
            });

            System.out.println("Created context");
            server.setExecutor(null);
            server.start();
        } catch (IOException error) {
            System.out.println("Server internal error");
        }
    }

    static void handleUpload(HttpExchange exchange) throws IOException {
        System.out.println("Got upload access");
    }

    static void handleJS(HttpExchange exchange) throws IOException {
        System.out.println("Got JS access");
        exchange.sendResponseHeaders(200, 0);
        OutputStream body_stream = exchange.getResponseBody();
        File file = new File("./main.js").getAbsoluteFile();
        System.out.println("The absolute path for the main.js is " + file.toString());

        try (FileInputStream fs = new FileInputStream(file)) {
            byte[] buffer = new byte[500];
            int count;
            while ((count = fs.read(buffer)) != -1) {
                body_stream.write(buffer, 0, count);
            }
        }

        body_stream.close();
    }

    static void handleCss(HttpExchange exchange) throws IOException {
        System.out.println("Got Css access");
        exchange.sendResponseHeaders(200, 0);
        OutputStream body_stream = exchange.getResponseBody();

        System.out.println("Relative was equal");
        File file = new File("./style.css").getAbsoluteFile();
        System.out.println("The absolute path for the style.css is " + file.toString());

        try (FileInputStream fs = new FileInputStream(file)) {
            byte[] buffer = new byte[500];
            int count;
            while ((count = fs.read(buffer)) != -1) {
                body_stream.write(buffer, 0, count);
            }
        }

        body_stream.close();
    }

    static void handleFileserv(HttpExchange exchange) throws IOException {
        System.out.println("Entered HttpHandler");
        URI uri = exchange.getRequestURI();
        String method = exchange.getRequestMethod();

        String response = "This is the response";

        System.out.println("REQUEST URI IS " + uri.toString() + "\n\tWITH METHOD '" + method + "'");

        if (method.equals("GET")) {
            System.out.println("Request is GET");

            String relative = null;
            try {
                relative = (new URI("/fileserv/")).relativize(uri).toString();
            } catch (URISyntaxException e) {
            }

            System.out.println("Relative is '" + relative + "'");
            if (relative.equals("")) {
                exchange.sendResponseHeaders(200, 0);
                OutputStream body_stream = exchange.getResponseBody();

                System.out.println("Relative was equal");
                File file = new File("./main.html").getAbsoluteFile();
                System.out.println("The absolute path for the main.html is " + file.toString());
                try (FileInputStream fs = new FileInputStream(file)) {
                    byte[] buffer = new byte[500];
                    int count;
                    while ((count = fs.read(buffer)) != -1) {
                        body_stream.write(buffer, 0, count);
                    }
                }
                body_stream.close();
            }

            exchange.sendResponseHeaders(200, response.length());
            OutputStream body_stream = exchange.getResponseBody();

            body_stream.write(response.getBytes());
            body_stream.close();
        }
    }
}
