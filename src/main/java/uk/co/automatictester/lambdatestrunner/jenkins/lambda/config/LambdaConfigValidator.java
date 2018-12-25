package uk.co.automatictester.lambdatestrunner.jenkins.lambda.config;

public class LambdaConfigValidator {
    private LambdaConfigValidator() {
    }

    public static void validate(LambdaConfig lambdaConfig) {
        validateFunctionName(lambdaConfig.getFunctionName());
        validateRegion(lambdaConfig.getRegion());
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
}
