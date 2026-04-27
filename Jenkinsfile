pipeline {
    agent any

    tools {
        maven 'maven-3.9.4'
        jdk   'java-17'
    }

    environment {
        VM_IP         = "172.17.0.1"
        NEXUS_IP      = "172.17.0.1"
        NEXUS_PORT    = "8081"
        APP_NAME      = "achat"
        APP_VERSION   = "1.0"
        GROUP_ID_PATH = "tn/esprit/rh"
        APP_PORT      = "8082"
        IMAGE_TAG     = "1.0.0"

        NEXUS_URL     = "http://${NEXUS_IP}:${NEXUS_PORT}"
        IMAGE_NAME    = "${APP_NAME}:${IMAGE_TAG}"
        JAR_NAME      = "${APP_NAME}-${APP_VERSION}.jar"
        NEXUS_JAR_URL = "${NEXUS_URL}/repository/maven-releases/${GROUP_ID_PATH}/${APP_NAME}/${APP_VERSION}/${JAR_NAME}"
    }

    stages {

        stage('Checkout SCM') {
            steps {
                // Checkout récupère aussi le Dockerfile depuis GitHub ✅
                git url: 'https://github.com/ADEMABESSI/Achat-Devops.git'
            }
        }
stage('Vérifier Dockerfile') {
    steps {
        sh '''
            ls -la
            if [ -f docker/Dockerfile ]; then
    echo "✅ Dockerfile trouvé !"
    cat docker/Dockerfile
else
    echo "❌ Dockerfile NON trouvé !"
    exit 1
fi
}
}

        stage('Tool Install') {
            steps {
                sh 'mvn --version'
                sh 'java -version'
                sh 'docker --version'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
                sh "ls -lh target/${JAR_NAME}"
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('SonarQube - Analyse qualité') {
            steps {
                withSonarQubeEnv('sonarqube') {
                    withCredentials([string(
                        credentialsId: 'sonar-token',
                        variable: 'SONAR_TOKEN'
                    )]) {
                        sh 'mvn sonar:sonar -Dsonar.login=$SONAR_TOKEN'
                    }
                }
            }
        }

        stage('Prepare Maven Settings') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'nexus-cred',
                    usernameVariable: 'NEXUS_USER',
                    passwordVariable: 'NEXUS_PASS'
                )]) {
                    writeFile file: 'settings.xml', text: """
<settings>
  <servers>
    <server>
      <id>nexus-releases</id>
      <username>${NEXUS_USER}</username>
      <password>${NEXUS_PASS}</password>
    </server>
    <server>
      <id>nexus-snapshots</id>
      <username>${NEXUS_USER}</username>
      <password>${NEXUS_PASS}</password>
    </server>
  </servers>
</settings>
"""
                }
            }
        }

        stage('Nexus - Publication') {
            steps {
                sh """
                    mvn clean deploy -s settings.xml \
                    -DskipTests \
                    -DaltDeploymentRepository=nexus-releases::default::${NEXUS_URL}/repository/maven-releases/ \
                    || echo "⚠️ JAR déjà dans Nexus - on continue"
                """
            }
        }

        stage('Get JAR from Nexus') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'nexus-cred',
                    usernameVariable: 'NEXUS_USER',
                    passwordVariable: 'NEXUS_PASS'
                )]) {
                    sh """
                        mkdir -p target
                        curl -f -u \${NEXUS_USER}:\${NEXUS_PASS} \
                             "${NEXUS_JAR_URL}" \
                             -o target/${JAR_NAME}
                        echo "✅ JAR récupéré depuis Nexus"
                        ls -lh target/${JAR_NAME}
                    """
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh """
                    echo "=== Vérifier JAR ==="
                    ls -lh target/${JAR_NAME}

                    echo "=== Vérifier Dockerfile ==="
                    cat Dockerfile

                    echo "=== Build Docker Image ==="
                    docker build \
                        --build-arg JAR_FILE=${JAR_NAME} \
                        --build-arg APP_PORT=${APP_PORT} \
                        -t ${IMAGE_NAME} .

                    echo "✅ Image Docker créée : ${IMAGE_NAME}"
                    docker images | grep ${APP_NAME}
                """
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                sh """
                    docker compose down --remove-orphans || true
                    docker compose up -d
                    echo "✅ Application déployée !"
                    sleep 5
                    docker ps
                """
            }
        }
    }

    post {
        always {
            echo 'Pipeline terminé'
        }
        success {
            echo """
            ✅ Pipeline réussi !
            ─────────────────────────────
            🌐 Application  : http://${VM_IP}:${APP_PORT}
            📦 Nexus        : http://${NEXUS_IP}:${NEXUS_PORT}
            🔍 SonarQube    : http://${VM_IP}:9000
            🐳 Image Docker : ${IMAGE_NAME}
            ─────────────────────────────
            """
        }
        failure {
            echo '❌ Pipeline échoué - vérifiez les logs'
        }
    }
}
