package org.jenkinsci.plugins.lambdatestrunner.jenkins.plugin;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.model.AWSLambdaException;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.lambda.model.TooManyRequestsException;
import hudson.FilePath;
import jenkins.security.MasterToSlaveCallable;
import org.jenkinsci.plugins.lambdatestrunner.jenkins.lambda.AWSLambdaFactory;
import org.jenkinsci.plugins.lambdatestrunner.jenkins.lambda.InvokeRequestFactory;
import org.jenkinsci.plugins.lambdatestrunner.jenkins.lambda.config.LambdaConfig;
import org.jenkinsci.plugins.lambdatestrunner.jenkins.outputs.BuildOutputDownloader;
import org.jenkinsci.plugins.lambdatestrunner.jenkins.outputs.BuildOutputExtractor;
import org.jenkinsci.plugins.lambdatestrunner.jenkins.request.Request;
import org.jenkinsci.plugins.lambdatestrunner.jenkins.request.RequestMapper;
import org.jenkinsci.plugins.lambdatestrunner.jenkins.response.Response;
import org.jenkinsci.plugins.lambdatestrunner.jenkins.response.ResponseMapper;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static hudson.model.Result.FAILURE;
import static java.nio.charset.StandardCharsets.UTF_8;

public class LambdaTestRunnerCallable extends MasterToSlaveCallable<SlaveExecution, Exception> {

    private final FilePath filePath;
    private final LambdaConfig lambdaConfig;
    private final Request request;

    LambdaTestRunnerCallable(FilePath filePath, LambdaConfig lambdaConfig, Request request) {
        this.filePath = filePath;
        this.lambdaConfig = lambdaConfig;
        this.request = request;
    }

    @Override
    public SlaveExecution call() throws Exception {
        String payload = RequestMapper.asString(request);
        InvokeRequest invokeRequest = InvokeRequestFactory.getInstance(lambdaConfig.getFunctionName(), payload);

        AWSLambda lambda = AWSLambdaFactory.getInstance(lambdaConfig.getRegion());

        InvokeResult invokeResult = null;
        SlaveExecution execution = new SlaveExecution();
        try {
            invokeResult = lambda.invoke(invokeRequest);
        } catch (TooManyRequestsException e) {
            execution.appendLogEntry("ERROR: " + e.getReason());
            execution.setResult(FAILURE);
        } catch (AWSLambdaException e) {
            execution.appendLogEntry("ERROR: " + e.getMessage());
            execution.setResult(FAILURE);
        }

        if (invokeResult != null) {

            String responseBody = new String(invokeResult.getPayload().array(), UTF_8);
            Response response;
            try {
                response = ResponseMapper.asObject(responseBody);
            } catch (IOException e) {
                JSONObject json = new JSONObject(responseBody);
                execution.appendLogEntry("===== Unexpected response =====\n" + json.toString(3));
                return execution;
            }

            execution.appendLogEntry("===== Response =====");
            execution.appendLogEntry("RequestId: " + response.getRequestId());
            execution.appendLogEntry("Command exit code: " + response.getExitCode());
            execution.appendLogEntry("S3 prefix: " + response.getS3Prefix() + "\n");
            if (response.getExitCode() != 0) execution.setResult(FAILURE);

            String destinationDir = String.format("%s/downloads", filePath.absolutize().toString());
            BuildOutputDownloader downloader = new BuildOutputDownloader(lambdaConfig.getS3Bucket(), response.getS3Prefix(), destinationDir);
            downloader.download();
            BuildOutputExtractor extractor = new BuildOutputExtractor(destinationDir);
            extractor.explode();

            String logFile = String.format("%s/%s/test-execution.log", destinationDir, response.getS3Prefix());

            Path lambdaExecutionLogFile = Paths.get(logFile);
            String lambdaExecutionLogContent = Files.lines(lambdaExecutionLogFile).collect(Collectors.joining("\n"));
            execution.appendLogEntry("===== Test execution log from Lambda =====");
            execution.appendLogEntry(lambdaExecutionLogContent);
        }

        return execution;
    }
}
