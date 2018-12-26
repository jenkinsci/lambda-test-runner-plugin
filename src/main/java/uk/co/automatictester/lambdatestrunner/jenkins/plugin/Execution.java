package uk.co.automatictester.lambdatestrunner.jenkins.plugin;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.lambda.model.ResourceNotFoundException;
import com.amazonaws.services.lambda.model.TooManyRequestsException;
import hudson.model.Result;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;
import uk.co.automatictester.lambdatestrunner.jenkins.lambda.AWSLambdaFactory;
import uk.co.automatictester.lambdatestrunner.jenkins.lambda.InvokeRequestFactory;
import uk.co.automatictester.lambdatestrunner.jenkins.lambda.config.LambdaConfig;
import uk.co.automatictester.lambdatestrunner.jenkins.lambda.config.LambdaConfigValidator;
import uk.co.automatictester.lambdatestrunner.jenkins.request.Request;
import uk.co.automatictester.lambdatestrunner.jenkins.request.RequestMapper;
import uk.co.automatictester.lambdatestrunner.jenkins.request.RequestTransformer;
import uk.co.automatictester.lambdatestrunner.jenkins.request.RequestValidator;
import uk.co.automatictester.lambdatestrunner.jenkins.response.Response;
import uk.co.automatictester.lambdatestrunner.jenkins.response.ResponseMapper;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Execution extends SynchronousNonBlockingStepExecution<Integer> {

    private final LambdaConfig lambdaConfig;
    private final Request rawRequest;

    Execution(StepContext context, LambdaConfig lambdaConfig, Request rawRequest) {
        super(context);
        this.lambdaConfig = lambdaConfig;
        this.rawRequest = rawRequest;
    }

    @Override
    protected Integer run() {
        LambdaConfigValidator.validate(lambdaConfig);
        RequestValidator.validate(rawRequest);
        Request request = RequestTransformer.transform(rawRequest);
        logLambdaInvocation();
        logRequestParameters(request);
        InvokeRequest invokeRequest = getInvokeRequest(request);
        Optional<InvokeResult> invokeResult = invokeLambda(invokeRequest);
        invokeResult.ifPresent(this::logResponseDetails);
        return 0;
    }

    private void logLambdaInvocation() {
        String message = String.format("Starting Lambda function '%s' in region '%s'", lambdaConfig.getFunctionName(), lambdaConfig.getRegion());
        log(message);
        log("");
    }

    private void logRequestParameters(Request request) {
        log("===== Request =====");
        log("repoUri: " + request.getRepoUri());
        log("command: " + request.getCommand());
        log("branch: " + request.getBranch());
        if (request.getStoreToS3() != null) log("storeToS3: " + request.getStoreToS3());
        log("");
    }

    private InvokeRequest getInvokeRequest(Request request) {
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

    private void logResponseDetails(InvokeResult invokeResult) {
        String responseBody = new String(invokeResult.getPayload().array(), UTF_8);
        Response response = ResponseMapper.asObject(responseBody);
        log("===== Response =====");
        log("RequestId: " + response.getRequestId());
        log("Command exit code: " + response.getExitCode());
        log("S3 prefix: " + response.getS3Prefix());
        log("");
    }

    private void setResultToFailure() {
        getContext().setResult(Result.FAILURE);
    }

    private void log(String message) {
        try {
            PrintStream printStream = getContext().get(TaskListener.class).getLogger();
            printStream.println(message);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
