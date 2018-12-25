package uk.co.automatictester.lambdatestrunner.jenkins.request;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class RequestMapper {

    private RequestMapper() {
    }

    public static String asString(Request request) {
        ObjectMapper objectMapper = new ObjectMapper();
        Writer stringWriter = new StringWriter();
        try {
            objectMapper.writeValue(stringWriter, request);
        } catch (IOException e) {
            throw new RuntimeException("Error generating JSON: " + e.getMessage());
        }
        return stringWriter.toString();
    }
}
