package uk.co.automatictester.lambdatestrunner.jenkins.response;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class ResponseTest {

    @Test
    public void testResponse() {
        String requestId = "b5fff7ea-085e-11e9-a665-db2069b28de5";
        String output = " all not so good";
        int exitCode = 1;
        String s3Prefix = "2018-12-23-16-00-41";

        Response response = new Response();
        response.setRequestId(requestId);
        response.setOutput(output);
        response.setExitCode(exitCode);
        response.setS3Prefix(s3Prefix);

        assertEquals(response.getRequestId(), requestId);
        assertEquals(response.getOutput(), output);
        assertEquals(response.getExitCode(), exitCode);
        assertEquals(response.getS3Prefix(), s3Prefix);
    }
}
