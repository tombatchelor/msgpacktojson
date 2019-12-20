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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.msgpack.core.ExtensionTypeHeader;
import org.msgpack.core.MessageFormat;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.jackson.dataformat.MessagePackFactory;
import org.msgpack.value.ValueType;

/**
 *
 * @author tombatchelor
 */
public class Handler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) {
        try {
            System.out.println(exchange.getRequestURI());
            InputStream is = exchange.getRequestBody();
            ObjectMapper msgPackObjectMapper = new ObjectMapper(new MessagePackFactory());
            List<Object> payload = msgPackObjectMapper.readValue(is, new TypeReference<List<Object>>() {});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String json = objectMapper.writeValueAsString(payload.get(0));
                sendJson(json );
            } catch (JsonProcessingException | InterruptedException e) {
                e.printStackTrace();
            }
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
                .header("Authorization", "Bearer 126338107931 JK6HJfeT73p9Ltz97HZghI5ii8VOVPoD")
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
