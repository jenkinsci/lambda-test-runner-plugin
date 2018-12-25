package uk.co.automatictester.lambdatestrunner.jenkins.lambda.config;

import java.io.Serializable;

public class LambdaConfig implements Serializable {
    private final String functionName;
    private final String region;

    public LambdaConfig(String functionName, String region) {
        this.functionName = functionName;
        this.region = region;
    }

    public String getFunctionName() {
        return functionName;
    }

    public String getRegion() {
        return region;
    }
}
