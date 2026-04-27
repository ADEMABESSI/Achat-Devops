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
    stage('SonarQube - Analyse qualité') {
    steps {
        withSonarQubeEnv('SonarQube') {
            sh """
                mvn sonar:sonar \
                -Dsonar.projectKey=MonProjet
            """
        }
    }
}
    stage('Nexus - Publication') {
            steps {
                echo '========== Publication de l artefact sur Nexus =========='
                sh 'mvn deploy -DskipTests'
            }
        }
    }

    post {
        success {
            echo '✅ PIPELINE COMPLET RÉUSSI !'
        }
        failure {
            echo '❌ PIPELINE ÉCHOUÉ - vérifier les logs'
        }
    }
    

