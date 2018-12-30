package org.jenkinsci.plugins.lambdatestrunner.jenkins.response;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ErrorResponseTest {

    @Test
    public void testResponse() {
        String errorMessage = "2018-12-30T12:33:41.375Z 231b043e-0c2f-11e9-9c99-055d41774281 Task timed out after 1.00 seconds";

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorMessage(errorMessage);

        assertEquals(errorResponse.getErrorMessage(), errorMessage);
    }
}
