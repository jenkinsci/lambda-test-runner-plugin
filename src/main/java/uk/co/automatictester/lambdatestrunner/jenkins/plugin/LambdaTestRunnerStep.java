package uk.co.automatictester.lambdatestrunner.jenkins.plugin;

import hudson.Extension;
import hudson.model.Run;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import uk.co.automatictester.lambdatestrunner.jenkins.lambda.config.LambdaConfig;
import uk.co.automatictester.lambdatestrunner.jenkins.request.Request;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

public class LambdaTestRunnerStep extends Step {

    public final String functionName;
    public final String region;
    public final String s3Bucket;
    public final String repoUri;
    public final String command;
    public String branch;
    public String storeToS3;

    @DataBoundConstructor
    public LambdaTestRunnerStep(String functionName, String region, String s3Bucket, String repoUri, String command) {
        this.functionName = functionName;
        this.region = region;
        this.s3Bucket = s3Bucket;
        this.repoUri = repoUri;
        this.command = command;
    }

    @DataBoundSetter
    public void setBranch(String branch) {
        this.branch = branch;
    }

    @DataBoundSetter
    public void setStoreToS3(String storeToS3) {
        this.storeToS3 = storeToS3;
    }

    @Override
    public StepExecution start(StepContext context) {
        LambdaConfig lambdaConfig = new LambdaConfig(functionName, region, s3Bucket);

        Request rawRequest = new Request();
        rawRequest.setRepoUri(repoUri);
        rawRequest.setCommand(command);
        rawRequest.setBranch(branch);
        if (storeToS3 != null && !storeToS3.equals("")) rawRequest.setStoreToS3(Arrays.asList(storeToS3.split(",")));

        return new Execution(context, lambdaConfig, rawRequest);
    }

    @Extension
    public static class StepDescriptorImpl extends StepDescriptor {

        public static final String defaultFunctionName = "LambdaTestRunner";

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return Collections.singleton(Run.class);
        }

        @Override
        public String getFunctionName() {
            return "lambdaTestRunner";
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Run AWS Lambda Test Runner";
        }
    }
}
