// This file is for releasing only. If you are not involved in releasing this plugin, you can ignore it.

multibranchPipelineJob('lambda-test-runner-plugin-release') {
    branchSources {
        git {
            remote('git@github.com:jenkinsci/lambda-test-runner-plugin.git')
            credentialsId('github-creds-automatictester')
        }
    }
}
