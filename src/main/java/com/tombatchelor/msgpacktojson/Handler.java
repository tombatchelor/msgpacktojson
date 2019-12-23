/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tombatchelor.msgpacktojson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import org.msgpack.jackson.dataformat.MessagePackFactory;

/**
 *
 * @author tombatchelor
 */
public class Handler implements HttpHandler {

    public static final String CUSTOMER_ID_KEY = "CUSTOMER_ID";
    public static final String AUTH_TOKEN_KEY = "AUTH_TOKEN";
    
    public static String bearerString;
    
    static {
        String customerID = System.getenv().get(CUSTOMER_ID_KEY);
        String authToken = System.getenv().get(AUTH_TOKEN_KEY);
        bearerString = "Bearer " + customerID + " " + authToken;
    }
            
    @Override
    public void handle(HttpExchange exchange) {
        try {
            System.out.println(exchange.getRequestURI());
            InputStream is = exchange.getRequestBody();
            ObjectMapper msgPackObjectMapper = new ObjectMapper(new MessagePackFactory());
            List<Object> payload = msgPackObjectMapper.readValue(is, new TypeReference<List<Object>>() {});
            is.close();
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String json = objectMapper.writeValueAsString(payload.get(0));
                sendJson(json );
            } catch (JsonProcessingException | InterruptedException e) {
                e.printStackTrace();
            }
            
            
            String body = "{\"OK\":\"OK\"}";
            exchange.sendResponseHeaders(200, body.length() == 0 ? -1 : body.length());
            exchange.getResponseBody().write(body.getBytes());
            exchange.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void sendJson(String json) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://collect.observe-staging.com/v1/observations/datadog"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Authorization", bearerString)
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
