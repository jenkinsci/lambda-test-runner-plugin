package org.jenkinsci.plugins.lambdatestrunner.jenkins.outputs;

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

public class BuildOutputDownloaderTest extends AmazonS3Test {

    private static final String bucket = "automatictester.co.uk-lambda-test-runner-build-outputs";
    private static final String prefix = getPrefix();
    private static final String destinationDir = "downloads";

    @BeforeClass
    public static void setup() {
        if (System.getProperty("mockS3") != null) {
            if (!amazonS3.doesBucketExistV2(bucket)) {
                amazonS3.createBucket(bucket);
            }
        }
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
