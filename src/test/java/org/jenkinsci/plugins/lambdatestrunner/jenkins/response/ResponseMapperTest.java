package org.jenkinsci.plugins.lambdatestrunner.jenkins.response;

import groovy.json.JsonException;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class ResponseMapperTest {

    @Test
    public void testAsObject() {
        String responseBody = "{\"exitCode\": 0, \"output\": \"all good\", \"requestId\": \"b5fff7ea-085e-11e9-a665-db2069b28de5\", \"s3Prefix\": \"2018-12-23-16-00-41\"}";

        Response response = new Response();
        response.setExitCode(0);
        response.setOutput("all good");
        response.setRequestId("b5fff7ea-085e-11e9-a665-db2069b28de5");
        response.setS3Prefix("2018-12-23-16-00-41");

        Response mappedResponse = ResponseMapper.asObject(responseBody);
        assertEquals(mappedResponse, response);
    }

    @Test(expectedExceptions = JsonException.class)
    public void testAsObjectException() {
        String responseBody = "{\"exitCode\": 0";
        ResponseMapper.asObject(responseBody);
    }
}