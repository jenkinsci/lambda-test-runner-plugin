package org.jenkinsci.plugins.lambdatestrunner.jenkins.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.json.JsonException;

import java.io.IOException;

public class ResponseMapper {

    private ResponseMapper() {
    }

    public static Response asObject(String responseBody) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(responseBody, Response.class);
        } catch (IOException e) {
            throw new JsonException("Error reading JSON: " + e.getMessage());
        }
    }
}
