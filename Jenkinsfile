pipeline {
	agent any

	parameters {
	string(name: 'PR_BRANCHES', defaultValue: '', description: 'Comma separated list of additional pull request branches (e.g. meta-trustx=PR-177,meta-trustx-nxp=PR-13,gyroidos_build=PR-97)')
	}

	stages {
		stage('build GyroidOS') {
			steps {
				build job: "../gyroidos/kirkstone", wait: true, parameters: [
					string(name: "PR_BRANCHES", value: "meta-trustx=${BRANCH_NAME},${PR_BRANCHES}")
				]
			}
		}
	}
}
