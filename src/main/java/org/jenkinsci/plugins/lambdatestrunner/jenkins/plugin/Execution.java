package org.jenkinsci.plugins.lambdatestrunner.jenkins.plugin;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.TaskListener;
import hudson.remoting.Callable;
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
        executeOnSlave(callable);
        return null;
    }

    private LambdaTestRunnerCallable getCallable(Request request) throws IOException, InterruptedException {
        TaskListener taskListener = getContext().get(TaskListener.class);
        FilePath filePath = getContext().get(FilePath.class);
        return new LambdaTestRunnerCallable(getContext(), taskListener, filePath, lambdaConfig, request);
    }

    private void executeOnSlave(Callable callable) throws IOException, InterruptedException {
        getContext().get(Launcher.class).getChannel().call(callable);
    }

    private void validateInput() {
        LambdaConfigValidator.validate(lambdaConfig);
        RequestValidator.validate(rawRequest);
    }

    private void logLambdaInvocation() throws IOException, InterruptedException {
        String message = String.format("Starting Lambda function '%s' in region '%s'", lambdaConfig.getFunctionName(), lambdaConfig.getRegion());
        log(message);
        log("");
    }

    private void logRequestParameters(Request request) throws IOException, InterruptedException {
        log("===== Request =====");
        log("repoUri: " + request.getRepoUri());
        log("command: " + request.getCommand());
        log("branch: " + request.getBranch());
        if (request.getStoreToS3() != null) log("storeToS3: " + request.getStoreToS3());
        log("");
    }

    private void log(String message) throws IOException, InterruptedException {
        getContext().get(TaskListener.class).getLogger().println(message);
    }
}
