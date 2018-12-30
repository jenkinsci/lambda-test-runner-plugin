package org.jenkinsci.plugins.lambdatestrunner.jenkins.request;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@JsonInclude(NON_EMPTY)
public class Request implements Serializable {

    private String repoUri;
    private String branch;
    private String command;
    private List<String> storeToS3;

    public String getRepoUri() {
        return repoUri;
    }

    public void setRepoUri(String repoUri) {
        this.repoUri = repoUri;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public List<String> getStoreToS3() {
        return storeToS3;
    }

    public void setStoreToS3(List<String> storeToS3) {
        this.storeToS3 = storeToS3;
    }
}
