package org.jenkinsci.plugins.lambdatestrunner.jenkins.plugin;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.TaskListener;
import hudson.remoting.VirtualChannel;
import org.jenkinsci.plugins.lambdatestrunner.jenkins.lambda.config.LambdaConfig;
import org.jenkinsci.plugins.lambdatestrunner.jenkins.lambda.config.LambdaConfigValidator;
import org.jenkinsci.plugins.lambdatestrunner.jenkins.request.Request;
import org.jenkinsci.plugins.lambdatestrunner.jenkins.request.RequestTransformer;
import org.jenkinsci.plugins.lambdatestrunner.jenkins.request.RequestValidator;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;

import java.io.IOException;

public class Execution extends SynchronousNonBlockingStepExecution<Void> {

    private final LambdaConfig lambdaConfig;
    private final Request rawRequest;

    Execution(StepContext context, LambdaConfig lambdaConfig, Request rawRequest) {
        super(context);
        this.lambdaConfig = lambdaConfig;
        this.rawRequest = rawRequest;
    }

    @Override
    protected Void run() throws Exception {
        validateInput();
        Request request = RequestTransformer.transform(rawRequest);
        logLambdaInvocation();
        logRequestParameters(request);
        LambdaTestRunnerCallable callable = getCallable(request);
        SlaveExecution slaveExecution = executeOnSlave(callable);
        handleExecutionResult(slaveExecution);
        return null;
    }

    private void validateInput() {
        LambdaConfigValidator.validate(lambdaConfig);
        RequestValidator.validate(rawRequest);
    }

    private void logLambdaInvocation() throws IOException, InterruptedException {
        String message = String.format("Starting Lambda function '%s' in region '%s'%n", lambdaConfig.getFunctionName(), lambdaConfig.getRegion());
        log(message);
    }

    private void logRequestParameters(Request request) throws IOException, InterruptedException {
        log("===== Request =====");
        log("repoUri: " + request.getRepoUri());
        log("command: " + request.getCommand());
        log("branch: " + request.getBranch());
        if (request.getStoreToS3() != null) log("storeToS3: " + request.getStoreToS3() + "\n");
    }

    private LambdaTestRunnerCallable getCallable(Request request) throws IOException, InterruptedException {
        FilePath filePath = getContext().get(FilePath.class);
        return new LambdaTestRunnerCallable(filePath, lambdaConfig, request);
    }

    private SlaveExecution executeOnSlave(LambdaTestRunnerCallable callable) throws Exception {
        Launcher launcher = getContext().get(Launcher.class);
        if (launcher != null) {
            VirtualChannel channel = launcher.getChannel();
            if (channel != null) {
                return channel.call(callable);
            }
        }
        return null;
    }

    private void handleExecutionResult(SlaveExecution slaveExecution) throws IOException, InterruptedException {
        log(slaveExecution.getExecutionLog());
        getContext().setResult(slaveExecution.getResult());
    }

    private void log(String message) throws IOException, InterruptedException {
        getContext().get(TaskListener.class).getLogger().println(message);
    }
}
