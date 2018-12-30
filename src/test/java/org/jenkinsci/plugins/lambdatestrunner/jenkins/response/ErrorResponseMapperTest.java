package org.jenkinsci.plugins.lambdatestrunner.jenkins.response;

import groovy.json.JsonException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ErrorResponseMapperTest {

    @Test
    public void testAsObject() {
        String responseBody = "{\"errorMessage\":\"2018-12-30T12:33:41.375Z 231b043e-0c2f-11e9-9c99-055d41774281 Task timed out after 1.00 seconds\"}";

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorMessage("2018-12-30T12:33:41.375Z 231b043e-0c2f-11e9-9c99-055d41774281 Task timed out after 1.00 seconds");

        ErrorResponse mappedResponse = ErrorResponseMapper.asObject(responseBody);
        assertEquals(mappedResponse, errorResponse);
    }

    @Test(expected = JsonException.class)
    public void testAsObjectException() {
        String errorResponseBody = "{\"errorMessage\": ";
        ErrorResponseMapper.asObject(errorResponseBody);
    }
}
