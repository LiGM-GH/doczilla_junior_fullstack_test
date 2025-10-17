package com.lipkingm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.exceptions.JedisException;

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
            server.createContext("/fileserv", exchange -> {
                handleFileserv(exchange);
            });

            server.createContext("/js/main.js", exchange -> {
                handleJS(exchange);
            });

            server.createContext("/css/style.css", exchange -> {
                handleCss(exchange);
            });

            JedisPooled jedis = new JedisPooled("0.0.0.0", 6379);
            server.createContext("/weather", exchange -> {
                handleWeather(exchange, jedis);
            });

            System.out.println("Created contexts");

            server.setExecutor(null);

            System.out.println("Created executor");

            server.start();
        } catch (IOException error) {
            System.out.println("Server internal error:" + error);
        }
    }

    static void handleWeather(HttpExchange exchange, JedisPooled jedis) throws IOException {
        System.out.println("Got Weather access");

        URI uri = exchange.getRequestURI();
        System.out.println("Got URI of " + uri.toString());
        String[] params = uri.getQuery().split("&");
        String city = null;
        for (String part : params) {
            String[] part_parts = part.split("=");
            if (part_parts[0].equals("city")) {
                city = part_parts[1];
            }
        }
        System.out.println("City is " + city);

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date(System.currentTimeMillis());

        boolean latest = false;

        String result = "Couldn't give you the reply";

        System.out.println("Got the latest flag: " + latest);

        String jedi = null;
        Date then = null;

        try {
            jedi = jedis.get("update-time-" + city);
            then = format.parse(jedi);

            System.out.println("JEDI COMPLETE");

            long now_minutes = now.getTime();
            long then_minutes = then.getTime();

            System.out.println("Now: " + now_minutes + " and then " + then_minutes);

            long diff = (now_minutes - then_minutes)
                    / 1000 // ms -> sec
                    / 60 // sec -> min
            ;

            if (diff > 15) {
                System.out.println("HOW?!");
            }

            latest = diff <= 15;

            System.out.println("New latest flag is: " + latest);
        } catch (JedisException e) {
            printError(e, "Jedi order has objections", "");
        } catch (ParseException e) {
            printError(e, "Parsing failed", "");
        } catch (Exception e) {
            printError(e, "Boxwood failed", "");
        }

        System.out.println("Got the latest flag: " + latest);

        if (latest) {
            System.out.println("Could find " + city + " in redis. Proceeding with cached reply");
            result = jedis.get(city);
        } else {
            System.out.println("Couldn't find " + city + " in redis. Proceeding with new reply");
            System.out.println("Trying to invoke GeoInfo");
            // That's GeoInfo JSON
            result = getGeoInfo(city);
            JsonElement parser = JsonParser.parseString(result);
            JsonObject obj = parser.getAsJsonObject().get("results").getAsJsonArray().get(0).getAsJsonObject();

            String lat = obj.get("latitude").getAsString();
            String lon = obj.get("longitude").getAsString();

            result = getWeatherInfo(lat, lon);

            try {
                jedis.set(city, result);
                jedis.set("update-time-" + city, format.format(new Date(System.currentTimeMillis())));
            } catch (Exception e) {
                printError(e, "Jedi order has objections", "");
            }
        }

        System.out.println("Writing headers");
        exchange.sendResponseHeaders(200, result.getBytes().length);
        System.out.println("Writing body");
        OutputStream body_stream = exchange.getResponseBody();
        body_stream.write(result.getBytes());
        body_stream.close();
        System.out.println("Closed body");
        exchange.close();
        System.out.println("Closed request");
    }

    static String getWeatherInfo(String lat, String lon) throws IOException {
        System.out.println("Invoked WeatherInfo");

        Response resp = null;

        try {
            resp = Request
                    .Get("https://api.open-meteo.com/v1/forecast?latitude=" + lat + "&longitude=" + lon
                            + "&hourly=temperature_2m")
                    .connectTimeout(1000).socketTimeout(1000).execute();
        } catch (ClientProtocolException e) {
            printError(e, "Client couldn't protocol", "");
        }

        return resp.returnContent().asString();
    }

    static String getGeoInfo(String city) throws IOException {
        System.out.println("Invoked GeoInfo");

        Response resp = null;

        try {
            resp = Request.Get("https://geocoding-api.open-meteo.com/v1/search?name=" + city)
                    .connectTimeout(1000).socketTimeout(1000).execute();
        } catch (ClientProtocolException e) {
            printError(e, "Client couldn't protocol", "");
            throw new IOException(e);
        }

        Content content = resp.returnContent();

        return content.asString();
    }

    static void printError(Exception e, String lhs, String rhs) {
        StringWriter str = new StringWriter();
        PrintWriter writer = new PrintWriter(str);
        e.printStackTrace(writer);

        System.out.println(
                "\033[31m" + lhs + ":\n" + str.toString() + "\n\n" + rhs + "\033[0m");
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
