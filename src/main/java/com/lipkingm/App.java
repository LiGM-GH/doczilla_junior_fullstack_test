package com.lipkingm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;

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
            HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", 5151), 0);
            System.out.println("Created server");
            server.createContext("/fileserv", exchange -> {
                handleFileserv(exchange);
            });

            server.createContext("/js/main.js", exchange -> {
                handleJS(exchange);
            });

            server.createContext("/css/style.css", exchange -> {
                handleCss(exchange);
            });

            server.createContext("/weather", exchange -> {
                handleWeather(exchange);
            });

            System.out.println("Created context");
            server.setExecutor(null);
            server.start();
        } catch (IOException error) {
            System.out.println("Server internal error");
        }
    }

    static void handleWeather(HttpExchange exchange) throws IOException {
        System.out.println("Got Weather access");

        preventCors(exchange);

        URI uri = exchange.getRequestURI();
        System.out.println("Got URI of " + uri.toString());
        String[] values = uri.toString().split("\\?");
        System.out.println("Values are " + values[0]);
        String citypart = values[1];
        String city = citypart.replaceFirst("^city=", "");
        System.out.println("City is " + city);
    }

    static void preventCors(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
            exchange.sendResponseHeaders(204, -1);
        }
    }

    static void serveFile(OutputStream body_stream, String filename) throws IOException {
        File file = new File(filename).getAbsoluteFile();

        System.out.println("The absolute path for the " + filename + " is " + file.toString());

        try (FileInputStream fs = new FileInputStream(file)) {
            byte[] buffer = new byte[500];
            int count;

            while ((count = fs.read(buffer)) != -1) {
                body_stream.write(buffer, 0, count);
            }
        }
    }

    static void handleJS(HttpExchange exchange) throws IOException {
        System.out.println("Got JS access");
        exchange.sendResponseHeaders(200, 0);
        OutputStream body_stream = exchange.getResponseBody();

        serveFile(body_stream, "main.js");

        body_stream.close();
    }

    static void handleCss(HttpExchange exchange) throws IOException {
        System.out.println("Got Css access");
        exchange.sendResponseHeaders(200, 0);
        OutputStream body_stream = exchange.getResponseBody();

        serveFile(body_stream, "style.css");

        body_stream.close();
    }

    static void handleFileserv(HttpExchange exchange) throws IOException {
        preventCors(exchange);

        System.out.println("Entered HttpHandler");
        String method = exchange.getRequestMethod();
        URI uri = exchange.getRequestURI();

        String response = "This is the response";

        System.out.println("REQUEST URI IS " + uri.toString() + "\n\tWITH METHOD '" + method + "'");

        if (method.equals("GET")) {
            System.out.println("Request is GET");

            String relative = null;
            try {
                relative = (new URI("/fileserv")).relativize(uri).toString();
            } catch (URISyntaxException e) {
                throw new IOException(e.getMessage());
            }

            System.out.println("Relative is '" + relative + "'");

            if (relative.equals("")) {
                handleFileservMainPage(exchange);
                return;
            }

            if (relative.chars().allMatch(value -> ('0' <= value && value <= '9') || ('a' <= value && value <= 'f'))) {
                handleFileservGetPage(exchange, relative);
                return;
            }

            if (relative.equals("?")) {
                System.out.println("Exchange is POST-like GET");
                handleFileservUploadedPage(exchange);
                return;
            }

            exchange.sendResponseHeaders(200, response.length());
            OutputStream body_stream = exchange.getResponseBody();

            body_stream.write(response.getBytes());
            body_stream.close();
        }

        if (method.equals("POST")) {
            System.out.println("Exchange is POST");
            handleFileservUploadedPage(exchange);
        }
    }

    static void handleFileservMainPage(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(200, 0);

        try (OutputStream body_stream = exchange.getResponseBody()) {
            serveFile(body_stream, "main.html");
        }

        exchange.close();
    }

    static void handleFileservGetPage(HttpExchange exchange, String file_hash) throws IOException {
        exchange.sendResponseHeaders(200, 0);

        try (OutputStream body_stream = exchange.getResponseBody()) {
        }

        exchange.close();
    }

    static void handleFileservUploadedPage(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(200, 0);

        byte[] body = new byte[500];
        InputStream req = exchange.getRequestBody();

        File tempfile = File.createTempFile("temp_", ".temp").getAbsoluteFile();

        System.out.println("Created tempfile " + tempfile.toString());

        try (FileOutputStream out_stream = new FileOutputStream(tempfile)) {
            while (req.read(body) != -1) {
                out_stream.write(body);
            }
        }

        System.out.println("Written tempfile");

        File new_tempfile = new File("userfiles/" + tempfile.toPath().getFileName().toString());

        System.out.println("Trying to move tempfile to " + new_tempfile.getAbsoluteFile().toString());

        Files.move(tempfile.toPath(), new_tempfile.toPath());

        System.out.println("Moved tempfile");

        try (OutputStream body_stream = exchange.getResponseBody()) {
            body_stream.write(new_tempfile.toPath().getFileName().toString().getBytes());
        }

        System.out.println("Ended returning name of tempfile");

        exchange.close();
    }
}
