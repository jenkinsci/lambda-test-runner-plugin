package org.jenkinsci.plugins.lambdatestrunner.jenkins.request;

import org.testng.annotations.Test;

import java.util.Collections;

import static org.testng.Assert.assertEquals;

public class RequestMapperTest {

    @Test
    public void testAsString() {
        Request rawRequest = new Request();
        rawRequest.setRepoUri("https://github.com/automatictester/lambda-test-runner-jenkins-plugin.git");
        rawRequest.setBranch("develop");
        rawRequest.setCommand("./mvnw clean test -Dtest=SmokeTest -Dmaven.repo.local=${MAVEN_USER_HOME}");
        rawRequest.setStoreToS3(Collections.singletonList("target/surefire-reports"));

        String mappedRequest = RequestMapper.asString(rawRequest);
        assertEquals(mappedRequest, "{\"repoUri\":\"https://github.com/automatictester/lambda-test-runner-jenkins-plugin.git\",\"branch\":\"develop\",\"command\":\"./mvnw clean test -Dtest=SmokeTest -Dmaven.repo.local=${MAVEN_USER_HOME}\",\"storeToS3\":[\"target/surefire-reports\"]}");
    }
}
