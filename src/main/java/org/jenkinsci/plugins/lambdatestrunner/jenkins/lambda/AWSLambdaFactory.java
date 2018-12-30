package org.jenkinsci.plugins.lambdatestrunner.jenkins.lambda;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;

public class AWSLambdaFactory {

    private AWSLambdaFactory() {
    }

    public static AWSLambda getInstance(String region) {
        return AWSLambdaClientBuilder.standard()
                .withRegion(region)
                .withClientConfiguration(clientConfiguration())
                .build();
    }

    private static ClientConfiguration clientConfiguration() {
        int timeout = (15 * 60 * 1000);
        return new ClientConfiguration()
                .withConnectionTimeout(timeout)
                .withRequestTimeout(timeout)
                .withSocketTimeout(timeout)
                .withClientExecutionTimeout(timeout);
    }
}
