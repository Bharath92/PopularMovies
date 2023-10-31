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
                echo "boo"
                script {
                    println "kkk"
                    params.each {param ->
                      println " '${param.key.trim()}' -> '${param.value.trim()}' "
                    }
                }
                sh 'printenv'
                echo "foo ${params}"
                echo "foo ${params.INPUT_REVISION}"
                echo "Hello ${params.GIT_REVISION}"
                sh 'ls -la $WORKSPACE_TMP'
                sh 'git ls-remote https://github.com/bharath92/popularmovies.git refs/heads/${BRANCH_NAME} | cut -f1'
                script {
                    branchHash = sh(
                        script: "echo -n \$( git ls-remote https://github.com/bharath92/popularmovies.git refs/heads/${env.BRANCH_NAME} | cut -f1 )",
                        returnStdout: true
                    ).trim()
                }
                echo "$branchHash"
                
                checkout scmGit(branches: [[name: '**']], extensions: [], userRemoteConfigs: [[refspec: "+${branchHash}:refs/remotes/origin/${BRANCH_NAME}", url: 'https://github.com/bharath92/popularmovies.git']])
                // checkout scm(branches: [[name: '**']], extensions: [], userRemoteConfigs: [[refspec: "+${GIT_COMMIT}:refs/remotes/origin/${BRANCH_NAME}", url: 'https://github.com/bharath92/popularmovies.git']])
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
