package org.jenkinsci.plugins.lambdatestrunner.jenkins.lambda;

import com.amazonaws.services.lambda.model.InvokeRequest;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class InvokeRequestFactoryTest {

    @Test
    public void testGetInstance() {
        String functionName = "MyLambda";
        String payload = "{}";
        InvokeRequest invokeRequest = InvokeRequestFactory.getInstance(functionName, payload);
        assertEquals(invokeRequest.getFunctionName(), functionName);
        assertEquals(new String(invokeRequest.getPayload().array()), payload);
    }
}
