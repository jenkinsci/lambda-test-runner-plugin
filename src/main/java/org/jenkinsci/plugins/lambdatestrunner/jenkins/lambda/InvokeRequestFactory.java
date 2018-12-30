package org.jenkinsci.plugins.lambdatestrunner.jenkins.lambda;

import com.amazonaws.services.lambda.model.InvokeRequest;

public class InvokeRequestFactory {

    private InvokeRequestFactory() {
    }

    public static InvokeRequest getInstance(String functionName, String payload) {
        return new InvokeRequest()
                .withFunctionName(functionName)
                .withPayload(payload);
    }
}
