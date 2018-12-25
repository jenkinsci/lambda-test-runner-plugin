package uk.co.automatictester.lambdatestrunner.jenkins.it;

import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.BuildWatcher;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class DeclarativePipelineIT {

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    @ClassRule
    public static BuildWatcher buildWatcher = new BuildWatcher();

    @Test
    public void declarativePipelineTest() throws Exception {
        String pipeline = readFileToString("src/test/resources/Jenkinsfile");
        WorkflowJob project = jenkinsRule.createProject(WorkflowJob.class);
        CpsFlowDefinition pipelineDefinition = new CpsFlowDefinition(pipeline, true);
        project.setDefinition(pipelineDefinition);

        WorkflowRun build = jenkinsRule.buildAndAssertSuccess(project);

        jenkinsRule.assertLogContains("Starting Lambda function 'LambdaTestRunner' in region 'eu-west-2'", build);
        jenkinsRule.assertLogContains("command: ./mvnw test -Dtest=SmokeTest -Dmaven.repo.local=${MAVEN_USER_HOME}", build);
        jenkinsRule.assertLogContains("Command exit code: 0", build);
    }

    private String readFileToString(String file) throws IOException {
        Path path = Paths.get(file);
        return Files.lines(path).collect(Collectors.joining("\n"));
    }
}
