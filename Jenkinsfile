// node {
//     stage('foo') {
//         shell('echo lol')
//         shell('printenv')
//     }
    
//     stage('checkout') {
//         checkout scmGit(branches: [[name: '**']], extensions: [], userRemoteConfigs: [[refspec: "+${GIT_COMMIT}:refs/remotes/origin/${GIT_BRANCH}", url: 'https://github.com/bharath92/gitissues.git']])
//     }

//     stage('HelloWorld') {
//         echo 'Hello World'
//     }
// }

pipeline {
    stages {
        stage('Build') {
            steps {
                sh 'echo foo'
            }
        }
        stage('Test') {
            steps {
                sh 'echo bar'
            }
        }
    }
}
