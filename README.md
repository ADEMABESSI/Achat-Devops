# 🚀 Achat DevOps — Chaîne CI/CD Complète

> Industrialisation, automatisation et sécurisation d'une application Spring Boot via une chaîne CI/CD complète.

![Jenkins](https://img.shields.io/badge/Jenkins-CI%2FCD-D24939?logo=jenkins&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Conteneurisation-2496ED?logo=docker&logoColor=white)
![SonarQube](https://img.shields.io/badge/SonarQube-Qualité-4E9BCD?logo=sonarqube&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.18-6DB33F?logo=springboot&logoColor=white)
![Prometheus](https://img.shields.io/badge/Prometheus-Monitoring-E6522C?logo=prometheus&logoColor=white)
![Grafana](https://img.shields.io/badge/Grafana-Dashboards-F46800?logo=grafana&logoColor=white)

---

## 📋 Table des matières

- [Architecture](#architecture)
- [Prérequis](#prérequis)
- [Installation rapide](#installation-rapide)
- [Services et URLs](#services-et-urls)
- [Pipeline Jenkins](#pipeline-jenkins)
- [Docker Compose](#docker-compose)
- [Monitoring](#monitoring)
- [Sécurité](#sécurité)
- [Structure du projet](#structure-du-projet)

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    MACHINE UBUNTU (172.17.0.1)               │
│                                                              │
│  GitHub ──► Jenkins:8080 ──► SonarQube:9000                 │
│                    │                                         │
│                    ├──► Nexus:8081 (artéfacts)               │
│                    │                                         │
│                    └──► Docker Compose                       │
│                              ├── app-achat:8082              │
│                              ├── mysqldb:3306                │
│                              ├── prometheus:9090             │
│                              ├── grafana:3000                │
│                              └── node-exporter:9100          │
└─────────────────────────────────────────────────────────────┘
```

---

## ✅ Prérequis

| Outil | Version minimale | Vérification |
|---|---|---|
| Java JDK | 17 | `java -version` |
| Maven | 3.9+ | `mvn --version` |
| Docker | 24+ | `docker --version` |
| Docker Compose | 2.x | `docker compose version` |
| Jenkins | 2.400+ | Interface web |
| Git | 2.x | `git --version` |

---

## ⚡ Installation rapide

### 1. Cloner le projet

```bash
git clone https://github.com/ADEMABESSI/Achat-Devops.git
cd Achat-Devops
```

### 2. Builder l'application

```bash
mvn clean package -DskipTests
```

### 3. Lancer tous les services avec Docker Compose

```bash
docker compose up -d
```

### 4. Vérifier que tout tourne

```bash
docker ps
```

Résultat attendu :
```
CONTAINER ID   IMAGE                    PORTS
xxxx           achat:1.0.0              0.0.0.0:8082->8082/tcp
xxxx           mysql:5.7                0.0.0.0:3306->3306/tcp
xxxx           prom/prometheus          0.0.0.0:9090->9090/tcp
xxxx           grafana/grafana          0.0.0.0:3000->3000/tcp
xxxx           prom/node-exporter       0.0.0.0:9100->9100/tcp
```

---

## 🌐 Services et URLs

| Service | URL | Credentials |
|---|---|---|
| **Application** | http://172.17.0.1:8082 | — |
| **Jenkins** | http://172.17.0.1:8080 | admin / (configuré) |
| **SonarQube** | http://172.17.0.1:9000 | admin / admin |
| **Nexus** | http://172.17.0.1:8081 | admin / admin123 |
| **Grafana** | http://172.17.0.1:3000 | admin / admin |
| **Prometheus** | http://172.17.0.1:9090 | — |
| **Métriques App** | http://172.17.0.1:8082/actuator/prometheus | — |

---

## 🔄 Pipeline Jenkins

Le pipeline contient **12 stages** automatisés :

```
Checkout SCM
    ↓
Vérifier Dockerfile
    ↓
Tool Install (Maven + Java)
    ↓
Build (mvn clean package)
    ↓
Test (mvn test - JUnit)
    ↓
OWASP Dependency Check ──► Rapport CVE
    ↓
SonarQube Analyse ──────► Rapport qualité
    ↓
Prepare Maven Settings
    ↓
Nexus Publication ──────► JAR versionné
    ↓
Get JAR from Nexus
    ↓
Build Docker Image
    ↓
Trivy Scan ─────────────► Rapport sécurité image
    ↓
Deploy with Docker Compose
    ↓
Post Actions (notifications)
```

### Déclencher le pipeline manuellement

```
Jenkins → jenkinsfiles → Build Now
```

### Configurer le webhook GitHub (déclenchement automatique)

```
GitHub → Settings → Webhooks → Add webhook
Payload URL : http://172.17.0.1:8080/github-webhook/
Content type : application/json
```

---

## 🐳 Docker Compose

### Lancer tous les services

```bash
docker compose up -d
```

### Arrêter tous les services

```bash
docker compose down
```

### Voir les logs d'un service

```bash
docker compose logs -f app-achat
docker compose logs -f mysqldb
```

### Rebuilder l'image de l'application

```bash
# Après modification du code
mvn clean package -DskipTests
docker build -f docker/Dockerfile --build-arg JAR_FILE=achat-1.1.jar -t achat:1.0.0 .
docker compose up -d app-achat
```

### Variables d'environnement

| Variable | Valeur par défaut | Description |
|---|---|---|
| `MYSQL_ROOT_PASSWORD` | root | Mot de passe MySQL root |
| `MYSQL_DATABASE` | achat | Nom de la base de données |
| `APP_PORT` | 8082 | Port de l'application |
| `SPRING_DATASOURCE_URL` | jdbc:mysql://mysqldb:3306/achat | URL de connexion |

---

## 📊 Monitoring

### Prometheus

Accéder à l'interface : http://172.17.0.1:9090

**Requêtes PromQL utiles :**

```promql
# Disponibilité des services
up

# Utilisation CPU
100 - (avg by (instance)(rate(node_cpu_seconds_total{mode="idle"}[2m])) * 100)

# Utilisation RAM
(node_memory_MemTotal_bytes - node_memory_MemAvailable_bytes) / node_memory_MemTotal_bytes * 100

# Builds Jenkins
jenkins_builds_last_build_result
```

### Grafana — Dashboards importés

| Dashboard | ID | Description |
|---|---|---|
| Node Exporter Full | `1860` | Métriques système Linux |
| Jenkins Performance | `9964` | Performance pipeline CI/CD |
| Spring Boot Stats | `11378` | Métriques applicatives |

**Importer un dashboard :**
```
Grafana → Dashboards → New → Import → entrer l'ID → Load → Import
```

---

## 🔐 Sécurité

### Outils de sécurité intégrés

#### Trivy (Scan image Docker)
```bash
# Lancer manuellement
docker run --rm \
  -v /var/run/docker.sock:/var/run/docker.sock \
  aquasec/trivy image achat:1.0.0
```

#### OWASP Dependency Check
```bash
# Lancer manuellement (avec clé NVD)
mvn dependency-check:check \
  -DnvdApiKey=VOTRE_CLE \
  -DnvdApiDelay=6000 \
  -Dformat=HTML
```

Obtenir une clé NVD gratuite : https://nvd.nist.gov/developers/request-an-api-key

### Résumé des vulnérabilités (Trivy)

| Sévérité | Nombre | Principaux composants |
|---|---|---|
| 🔴 CRITICAL | 6 | Tomcat 9.0.50, Spring4Shell, spring-web |
| 🟠 HIGH | 37 | Logback, Jackson, Hibernate, SnakeYaml |
| 🟡 MEDIUM | 36 | MySQL connector, Tomcat, Spring |
| 🟢 LOW | 9 | Tomcat, Spring context |

**Recommandation principale :** Mettre à jour Spring Boot vers 2.7.18+ corrige ~70% des CVE.

### Jenkins Credentials configurés

| ID | Type | Usage |
|---|---|---|
| `nexus-cred` | Username/Password | Publication Nexus |
| `sonar-token` | Secret text | Analyse SonarQube |
| `nvd-api-key` | Secret text | OWASP Dependency Check |

---

## 📁 Structure du projet

```
Achat-Devops/
├── docker/
│   └── Dockerfile              # Image Docker de l'application
├── monitoring/
│   └── prometheus.yml          # Configuration Prometheus
├── src/
│   ├── main/
│   │   ├── java/tn/esprit/rh/  # Code source Spring Boot
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/               # Tests JUnit
├── docker-compose.yml          # Orchestration des services
├── Jenkinsfile                 # Pipeline CI/CD
├── pom.xml                     # Dépendances Maven
└── README.md                   # Documentation
```

---

## 🔧 Dépannage

### L'application ne démarre pas

```bash
# Vérifier les logs
docker compose logs app-achat

# Vérifier que MySQL est healthy
docker ps | grep mysql

# Redémarrer les services
docker compose restart
```

### MySQL unhealthy

```bash
# Vérifier l'état
docker inspect mysqldb | grep Health

# Voir les logs MySQL
docker compose logs mysqldb
```

### Pipeline Jenkins échoue

```bash
# Nettoyer le workspace Jenkins
# Jenkins → Pipeline → Workspace → Wipe out current workspace
# Puis relancer le build
```

### SonarQube inaccessible

```bash
# Vérifier que SonarQube tourne
docker ps | grep sonar

# L'URL dans Jenkins doit être :
# http://172.17.0.1:9000 (PAS 192.168.x.x)
```

---

## 👥 Équipe

Projet réalisé dans le cadre du module **DevOps** — SESAME Technology

| Rôle | Responsabilité |
|---|---|
| Product Owner | Priorités backlog, validation livrables |
| Scrum Master | Facilitation, suivi avancement |
| Développeur DevOps | Pipeline Jenkins, Docker, Monitoring |
| Développeur Sécurité | OWASP, Trivy, rapport sécurité |

---

## 📄 Licence

Ce projet est réalisé dans un cadre pédagogique — SESAME Technology 2026.
