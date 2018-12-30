package org.jenkinsci.plugins.lambdatestrunner.jenkins.request;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class RequestTransformerTest {

    private static final String REPO_URI = "https://github.com/automatictester/lambda-test-runner-jenkins-plugin.git";
    private static final String COMMAND = "./mvnw clean test -Dmaven.repo.local=${MAVEN_USER_HOME}";
    private static final String MASTER = "master";
    private static final String HEAD = "HEAD";

    private Request rawRequest;

    @Before
    public void createRequest() {
        rawRequest = new Request();
        rawRequest.setRepoUri(REPO_URI);
        rawRequest.setBranch(MASTER);
        rawRequest.setCommand(COMMAND);
        rawRequest.setStoreToS3(getDirsToStore());
    }

    private List<String> getDirsToStore() {
        List<String> dirsToStore = new ArrayList<>();
        dirsToStore.add("target/surefire-reports");
        return dirsToStore;
    }

    @Test
    public void testHappyPath() {
        Request request = RequestTransformer.transform(rawRequest);
        assertEquals(request.getRepoUri(), REPO_URI);
        assertEquals(request.getCommand(), COMMAND);
        assertEquals(request.getBranch(), MASTER);
        assertEquals(request.getStoreToS3(), getDirsToStore());
    }

    @Test
    public void testBranchEmpty() {
        rawRequest.setBranch("");
        Request request = RequestTransformer.transform(rawRequest);
        assertEquals(request.getBranch(), HEAD);
    }

    @Test
    public void testBranchNull() {
        rawRequest.setBranch(null);
        Request request = RequestTransformer.transform(rawRequest);
        assertEquals(request.getBranch(), HEAD);
    }
}
