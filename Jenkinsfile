// Jenkinsfile (Declarative Pipeline)
pipeline {
    agent any
    stages {
        stage('Github') {
           steps {
                git branch: 'dev', url: 'https://github.com/hanjongho/banking.git'
            }
            post {
                success {
                    sh 'echo "Successfully Cloned Repository"'
                }
                failure {
                    sh 'echo "Fail Cloned Repository"'
                }
            }
        }
        stage('Build') {
            steps {
                sh 'chmod +x gradlew'
                sh  './gradlew clean build'
                sh 'ls -al ./build'
            }
            post {
                success {
                    echo 'gradle build success'
                }

                failure {
                    echo 'gradle build failed'
                }
            }
        }
        stage('Test') {
            steps {
                echo  '테스트 단계와 관련된 몇 가지 단계를 수행합니다.'
            }
        }
        stage('Deploy') {
            steps {
            }
        }
    }
}