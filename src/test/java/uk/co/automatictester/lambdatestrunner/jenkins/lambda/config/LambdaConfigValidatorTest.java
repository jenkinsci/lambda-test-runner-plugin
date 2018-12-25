package uk.co.automatictester.lambdatestrunner.jenkins.lambda.config;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class LambdaConfigValidatorTest {

    @Test
    public void testHappyPath() {
        LambdaConfig lambdaConfig = new LambdaConfig("name", "region");
        LambdaConfigValidator.validate(lambdaConfig);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNameNull() {
        LambdaConfig lambdaConfig = new LambdaConfig(null, "region");
        LambdaConfigValidator.validate(lambdaConfig);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNameEmpty() {
        LambdaConfig lambdaConfig = new LambdaConfig("", "region");
        LambdaConfigValidator.validate(lambdaConfig);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testRegionNull() {
        LambdaConfig lambdaConfig = new LambdaConfig("name", null);
        LambdaConfigValidator.validate(lambdaConfig);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testRegionEmpty() {
        LambdaConfig lambdaConfig = new LambdaConfig("name", "");
        LambdaConfigValidator.validate(lambdaConfig);
    }
}
