pipeline {
	agent any

	parameters {
	string(name: 'CI_LIB_VERSION', defaultValue: 'main', description: 'Version of gyroidos_ci_common to use for this build')
	string(name: 'PR_BRANCHES', defaultValue: '', description: 'Comma separated list of additional pull request branches (e.g. meta-gyroidos=PR-177,meta-gyroidos-nxp=PR-13,gyroidos_build=PR-97)')
	}

	stages {
		stage('build GyroidOS') {
			steps {
				script {
					REPO_NAME = determineRepoName()

					if (env.CHANGE_TARGET != null) {
						// in case this is a PR build
						// set the BASE_BRANCH to the target
						// e.g. PR-123 -> kirkstone
						BASE_BRANCH = env.CHANGE_TARGET
					} else {
						// in case this is a regular build
						// let the BASE_BRANCH equal this branch
						// e.g. kirkstone -> kirkstone
						BASE_BRANCH = env.BRANCH_NAME
					}
				}

				build job: "../gyroidos/${BASE_BRANCH}", wait: true, parameters: [
					string(name: "CI_LIB_VERSION", value: CI_LIB_VERSION),
					string(name: "PR_BRANCHES", value: "${REPO_NAME}=${env.BRANCH_NAME},${env.PR_BRANCHES}")
				]
			}
		}
	}
}


// Determine the Repository name from its URL.
// Avoids hardcoding the name in every Jenkinsfile individually.
// Source: https://stackoverflow.com/a/45690925
String determineRepoName() {
	return scm.getUserRemoteConfigs()[0].getUrl().tokenize('/').last().split("\\.")[0]
}
