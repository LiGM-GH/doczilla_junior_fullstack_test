package com.lipkingm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.xml.ws.spi.http.HttpExchange;

import com.sun.net.httpserver.*;

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
            HttpContext context = server.createContext("/fileserv/", exchange -> {
                System.out.println("Entered HttpHandler");
                URI uri = exchange.getRequestURI();
                String method = exchange.getRequestMethod();

                String response = "This is the response";

                System.out.println("REQUEST URI IS " + uri.toString() + "\n\tWITH METHOD '" + method + "'");

                exchange.sendResponseHeaders(200, response.length());
                InputStream stream = exchange.getRequestBody();

                if (method.equals("GET")) {
                    System.out.println("Request is GET");

                    OutputStream body_stream = exchange.getResponseBody();
                    body_stream.write(response.getBytes());
                    body_stream.close();
                }
            });
            System.out.println("Created context");
            server.setExecutor(null);
            server.start();
        } catch (IOException error) {
            System.out.println("Server couldn't be created");
        }
    }
}
