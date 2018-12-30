package org.jenkinsci.plugins.lambdatestrunner.jenkins.lambda.config;

import java.io.Serializable;

public class LambdaConfig implements Serializable {
    private final String functionName;
    private final String region;
    private final String s3Bucket;

    public LambdaConfig(String functionName, String region, String s3Bucket) {
        this.functionName = functionName;
        this.region = region;
        this.s3Bucket = s3Bucket;
    }

    public String getFunctionName() {
        return functionName;
    }

    public String getRegion() {
        return region;
    }

    public String getS3Bucket() {
        return s3Bucket;
    }
}
