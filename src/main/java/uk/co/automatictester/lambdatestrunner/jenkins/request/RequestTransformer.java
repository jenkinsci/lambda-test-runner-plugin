package uk.co.automatictester.lambdatestrunner.jenkins.request;

public class RequestTransformer {

    private RequestTransformer() {
    }

    public static Request transform(Request rawRequest) {
        Request request = new Request();
        request.setRepoUri(rawRequest.getRepoUri());
        request.setCommand(rawRequest.getCommand());
        request.setStoreToS3(rawRequest.getStoreToS3());

        if (rawRequest.getBranch() == null || rawRequest.getBranch().equals("")) {
            request.setBranch("HEAD");
        } else {
            request.setBranch(rawRequest.getBranch());
        }

        return request;
    }
}
