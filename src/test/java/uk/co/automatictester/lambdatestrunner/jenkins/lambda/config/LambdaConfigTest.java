package uk.co.automatictester.lambdatestrunner.jenkins.lambda.config;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class LambdaConfigTest {

    @Test
    public void testLambdaConfig() {
        String name = "name";
        String region = "region";
        String bucket = "bucket";
        LambdaConfig lambdaConfig = new LambdaConfig(name, region, bucket);
        assertEquals(lambdaConfig.getFunctionName(), name);
        assertEquals(lambdaConfig.getRegion(), region);
        assertEquals(lambdaConfig.getS3Bucket(), bucket);
    }
}
