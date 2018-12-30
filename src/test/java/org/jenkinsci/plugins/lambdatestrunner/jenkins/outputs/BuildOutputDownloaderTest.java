package org.jenkinsci.plugins.lambdatestrunner.jenkins.outputs;

import com.amazonaws.services.s3.AmazonS3;
import io.findify.s3mock.S3Mock;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertTrue;

public class BuildOutputDownloaderTest {

    private static final S3Mock s3Mock = new S3Mock.Builder().withPort(8001).withInMemoryBackend().build();
    private static final AmazonS3 amazonS3 = AmazonS3Factory.getInstance();
    private static final String bucket = "automatictester.co.uk-lambda-test-runner-build-outputs";
    private static final String prefix = getPrefix();
    private static final String destinationDir = "downloads";

    @BeforeClass
    public static void setup() {
        if (System.getProperty("mockS3") != null) {
            startS3Mock();
        }
        maybeCreateBucket();
        uploadFile("src/test/resources/build-output-extractor/test-execution.log", "test-execution.log");
        uploadFile("src/test/resources/build-output-extractor/target-dir/failsafe-reports.zip", "target-dir/failsafe-reports.zip");
        uploadFile("src/test/resources/build-output-extractor/target-dir/surefire-reports.zip", "target-dir/surefire-reports.zip");
    }

    @Test
    public void testDownload() {
        BuildOutputDownloader downloader = new BuildOutputDownloader(bucket, prefix, destinationDir);
        downloader.download();

        assertTrue(Files.exists(Paths.get(destinationDir + "/" + prefix + "/" + "test-execution.log")));
        assertTrue(Files.exists(Paths.get(destinationDir + "/" + prefix + "/" + "target-dir/failsafe-reports.zip")));
        assertTrue(Files.exists(Paths.get(destinationDir + "/" + prefix + "/" + "target-dir/surefire-reports.zip")));
    }

    @AfterClass
    public static void teardown() throws IOException {
        FileUtils.deleteDirectory(new File(destinationDir));
        if (System.getProperty("mockS3") != null) {
            s3Mock.stop();
        }
    }

    private static void startS3Mock() {
        s3Mock.start();
    }

    private static void maybeCreateBucket() {
        if (!amazonS3.doesBucketExistV2(bucket)) {
            amazonS3.createBucket(bucket);
        }
    }

    private static String getPrefix() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        return f.format(now);
    }

    private static void uploadFile(String file, String key) {
        amazonS3.putObject(bucket, prefix + "/" + key, new File(file));
    }
}
