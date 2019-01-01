package org.jenkinsci.plugins.lambdatestrunner.jenkins.response;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class ResponseMapper {

    private ResponseMapper() {
    }

    public static Response asObject(String responseBody) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(responseBody, Response.class);
    }
}
