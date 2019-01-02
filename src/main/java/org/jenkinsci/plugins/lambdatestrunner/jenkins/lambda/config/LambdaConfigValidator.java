package org.jenkinsci.plugins.lambdatestrunner.jenkins.lambda.config;

public class LambdaConfigValidator {
    private LambdaConfigValidator() {
    }

    public static void validate(LambdaConfig lambdaConfig) {
        validateFunctionName(lambdaConfig.getFunctionName());
        validateRegion(lambdaConfig.getRegion());
        validateS3Bucket(lambdaConfig.getS3Bucket());
    }

    public static boolean isFunctionNameValid(String functionName) {
        return !(functionName == null || functionName.equals(""));
    }

    public static boolean isRegionValid(String region) {
        return !(region == null || region.equals(""));
    }

    public static boolean isS3BucketValid(String s3Bucket) {
        return !(s3Bucket == null || s3Bucket.equals(""));
    }

    private static void validateFunctionName(String functionName) {
        if (!isFunctionNameValid(functionName)) {
            throw new IllegalArgumentException("functionName not defined");
        }
    }

    private static void validateRegion(String region) {
        if (!isRegionValid(region)) {
            throw new IllegalArgumentException("region not defined");
        }
    }

    private static void validateS3Bucket(String s3Bucket) {
        if (!isS3BucketValid(s3Bucket)) {
            throw new IllegalArgumentException("s3Bucket not defined");
        }
    }
}
