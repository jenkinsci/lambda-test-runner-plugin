package org.jenkinsci.plugins.lambdatestrunner.jenkins.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.json.JsonException;

import java.io.IOException;

public class ErrorResponseMapper {

    private ErrorResponseMapper() {
    }

    public static ErrorResponse asObject(String responseBody) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(responseBody, ErrorResponse.class);
        } catch (IOException e) {
            throw new JsonException("Error reading JSON: " + e.getMessage());
        }
    }
}
