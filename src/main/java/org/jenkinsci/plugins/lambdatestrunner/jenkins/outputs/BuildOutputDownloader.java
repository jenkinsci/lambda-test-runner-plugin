package org.jenkinsci.plugins.lambdatestrunner.jenkins.outputs;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.transfer.MultipleFileDownload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;

import java.io.File;

public class BuildOutputDownloader {

    private final AmazonS3 amazonS3 = AmazonS3Factory.getInstance();
    private final String bucket;
    private final String prefix;
    private final String destinationDir;

    public BuildOutputDownloader(String bucket, String prefix, String destinationDir) {
        this.bucket = bucket;
        this.prefix = prefix;
        this.destinationDir = destinationDir;
    }

    public void download() {
        TransferManager manager = TransferManagerBuilder
                .standard()
                .withS3Client(amazonS3)
                .build();
        try {
            MultipleFileDownload download = manager.downloadDirectory(bucket, prefix, new File(destinationDir));
            download.waitForCompletion();
        } catch (InterruptedException e) {
            throw new RuntimeException("Error downloading objects from S3: " + e.getMessage());
        }
    }
}
