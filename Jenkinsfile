pipeline {
    agent any

    environment {
        imagename = "banking-api"
        registryCredential = 'docker-hub'
        dockerImage = ''
    }

    stages {
        stage('Prepare') {
          steps {
            echo 'Clonning Repository'
            git url: 'git@github.com:hanjongho/banking.git',
              branch: 'dev',
              credentialsId: 'github'
            }
            post {
             success {
               echo 'Successfully Cloned Repository'
             }
           	 failure {
               error 'This pipeline stops here...'
             }
          }
        }

        stage('Bulid Gradle') {
          steps {
            echo 'Bulid Gradle'
            dir('.'){
                sh './gradlew clean build'
            }
          }
          post {
            failure {
              error 'This pipeline stops here...'
            }
          }
        }

        stage('Build Docker') {
          steps {
            echo 'Build Docker'
            script {
                dockerImage = docker.build ('hanjongho/banking-api')
            }
          }
          post {
            failure {
              error 'This pipeline stops here...'
            }
          }
        }

        stage('Push Docker') {
          steps {
            echo 'Push Docker'
            script {
                docker.withRegistry('', registryCredential) {
                    dockerImage.push()
                }
            }
          }
          post {
            failure {
              error 'This pipeline stops here...'
            }
          }
        }

        stage('Docker Run') {
            steps {
                echo 'Pull Docker Image & Docker Image Run'
                sshagent (credentials: ['ssh']) {
                    sh "ssh -o StrictHostKeyChecking=no ubuntu@3.35.185.185 'docker pull hanjongho/banking-api:latest'"
//                     sh "ssh -o StrictHostKeyChecking=no ubuntu@3.35.185.185 'docker ps -q --filter name=banking-api | grep -q . && docker rm -f docker ps -aq --filter name=banking-api'"
                    sh "ssh -o StrictHostKeyChecking=no ubuntu@3.35.185.185 'docker stop banking-api'"
                    sh "ssh -o StrictHostKeyChecking=no ubuntu@3.35.185.185 'docker rm banking-api'"
                    sh "ssh -o StrictHostKeyChecking=no ubuntu@3.35.185.185 'docker run -d --name banking-api -p 8080:8080 hanjongho/banking-api:latest'"
                }
            }
        }
    }
    post {
        success {
            slackSend (channel: '#프로젝트', color: '#00FF00', message: "SUCCESSFUL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
        }
        failure {
            slackSend (channel: '#프로젝트', color: '#FF0000', message: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
        }
    }
}