package uk.co.automatictester.lambdatestrunner.jenkins.lambda;

import com.amazonaws.services.lambda.AWSLambda;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class AWSLambdaFactoryTest {

    @Test
    public void testGetInstance() {
        AWSLambda lambda = AWSLambdaFactory.getInstance("eu-west-1");
        assertNotNull(lambda);
    }
}
