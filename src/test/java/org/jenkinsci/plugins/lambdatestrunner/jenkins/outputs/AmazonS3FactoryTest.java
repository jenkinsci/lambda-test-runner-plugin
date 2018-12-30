package org.jenkinsci.plugins.lambdatestrunner.jenkins.outputs;

import com.amazonaws.services.s3.AmazonS3;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

public class AmazonS3FactoryTest {

    @Test
    public void testGetInstance() {
        AmazonS3 amazonS3 = AmazonS3Factory.getInstance();
        assertNotNull(amazonS3);
    }
}
