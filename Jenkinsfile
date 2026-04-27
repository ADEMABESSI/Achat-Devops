pipeline {
    agent any
    
    stages {
        
        stage('Checkout GIT') {
            steps {
                echo 'Récupération du code depuis GitHub...'
            }
        }
        
        stage('Maven Build') {
            steps {
                echo 'Compilation Maven...'
                sh 'mvn clean package -DskipTests'
            }
        }
        
        stage('Maven Test') {
            steps {
                echo 'Tests unitaires...'
                sh 'mvn test'
            }
        }
        
    }
    
    post {
        success {
            echo 'Build réussi !'
        }
        failure {
            echo 'Build échoué !'
        }
    }
}
