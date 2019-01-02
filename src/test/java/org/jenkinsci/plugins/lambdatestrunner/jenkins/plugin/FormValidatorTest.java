package org.jenkinsci.plugins.lambdatestrunner.jenkins.plugin;

import hudson.util.FormValidation;
import org.junit.Test;

import static org.junit.Assert.*;

public class FormValidatorTest {

    @Test
    public void testValidateNoErrors() {
        String functionName = "LambdaTestRunner";
        String region = "eu-west-2";
        String s3Bucket = "automatictester.co.uk-lambda-test-runner-build-outputs";
        String repoUri = "https://github.com/automatictester/lambda-test-runner.git";
        String command = "./mvnw test -Dmaven.repo.local=${MAVEN_USER_HOME}";
        String storeToS3 = "target/surefire-reports";

        FormValidation result = FormValidator.validate(functionName, region, s3Bucket, repoUri, command, storeToS3);
        assertEquals(result, FormValidation.ok());
    }

    @Test
    public void testValidateSingleError() {
        String functionName = "";
        String region = "eu-west-2";
        String s3Bucket = "automatictester.co.uk-lambda-test-runner-build-outputs";
        String repoUri = "https://github.com/automatictester/lambda-test-runner.git";
        String command = "./mvnw test -Dmaven.repo.local=${MAVEN_USER_HOME}";
        String storeToS3 = "target/surefire-reports";

        FormValidation result = FormValidator.validate(functionName, region, s3Bucket, repoUri, command, storeToS3);
        assertEquals(result.kind, FormValidation.Kind.ERROR);
        assertEquals(result.getMessage(), "Function name cannot be empty<br>");
    }

    @Test
    public void testValidateMultipleErrors() {
        String functionName = "";
        String region = "eu-west-2";
        String s3Bucket = "automatictester.co.uk-lambda-test-runner-build-outputs";
        String repoUri = "https://github.com/automatictester/lambda-test-runner.git";
        String command = "./mvnw test -Dmaven.repo.local=${MAVEN_USER_HOME}";
        String storeToS3 = "/target/surefire-reports";

        FormValidation result = FormValidator.validate(functionName, region, s3Bucket, repoUri, command, storeToS3);
        assertEquals(result.kind, FormValidation.Kind.ERROR);
        assertEquals(result.getMessage(), "Function name cannot be empty<br>Dirs to store to S3 cannot contain absolute paths<br>");
    }
}
