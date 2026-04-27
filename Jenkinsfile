pipeline {
    agent any

    tools {
        maven 'maven-3.9.4'
        jdk 'java-17'
    }

    environment {
        SONARQUBE_ENV = 'sonarqube'
        NEXUS_URL = 'http://192.168.1.10:8081'

    }

    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/ADEMABESSI/Achat-Devops.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('sonarqube - Analyse qualité') {
            steps {
                withSonarQubeEnv('sonarqube') {
                    withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                        sh 'mvn sonar:sonar -Dsonar.login=$SONAR_TOKEN'
                    }
                }
            }
        }

      stage('Nexus - Publication') {
    steps {
        sh "mvn deploy -Dnexus.url=${NEXUS_URL}"
    }
}
        
    

    post {
        always {
            echo 'Pipeline terminé'
        }
        success {
            echo 'Succès'
        }
        failure {
            echo 'Échec'
        }
    }
}
