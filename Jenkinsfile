pipeline {
    agent any

    tools {
        maven 'maven-3.9.4'
        jdk   'java-17'
    }

    parameters {
        string(name: 'VM_IP',         defaultValue: '',             description: 'IP de la VM Jenkins/Docker')
        string(name: 'NEXUS_IP',      defaultValue: '',             description: 'IP du serveur Nexus')
        string(name: 'NEXUS_PORT',    defaultValue: '8081',         description: 'Port de Nexus')
        string(name: 'APP_NAME',      defaultValue: 'achat',        description: 'Nom de l application')
        string(name: 'APP_VERSION',   defaultValue: '1.1',          description: 'Version du JAR')
        string(name: 'GROUP_ID_PATH', defaultValue: 'tn/esprit/rh', description: 'Chemin groupId dans Nexus')
        string(name: 'APP_PORT',      defaultValue: '8082',         description: 'Port de l application')
        string(name: 'IMAGE_TAG',     defaultValue: '1.0.0',        description: 'Tag de l image Docker')
    }

    environment {
        IMAGE_NAME = "${params.APP_NAME}:${params.IMAGE_TAG}"
        JAR_NAME   = "${params.APP_NAME}-${params.APP_VERSION}.jar"
    }

    stages {

        stage('Checkout SCM') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/ADEMABESSI/Achat-Devops.git'
            }
        }

        stage('Tool Install') {
            steps {
                sh 'mvn --version'
                sh 'java -version'
                sh 'docker --version'
            }
        }

        stage('Verifier Dockerfile') {
            steps {
                sh '''
                    if [ -f docker/Dockerfile ]; then
                        echo "Dockerfile trouve !"
                    else
                        echo "Dockerfile NON trouve !"
                        exit 1
                    fi
                '''
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
                sh 'ls -lh target/$JAR_NAME'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('OWASP Dependency Check') {
    steps {
        withCredentials([string(
            credentialsId: 'nvd-api-key',  // ← mets l'ID exact ici
            variable: 'NVD_API_KEY'
        )]) {
            sh """
                mvn org.owasp:dependency-check-maven:check \
                    -DnvdApiKey=\${NVD_API_KEY} \
                    -DfailBuildOnCVSS=11 \
                    -Dformat=HTML \
                    || echo "⚠️ Vulnérabilités détectées - on continue"
            """
        }
    }
    post {
        always {
            publishHTML([
                allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'target',
                reportFiles: 'dependency-check-report.html',
                reportName: 'OWASP Dependency Check Report'
            ])
        }
    }
}
        stage('SonarQube - Analyse qualite') {
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
                    sh '''
                        cat > settings.xml << EOF
<settings>
  <servers>
    <server>
      <id>nexus-releases</id>
      <username>$NEXUS_USER</username>
      <password>$NEXUS_PASS</password>
    </server>
  </servers>
</settings>
EOF
                    '''
                }
            }
        }

        stage('Nexus - Publication') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'nexus-cred',
                    usernameVariable: 'NEXUS_USER',
                    passwordVariable: 'NEXUS_PASS'
                )]) {
                    sh '''
                        NEXUS_HTTP="http://$NEXUS_IP:$NEXUS_PORT"
                        echo "=== Publication vers : $NEXUS_HTTP ==="
                        mvn clean deploy -s settings.xml \
                            -DskipTests \
                            -DaltDeploymentRepository=nexus-releases::default::$NEXUS_HTTP/repository/maven-releases/ \
                            || echo "JAR deja dans Nexus - on continue"
                    '''
                }
            }
            post {
                always {
                    sh 'rm -f settings.xml'
                    echo 'settings.xml supprime'
                }
            }
        }

        stage('Get JAR from Nexus') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'nexus-cred',
                    usernameVariable: 'NEXUS_USER',
                    passwordVariable: 'NEXUS_PASS'
                )]) {
                    sh '''
                        mkdir -p target
                        NEXUS_HTTP="http://$NEXUS_IP:$NEXUS_PORT"
                        JAR_URL="$NEXUS_HTTP/repository/maven-releases/$GROUP_ID_PATH/$APP_NAME/$APP_VERSION/$APP_NAME-$APP_VERSION.jar"
                        echo "=== URL utilisee : $JAR_URL ==="
                        curl -f --silent --show-error \
                             -u $NEXUS_USER:$NEXUS_PASS \
                             "$JAR_URL" \
                             -o target/$APP_NAME-$APP_VERSION.jar
                        echo "JAR recupere !"
                        ls -lh target/
                    '''
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh '''
                    echo "=== Verification JAR ==="
                    ls -lh target/$JAR_NAME
                    echo "=== Build Docker Image ==="
                    docker build \
                        -f docker/Dockerfile \
                        --build-arg JAR_FILE=$JAR_NAME \
                        --build-arg APP_PORT=$APP_PORT \
                        --no-cache \
                        -t $IMAGE_NAME .
                    echo "Image creee : $IMAGE_NAME"
                    docker images | grep "$APP_NAME"
                '''
            }
        }

     stage('Trivy - Scan Image') {
    steps {
        sh '''
            trivy image \
              --timeout 30m \
              --skip-version-check \
              --severity HIGH,CRITICAL \
              --exit-code 1 \
              achat:1.0.0
        '''
    }
}

        stage('Deploy with Docker Compose') {
            steps {
                sh '''
                    docker compose down --remove-orphans || true
                    docker compose up -d
                    echo "Application deployee !"
                    sleep 15
                    docker ps
                '''
            }
        }
    }

    post {
        always {
            sh 'rm -f settings.xml || true'
            echo 'Pipeline termine'
        }
        success {
            echo "Pipeline reussi ! Application sur port $APP_PORT"
        }
        failure {
            echo 'Pipeline echoue - verifiez les logs'
        }
    }
}
