package org.jenkinsci.plugins.lambdatestrunner.jenkins.outputs;

import com.amazonaws.services.s3.AmazonS3;
import io.findify.s3mock.S3Mock;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public abstract class AmazonS3Test {

    protected static S3Mock s3Mock;
    protected static AmazonS3 amazonS3 = AmazonS3Factory.getInstance();

    private static int getMockedPort() {
        return Integer.parseInt(AmazonS3Factory.getMockedPort());
    }

    @BeforeClass
    public static void maybeSetupS3mock() {
        if (System.getProperty("mockS3") != null) {
            s3Mock = new S3Mock.Builder().withPort(getMockedPort()).withInMemoryBackend().build();
            s3Mock.start();
        }
    }

    @AfterClass
    public static void maybeCleanupS3Mock() {
        if (System.getProperty("mockS3") != null) {
            s3Mock.stop();
        }
    }
}
