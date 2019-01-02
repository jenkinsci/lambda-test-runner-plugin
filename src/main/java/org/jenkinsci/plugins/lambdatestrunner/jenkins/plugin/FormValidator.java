package org.jenkinsci.plugins.lambdatestrunner.jenkins.plugin;

import hudson.Util;
import hudson.util.FormValidation;

import static org.jenkinsci.plugins.lambdatestrunner.jenkins.lambda.config.LambdaConfigValidator.*;
import static org.jenkinsci.plugins.lambdatestrunner.jenkins.request.RequestValidator.*;

public class FormValidator {

    private FormValidator() {
    }

    public static FormValidation validate(String functionName, String region, String s3Bucket, String repoUri, String command, String storeToS3) {
        boolean isError = false;
        String errorMessage = "";

        if (!isFunctionNameValid(Util.fixEmptyAndTrim(functionName))) {
            errorMessage += "Function name cannot be empty\n";
            isError = true;
        }
        if (!isRegionValid(Util.fixEmptyAndTrim(region))) {
            errorMessage += "Region cannot be empty\n";
            isError = true;
        }
        if (!isS3BucketValid(Util.fixEmptyAndTrim(s3Bucket))) {
            errorMessage += "S3 bucket cannot be empty\n";
            isError = true;
        }
        if (!isRepoUriValid(Util.fixEmptyAndTrim(repoUri))) {
            errorMessage += "Repo URI must start with 'git' or 'http'\n";
            isError = true;
        }
        if (!isCommandValid(Util.fixEmptyAndTrim(command))) {
            errorMessage += "Command cannot be empty\n";
            isError = true;
        }
        if (!isStoreToS3Valid(Util.fixEmptyAndTrim(storeToS3))) {
            errorMessage += "Dirs to store to S3 cannot contain absolute paths\n";
            isError = true;
        }

        if (isError) {
            return FormValidation.error(errorMessage);
        } else {
            return FormValidation.ok();
        }
    }
}
