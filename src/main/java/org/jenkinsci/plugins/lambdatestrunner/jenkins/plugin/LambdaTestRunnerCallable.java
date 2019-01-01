package org.jenkinsci.plugins.lambdatestrunner.jenkins.plugin;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.lambda.model.ResourceNotFoundException;
import com.amazonaws.services.lambda.model.TooManyRequestsException;
import hudson.FilePath;
import hudson.model.Result;
import hudson.model.TaskListener;
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
import org.jenkinsci.plugins.workflow.steps.StepContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public class LambdaTestRunnerCallable extends MasterToSlaveCallable<Void, RuntimeException> {

    private final StepContext context;
    private final TaskListener taskListener;
    private final FilePath filePath;
    private final LambdaConfig lambdaConfig;
    private final Request request;

    public LambdaTestRunnerCallable(StepContext context, TaskListener taskListener, FilePath filePath, LambdaConfig lambdaConfig, Request request) {
        this.context = context;
        this.taskListener = taskListener;
        this.filePath = filePath;
        this.lambdaConfig = lambdaConfig;
        this.request = request;
    }

    @Override
    public Void call() throws RuntimeException {
        InvokeRequest invokeRequest = getInvokeRequest();
        Optional<InvokeResult> invokeResult = invokeLambda(invokeRequest);
        if (invokeResult.isPresent()) {
            Response response = getResponse(invokeResult.get());
            logResponseDetails(response);
            if (response.getExitCode() != 0) setResultToFailure();

            String destinationDir = getDownloadDestinationDir();
            BuildOutputDownloader downloader = new BuildOutputDownloader(lambdaConfig.getS3Bucket(), response.getS3Prefix(), destinationDir);
            downloader.download();
            BuildOutputExtractor extractor = new BuildOutputExtractor(destinationDir);
            extractor.explode();

            String logFile = String.format("%s/%s/test-execution.log", destinationDir, response.getS3Prefix());
            logTestExecutionOutput(logFile);
        }
        return null;
    }

    private InvokeRequest getInvokeRequest() {
        String payload = RequestMapper.asString(request);
        return InvokeRequestFactory.getInstance(lambdaConfig.getFunctionName(), payload);
    }

    private Optional<InvokeResult> invokeLambda(InvokeRequest request) {
        AWSLambda lambda = AWSLambdaFactory.getInstance(lambdaConfig.getRegion());
        Optional<InvokeResult> result = Optional.empty();
        try {
            result = Optional.of(lambda.invoke(request));
        } catch (TooManyRequestsException e) {
            log("ERROR: " + e.getReason());
            setResultToFailure();
        } catch (ResourceNotFoundException e) {
            log("ERROR: " + e.getMessage());
            setResultToFailure();
        }
        return result;
    }

    private Response getResponse(InvokeResult invokeResult) {
        String responseBody = new String(invokeResult.getPayload().array(), UTF_8);
        return ResponseMapper.asObject(responseBody);
    }

    private String getDownloadDestinationDir() {
        return String.format("%s/downloads", getPresentWorkingDir());
    }

    private void logTestExecutionOutput(String logFile) {
        Path path = Paths.get(logFile);
        String content = null;
        try {
            content = Files.lines(path).collect(Collectors.joining("\n"));
        } catch (IOException e) {
            setResultToFailure();
            log("ERROR: Cannot read the log file '" + logFile + "': " + e.getMessage());
        }
        log("===== Test execution log from Lambda ====="); // TODO log("");
        log(content);
    }

    private String getPresentWorkingDir() {
        String pwd = null;
        try {
            pwd = filePath.absolutize().toString();
        } catch (IOException | InterruptedException e) {
            setResultToFailure();
            log("ERROR: Cannot read present working directory: " + e.getMessage());
        }
        return pwd;
    }

    private void setResultToFailure() {
        context.setResult(Result.FAILURE);
    }

    private void logResponseDetails(Response response) {
        log("===== Response =====");
        log("RequestId: " + response.getRequestId());
        log("Command exit code: " + response.getExitCode());
        log("S3 prefix: " + response.getS3Prefix());
        log("");
    }

    private void log(String message) {
        taskListener.getLogger().println(message);
    }
}
