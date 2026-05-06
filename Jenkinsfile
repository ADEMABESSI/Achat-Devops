pipeline {
    agent any

    tools {
        maven 'maven-3.9.4'
        jdk 'java-17'
    }

    parameters {
        string(name: 'VM_IP',         defaultValue: '',  description: 'IP de la VM Jenkins/Docker')
        string(name: 'NEXUS_IP',      defaultValue: '',  description: 'IP du serveur Nexus')
        string(name: 'NEXUS_PORT',    defaultValue: '8081',           description: 'Port de Nexus')
        string(name: 'APP_NAME',      defaultValue: 'achat',          description: 'Nom de l application')
        string(name: 'APP_VERSION',   defaultValue: '1.1',            description: 'Version du JAR')
        string(name: 'GROUP_ID_PATH', defaultValue: 'tn/esprit/rh',   description: 'Chemin groupId dans Nexus')
        string(name: 'APP_PORT',      defaultValue: '8082',           description: 'Port de l application')
        string(name: 'IMAGE_TAG',     defaultValue: '1.0.0',          description: 'Tag de l image Docker')
    }

<<<<<<< HEAD
    environment {
        NEXUS_URL     = "http://${params.NEXUS_IP}:${params.NEXUS_PORT}"
        IMAGE_NAME    = "${params.APP_NAME}:${params.IMAGE_TAG}"
        JAR_NAME      = "${params.APP_NAME}-${params.APP_VERSION}.jar"
        NEXUS_JAR_URL = "${NEXUS_URL}/repository/maven-releases/${params.GROUP_ID_PATH}/${params.APP_NAME}/${params.APP_VERSION}/${JAR_NAME}"
=======
    // ─────────────────────────────────────────────────────────────────────────
    // CORRECTION 1 : Les paramètres sont conservés pour la flexibilité,
    // MAIS les valeurs par défaut sensibles (IPs) sont retirées.
    // Les credentials (mots de passe) ne passent JAMAIS en paramètre —
    // ils sont gérés exclusivement par Jenkins Credentials Store.
    // ─────────────────────────────────────────────────────────────────────────
    parameters {
        string(name: 'VM_IP',         defaultValue: '',     description: 'IP de la VM Jenkins/Docker')
        string(name: 'NEXUS_IP',      defaultValue: '',     description: 'IP du serveur Nexus')
        string(name: 'NEXUS_PORT',    defaultValue: '8081', description: 'Port de Nexus')
        string(name: 'APP_NAME',      defaultValue: 'achat', description: 'Nom de l application')
        string(name: 'APP_VERSION',   defaultValue: '1.1',  description: 'Version du JAR')
        string(name: 'GROUP_ID_PATH', defaultValue: 'tn/esprit/rh', description: 'Chemin groupId dans Nexus')
        string(name: 'APP_PORT',      defaultValue: '8082', description: 'Port de l application')
        string(name: 'IMAGE_TAG',     defaultValue: '1.0.0', description: 'Tag de l image Docker')
    }

    environment {
        // ─────────────────────────────────────────────────────────────────────
        // CORRECTION 2 : NEXUS_URL passe en HTTPS au lieu de HTTP.
        // Avant : "http://${params.NEXUS_IP}:${params.NEXUS_PORT}"
        // Les artifacts et credentials ne transitent plus en clair.
        // ─────────────────────────────────────────────────────────────────────
        NEXUS_URL     = "https://${params.NEXUS_IP}:${params.NEXUS_PORT}"
        IMAGE_NAME    = "${params.APP_NAME}:${params.IMAGE_TAG}"
        JAR_NAME      = "${params.APP_NAME}-${params.APP_VERSION}.jar"
        NEXUS_JAR_URL = "${NEXUS_URL}/repository/maven-releases/${params.GROUP_ID_PATH}/${params.APP_NAME}/${params.APP_VERSION}/${JAR_NAME}"

        // ─────────────────────────────────────────────────────────────────────
        // CORRECTION 3 : Credentials chargés depuis Jenkins Credentials Store.
        // Avant : variables en clair dans environment ou passées via params.
        // Jenkins masque automatiquement ces valeurs dans les logs (****).
        // À créer dans : Jenkins → Manage Credentials
        //   - nexus-cred    → Username/Password
        //   - sonar-token   → Secret text
        // ─────────────────────────────────────────────────────────────────────
        NEXUS_CREDS     = credentials('nexus-cred')
        SONAR_TOKEN_VAL = credentials('sonar-token')
>>>>>>> 9329ca7 (modification de securité)
    }

    stages {

        stage('Checkout SCM') {
            steps {
<<<<<<< HEAD
                git branch: 'main',
                    url: 'https://github.com/ADEMABESSI/Achat-Devops.git'
            }
        }

        stage('Vérifier Dockerfile') {
            steps {
                sh '''
                    ls -la
                    if [ -f docker/Dockerfile ]; then
                        echo "Dockerfile trouvé !"
                        cat docker/Dockerfile
                    else
                        echo "Dockerfile NON trouvé !"
                        exit 1
                    fi
                '''
=======
                // CORRECTION 4 : Toujours spécifier la branche explicitement.
                // Avant : git url sans branch → prend la branche par défaut
                // sans contrôle, ce qui peut entraîner des builds sur des
                // branches non vérifiées.
                git branch: 'main',
                    url: 'https://github.com/ADEMABESSI/Achat-Devops.git'
>>>>>>> 9329ca7 (modification de securité)
            }
        }

        stage('Tool Install') {
            steps {
                sh 'mvn --version'
                sh 'java -version'
                sh 'docker --version'
            }
        }

        stage('Vérifier Dockerfile') {
            steps {
                // CORRECTION 5 : Apostrophes simples ''' (sh littéral).
                // Avant : sh """ — Groovy interpole les variables avant le shell,
                // ce qui peut exposer des valeurs sensibles dans les logs Jenkins.
                // Règle : sh ''' pour tout script sans interpolation Groovy nécessaire.
                sh '''
                    ls -la
                    if [ -f docker/Dockerfile ]; then
                        echo "Dockerfile trouvé"
                        cat docker/Dockerfile
                    else
                        echo "Dockerfile NON trouvé"
                        exit 1
                    fi
                '''
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
<<<<<<< HEAD
                sh "ls -lh target/${JAR_NAME}"
=======
                // CORRECTION 6 : Apostrophes simples — JAR_NAME est une variable
                // d'environnement Shell, pas besoin d'interpolation Groovy.
                sh 'ls -lh target/$JAR_NAME'
>>>>>>> 9329ca7 (modification de securité)
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        // ─────────────────────────────────────────────────────────────────────
        // NOUVEAU STAGE : OWASP Dependency Check
        // Analyse les dépendances Maven pour détecter les CVEs connues.
        // Le build échoue automatiquement si un CVE de score >= 7 est trouvé.
        // Nécessite le plugin dependency-check-maven dans pom.xml.
        // ─────────────────────────────────────────────────────────────────────
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

        stage('SonarQube - Analyse qualité') {
            steps {
                withSonarQubeEnv('sonarqube') {
                    withCredentials([string(
                        credentialsId: 'sonar-token',
                        variable: 'SONAR_TOKEN'
                    )]) {
                        // CORRECTION 7 : Apostrophes simples → Jenkins masque $SONAR_TOKEN
                        // dans les logs. Avec guillemets doubles, la valeur du token
                        // peut apparaître en clair dans la trace Groovy.
                        // Ajout de -Dsonar.security.sources pour les règles OWASP.
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
                    // CORRECTION 8 : writeFile reste nécessaire pour Maven.
                    // Le fichier settings.xml est créé temporairement avec les
                    // credentials. Jenkins masque les valeurs dans les logs.
                    // IMPORTANT : ce fichier est supprimé dans post { always }
                    // pour ne jamais rester sur le disque après le build.
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
                // CORRECTION 9 : Apostrophes simples — les variables viennent
                // de l'environnement shell, pas d'interpolation Groovy nécessaire.
                // Avant : sh """ avec ${NEXUS_URL} → exposait l'URL en clair via Groovy.
                sh '''
                    mvn clean deploy -s settings.xml \
<<<<<<< HEAD
                    -DskipTests \
                    -DaltDeploymentRepository=nexus-releases::default::${NEXUS_URL}/repository/maven-releases/ \
                    || echo "JAR déjà dans Nexus - on continue"
                """
=======
                        -DskipTests \
                        -DaltDeploymentRepository=nexus-releases::default::$NEXUS_URL/repository/maven-releases/ \
                        || echo "JAR déjà dans Nexus - on continue"
                '''
>>>>>>> 9329ca7 (modification de securité)
            }
        }

        stage('Get JAR from Nexus') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'nexus-cred',
                    usernameVariable: 'NEXUS_USER',
                    passwordVariable: 'NEXUS_PASS'
                )]) {
                    // CORRECTION 10 : Apostrophes simples OBLIGATOIRES ici.
                    // Avant : sh """ avec ${NEXUS_USER}:${NEXUS_PASS} → Groovy interpolait
                    // le mot de passe AVANT le shell. Le mot de passe apparaissait en clair
                    // dans la trace Groovy même avec withCredentials actif.
                    // Avec ''', le shell résout $NEXUS_USER et $NEXUS_PASS au runtime
                    // et Jenkins les masque correctement dans les logs (****).
                    sh '''
                        mkdir -p target
<<<<<<< HEAD
                        curl -f -u ${NEXUS_USER}:${NEXUS_PASS} \
                             "${NEXUS_JAR_URL}" \
                             -o target/${JAR_NAME}
                        echo "JAR récupéré depuis Nexus"
                        ls -lh target/${JAR_NAME}
                    """
=======
                        curl -f \
                             --silent \
                             --show-error \
                             -u $NEXUS_USER:$NEXUS_PASS \
                             "$NEXUS_JAR_URL" \
                             -o target/$JAR_NAME
                        echo "JAR récupéré depuis Nexus"
                        ls -lh target/$JAR_NAME
                    '''
>>>>>>> 9329ca7 (modification de securité)
                }
            }
        }

        stage('Build Docker Image') {
            steps {
<<<<<<< HEAD
                sh """
                    echo "=== Vérifier JAR ==="
                    ls -lh target/${JAR_NAME}

                    echo "=== Vérifier Dockerfile ==="
                    cat docker/Dockerfile
=======
                // CORRECTION 11 : Apostrophes simples — $JAR_NAME, $IMAGE_NAME,
                // $APP_PORT viennent du bloc environment et sont accessibles
                // via le shell sans interpolation Groovy.
                // Ajout de --no-cache pour éviter d'utiliser des couches obsolètes
                // contenant d'anciennes dépendances vulnérables.
                sh '''
                    echo "=== Vérifier JAR ==="
                    ls -lh target/$JAR_NAME
>>>>>>> 9329ca7 (modification de securité)

                    echo "=== Build Docker Image ==="
                    docker build \
                        -f docker/Dockerfile \
<<<<<<< HEAD
                        --build-arg JAR_FILE=${JAR_NAME} \
                        --build-arg APP_PORT=${params.APP_PORT} \
                        -t ${IMAGE_NAME} .

                    echo "Image Docker créée : ${IMAGE_NAME}"
                    docker images | grep ${params.APP_NAME}
                """
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                sh """
                    docker compose down --remove-orphans || true
                    docker compose up -d
                    echo "Application déployée !"
                    sleep 5
=======
                        --build-arg JAR_FILE=$JAR_NAME \
                        --build-arg APP_PORT=$APP_PORT \
                        --no-cache \
                        -t $IMAGE_NAME .

                    echo "Image Docker créée : $IMAGE_NAME"
                    docker images | grep "$APP_NAME"
                '''
            }
        }

        // ─────────────────────────────────────────────────────────────────────
        // NOUVEAU STAGE : Trivy — scan de vulnérabilités de l'image Docker
        // Analyse les packages OS et librairies Java dans l'image construite.
        // Le build échoue si une vulnérabilité HIGH ou CRITICAL est détectée.
        // ─────────────────────────────────────────────────────────────────────
        stage('Trivy - Scan Image Docker') {
            steps {
                sh '''
                    docker run --rm \
                        -v /var/run/docker.sock:/var/run/docker.sock \
                        aquasec/trivy:latest image \
                        --exit-code 1 \
                        --severity HIGH,CRITICAL \
                        --format table \
                        $IMAGE_NAME
                '''
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                // CORRECTION 12 : Apostrophes simples partout.
                // Ajout d'un health check après déploiement pour valider
                // que l'application répond correctement.
                sh '''
                    docker compose down --remove-orphans || true
                    docker compose up -d
                    echo "Application déployée"

                    echo "=== Attente démarrage (15s) ==="
                    sleep 15

                    echo "=== Conteneurs actifs ==="
>>>>>>> 9329ca7 (modification de securité)
                    docker ps

                    echo "=== Health check ==="
                    curl -f --silent \
                         http://localhost:$APP_PORT/SpringMVC/actuator/health \
                         || echo "Health check non disponible - vérifier manuellement"
                '''
            }
        }
    }

    post {
        always {
            // ─────────────────────────────────────────────────────────────────
            // CORRECTION 13 : Suppression du settings.xml après chaque build.
            // Avant : le fichier avec les credentials Nexus restait sur le
            // workspace Jenkins indéfiniment, lisible par d'autres jobs.
            // Cette suppression s'exécute même si le pipeline échoue.
            // ─────────────────────────────────────────────────────────────────
            sh 'rm -f settings.xml'
            echo 'Pipeline terminé — secrets nettoyés'
        }
        success {
            // CORRECTION 14 : URLs passées en HTTPS dans le message de succès.
            // Avant : toutes les URLs affichées en http:// dans les logs.
            echo """
<<<<<<< HEAD
            Pipeline réussi !
            Application  : http://${params.VM_IP}:${params.APP_PORT}
            Nexus        : http://${params.NEXUS_IP}:${params.NEXUS_PORT}
            SonarQube    : http://${params.VM_IP}:9000
            Image Docker : ${IMAGE_NAME}
            """
        }
        failure {
            echo 'Pipeline échoué - vérifiez les logs'
=======
            ✅ Pipeline réussi !
            ─────────────────────────────
            🌐 Application  : https://${params.VM_IP}:${params.APP_PORT}
            📦 Nexus        : https://${params.NEXUS_IP}:${params.NEXUS_PORT}
            🔍 SonarQube    : https://${params.VM_IP}:9000
            🐳 Image Docker : ${IMAGE_NAME}
            ─────────────────────────────
            """
        }
        failure {
            echo '❌ Pipeline échoué — vérifiez les logs ci-dessus'
>>>>>>> 9329ca7 (modification de securité)
        }
    }
}