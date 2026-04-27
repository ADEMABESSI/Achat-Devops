pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk 'JDK8'
    }

    environment {
        SONARQUBE_ENV = 'sonarqube'
        NEXUS_URL = 'http://nexus-server:8081'
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/ADEMABESSI/Achat-Devops.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonarqube') {
                    withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                        sh '''
                            mvn sonar:sonar \
                            -Dsonar.login=$SONAR_TOKEN
                        '''
                    }
                }
            }
        }

        stage('Nexus Deploy') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'nexus-cred',
                    usernameVariable: 'NEXUS_USER',
                    passwordVariable: 'NEXUS_PASS'
                )]) {

                    sh '''
                        mvn deploy -DskipTests \
                        -DaltDeploymentRepository=nexus-releases::default::http://nexus-server:8081/repository/maven-releases/
                    '''
                }
            }
        }
    }

    post {
        always {
            cleanWs()
            echo 'Pipeline terminé'
        }
    }
}
