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
    agent any
    options { skipDefaultCheckout() } 
    stages {
        stage('Build2') {
            steps {
                sh 'printenv'
                sh 'ls -la $WORKSPACE_TMP'
                sh 'sleep 60'
                checkout scm(branches: [[name: '**']], extensions: [], userRemoteConfigs: [[refspec: "+${GIT_COMMIT}:refs/remotes/origin/${GIT_BRANCH}", url: 'https://github.com/bharath92/popularmovies.git']])
                sh 'echo foo'
            }
        }
        // stage('checkout') {
        //     steps {
                
        //     }
        // }
        stage('Test2') {
            steps {
                sh 'echo bar'
            }
        }
    }
}
