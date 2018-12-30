package org.jenkinsci.plugins.lambdatestrunner.jenkins.lambda;

import com.amazonaws.services.lambda.AWSLambda;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class AWSLambdaFactoryTest {

    @Test
    public void testGetInstance() {
        AWSLambda lambda = AWSLambdaFactory.getInstance("eu-west-1");
        assertNotNull(lambda);
    }
}
