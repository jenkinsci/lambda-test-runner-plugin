package org.jenkinsci.plugins.lambdatestrunner.jenkins.lambda.config;

import org.testng.annotations.Test;

public class LambdaConfigValidatorTest {

    @Test
    public void testHappyPath() {
        LambdaConfig lambdaConfig = new LambdaConfig("name", "region", "bucket");
        LambdaConfigValidator.validate(lambdaConfig);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNameNull() {
        LambdaConfig lambdaConfig = new LambdaConfig(null, "region", "bucket");
        LambdaConfigValidator.validate(lambdaConfig);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNameEmpty() {
        LambdaConfig lambdaConfig = new LambdaConfig("", "region", "bucket");
        LambdaConfigValidator.validate(lambdaConfig);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testRegionNull() {
        LambdaConfig lambdaConfig = new LambdaConfig("name", null, "bucket");
        LambdaConfigValidator.validate(lambdaConfig);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testRegionEmpty() {
        LambdaConfig lambdaConfig = new LambdaConfig("name", "", "bucket");
        LambdaConfigValidator.validate(lambdaConfig);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testBucketNull() {
        LambdaConfig lambdaConfig = new LambdaConfig("name", "region", null);
        LambdaConfigValidator.validate(lambdaConfig);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testBucketEmpty() {
        LambdaConfig lambdaConfig = new LambdaConfig("name", "region", "");
        LambdaConfigValidator.validate(lambdaConfig);
    }
}
