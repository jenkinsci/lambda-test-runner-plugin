package org.jenkinsci.plugins.lambdatestrunner.jenkins.plugin;

import hudson.model.Result;

import java.io.Serializable;

public class SlaveExecution implements Serializable {

    private StringBuilder executionLog = new StringBuilder();
    private Result result = Result.SUCCESS;

    public String getExecutionLog() {
        return executionLog.toString();
    }

    public void appendLogEntry(String logEntry) {
        this.executionLog.append(logEntry + "\n");
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
