package org.jenkinsci.plugins.lambdatestrunner.jenkins.lambda.config;

public class LambdaConfigValidator {
    private LambdaConfigValidator() {
    }

    public static void validate(LambdaConfig lambdaConfig) {
        validateFunctionName(lambdaConfig.getFunctionName());
        validateRegion(lambdaConfig.getRegion());
        validateS3Bucket(lambdaConfig.getS3Bucket());
    }

    private static void validateFunctionName(String functionName) {
        if (functionName == null || functionName.equals("")) {
            throw new IllegalArgumentException("functionName not defined");
        }
    }

    private static void validateRegion(String region) {
        if (region == null || region.equals("")) {
            throw new IllegalArgumentException("region not defined");
        }
    }

    private static void validateS3Bucket(String s3Bucket) {
        if (s3Bucket == null || s3Bucket.equals("")) {
            throw new IllegalArgumentException("s3Bucket not defined");
        }
    }
}
