package org.jenkinsci.plugins.lambdatestrunner.jenkins.request;

import java.util.Arrays;
import java.util.List;

public class RequestValidator {
    private RequestValidator() {
    }

    public static void validate(Request request) {
        validateRepoUri(request.getRepoUri());
        validateCommand(request.getCommand());
        validateStoreToS3(request.getStoreToS3());
    }

    public static boolean isRepoUriValid(String repoUri) {
        return !(repoUri == null || repoUri.equals("") || (!repoUri.startsWith("git") && !repoUri.startsWith("http")));
    }

    public static boolean isCommandValid(String command) {
        return !(command == null || command.equals(""));
    }

    public static boolean isStoreToS3Valid(String storeToS3) {
        if (storeToS3 == null) return true;
        List<String> storeToS3AsList = Arrays.asList(storeToS3.split(","));
        return isStoreToS3Valid(storeToS3AsList);
    }

    private static boolean isStoreToS3Valid(List<String> storeToS3) {
        if (storeToS3 != null) {
            for (String dir : storeToS3) {
                if (dir.startsWith("/")) {
                    return false;
                }
            }
        }
        return true;
    }

    private static void validateRepoUri(String repoUri) {
        if (!isRepoUriValid(repoUri)) {
            throw new IllegalArgumentException("Invalid repoUri: " + repoUri);
        }
    }

    private static void validateCommand(String command) {
        if (!isCommandValid(command)) {
            throw new IllegalArgumentException("Invalid command: " + command);
        }
    }

    private static void validateStoreToS3(List<String> storeToS3) {
        if (!isStoreToS3Valid(storeToS3)) {
            throw new IllegalArgumentException("Invalid storeToS3: must be a relative path");
        }
    }
}
