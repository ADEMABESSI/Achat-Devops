pipeline {
    agent any

    environment {
        SONARQUBE_ENV = 'sonarqube'
        NEXUS_URL = 'http://nexus-server:8081'
    }

    stages {

        stage('Clean Workspace') {
            steps {
                cleanWs()
            }
        }

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

        stage('Debug POM') {
    steps {
        sh 'pwd'
        sh 'ls -la'
        sh 'cat pom.xml | grep -n distributionManagement'
    }
}

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('SonarQube') {
            steps {
                withSonarQubeEnv('sonarqube') {
                    withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                        sh 'mvn sonar:sonar -Dsonar.login=$SONAR_TOKEN'
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
                        --settings /var/lib/jenkins/.m2/settings.xml \
                        -Dnexus.username=$NEXUS_USER \
                        -Dnexus.password=$NEXUS_PASS
                    '''
                }
            }
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
