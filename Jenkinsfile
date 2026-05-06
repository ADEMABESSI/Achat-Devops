pipeline {
    agent any

    tools {
        maven 'maven-3.9.4'
        jdk   'java-17'
    }

    parameters {
        string(name: 'VM_IP',         defaultValue: '',           description: 'IP de la VM Jenkins/Docker')
        string(name: 'NEXUS_IP',      defaultValue: '',           description: 'IP du serveur Nexus')
        string(name: 'NEXUS_PORT',    defaultValue: '8081',       description: 'Port de Nexus')
        string(name: 'APP_NAME',      defaultValue: 'achat',      description: 'Nom de l application')
        string(name: 'APP_VERSION',   defaultValue: '1.1',        description: 'Version du JAR')
        string(name: 'GROUP_ID_PATH', defaultValue: 'tn/esprit/rh', description: 'Chemin groupId dans Nexus')
        string(name: 'APP_PORT',      defaultValue: '8082',       description: 'Port de l application')
        string(name: 'IMAGE_TAG',     defaultValue: '1.0.0',      description: 'Tag de l image Docker')
    }

    environment {
        NEXUS_URL     = "https://${params.NEXUS_IP}:${params.NEXUS_PORT}"
        IMAGE_NAME    = "${params.APP_NAME}:${params.IMAGE_TAG}"
        JAR_NAME      = "${params.APP_NAME}-${params.APP_VERSION}.jar"
        NEXUS_JAR_URL = "${NEXUS_URL}/repository/maven-releases/${params.GROUP_ID_PATH}/${params.APP_NAME}/${params.APP_VERSION}/${JAR_NAME}"
        NEXUS_CREDS     = credentials('nexus-cred')
        SONAR_TOKEN_VAL = credentials('sonar-token')
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
                        echo "Dockerfile trouve"
                    else
                        echo "Dockerfile NON trouve"
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
                sh 'mvn org.owasp:dependency-check-maven:check -DfailBuildOnCVSS=7 -DskipTests'
            }
            post {
                always {
                    publishHTML([
                        allowMissing: true,
                        reportDir: 'target',
                        reportFiles: 'dependency-check-report.html',
                        reportName: 'OWASP Dependency Report'
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
                        sh '''
                            mvn sonar:sonar \
                                -Dsonar.login=$SONAR_TOKEN \
                                -Dsonar.security.sources=src/main/java
                        '''
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
                sh '''
                    mvn clean deploy -s settings.xml \
                        -DskipTests \
                        -DaltDeploymentRepository=nexus-releases::default::$NEXUS_URL/repository/maven-releases/ \
                        || echo "JAR deja dans Nexus - on continue"
                '''
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
                        curl -f --silent --show-error \
                             -u $NEXUS_USER:$NEXUS_PASS \
                             "$NEXUS_JAR_URL" \
                             -o target/$JAR_NAME
                        echo "JAR recupere depuis Nexus"
                        ls -lh target/$JAR_NAME
                    '''
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh '''
                    ls -lh target/$JAR_NAME
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

        stage('Trivy - Scan Image Docker') {
            steps {
                sh '''
                    docker run --rm \
                        -v /var/run/docker.sock:/var/run/docker.sock \
                        aquasec/trivy:latest image \
                        --exit-code 0 \
                        --severity HIGH,CRITICAL \
                        --format table \
                        $IMAGE_NAME
                '''
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                sh '''
                    docker compose down --remove-orphans || true
                    docker compose up -d
                    sleep 15
                    docker ps
                '''
            }
        }
    }

    post {
        always {
            sh 'rm -f settings.xml'
            echo 'Pipeline termine'
        }
        success {
            echo "Pipeline reussi - port ${params.APP_PORT}"
        }
        failure {
            echo 'Pipeline echoue - verifiez les logs'
        }
    }
}
