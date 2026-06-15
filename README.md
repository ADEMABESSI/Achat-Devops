# 🚀 Achat DevOps — Chaîne CI/CD Complète

Industrialisation, automatisation et sécurisation d'une application Spring Boot via une chaîne CI/CD complète avec monitoring, alerting et tests automatisés.

![Jenkins](https://img.shields.io/badge/Jenkins-CI%2FCD-red)
![Docker](https://img.shields.io/badge/Docker-Containerisation-blue)
![SonarQube](https://img.shields.io/badge/SonarQube-Qualité-green)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-Java-brightgreen)
![Prometheus](https://img.shields.io/badge/Prometheus-Monitoring-orange)
![Grafana](https://img.shields.io/badge/Grafana-Dashboards-yellow)
![MailHog](https://img.shields.io/badge/MailHog-Alerting-lightgrey)
![JUnit](https://img.shields.io/badge/JUnit-Tests-blue)

---

## 📋 Table des matières

- [Architecture](#architecture)
- [Prérequis](#prérequis)
- [Installation rapide](#installation-rapide)
- [Services et URLs](#services-et-urls)
- [Pipeline Jenkins](#pipeline-jenkins)
- [Tests Unitaires](#tests-unitaires)
- [Monitoring](#monitoring)
- [Alerting avec MailHog](#alerting-avec-mailhog)
- [Sécurité](#sécurité)
- [Structure du projet](#structure-du-projet)

---

## 🏗️ Architecture

```
┌──────────────────────────────────────────────────────────────────┐
│                    MACHINE UBUNTU (172.17.0.1)                    │
│                                                                   │
│  GitHub ──► Jenkins:8080 ──► SonarQube:9000                      │
│                    │                                              │
│                    ├──► Nexus:8081        (artefacts Maven)       │
│                    ├──► OWASP Check       (CVE dépendances)       │
│                    ├──► Trivy             (scan image Docker)     │
│                    ├──► JUnit + JaCoCo   (tests + coverage)      │
│                    │                                              │
│                    └──► Docker Compose                            │
│                              ├── app-achat:8082                   │
│                              ├── mysqldb:3306                     │
│                              ├── prometheus:9090                  │
│                              ├── grafana:3000                     │
│                              ├── node-exporter:9100               │
│                              └── mailhog:8025 (alertes email)     │
└──────────────────────────────────────────────────────────────────┘
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

### 3. Lancer tous les services
```bash
docker compose up -d
```

### 4. Vérifier que tout tourne
```bash
docker ps
```

Résultat attendu :
```
CONTAINER ID   IMAGE                  PORTS
xxxx           achat:1.0.0            0.0.0.0:8082->8082/tcp
xxxx           mysql:5.7              0.0.0.0:3306->3306/tcp
xxxx           prom/prometheus        0.0.0.0:9090->9090/tcp
xxxx           grafana/grafana        0.0.0.0:3000->3000/tcp
xxxx           prom/node-exporter     0.0.0.0:9100->9100/tcp
xxxx           mailhog/mailhog        0.0.0.0:8025->8025/tcp
```

---

## 🌐 Services et URLs

| Service | URL | Credentials |
|---|---|---|
| Application | http://172.17.0.1:8082/SpringMVC | — |
| Jenkins | http://172.17.0.1:8080 | admin / (configuré) |
| SonarQube | http://172.17.0.1:9000 | admin / admin |
| Nexus | http://172.17.0.1:8081 | admin / admin123 |
| Grafana | http://172.17.0.1:3000 | admin / admin |
| Prometheus | http://172.17.0.1:9090 | — |
| MailHog | http://172.17.0.1:8025 | — |
| Métriques App | http://172.17.0.1:8082/actuator/prometheus | — |

---

## 🔄 Pipeline Jenkins

Le pipeline contient **14 stages automatisés** :

```
Checkout SCM
    ↓
Tool Install (Maven + Java)
    ↓
Vérifier Dockerfile
    ↓
Build (mvn clean package)
    ↓
Tests Unitaires (JUnit + JaCoCo) ──► Rapport coverage
    ↓
OWASP Dependency Check ───────────► Rapport CVE dépendances
    ↓
SonarQube Analyse ────────────────► Rapport qualité code
    ↓
Prepare Maven Settings
    ↓
Nexus Publication ────────────────► JAR versionné
    ↓
Get JAR from Nexus
    ↓
Build Docker Image
    ↓
Trivy Scan ───────────────────────► Rapport sécurité image
    ↓
Deploy with Docker Compose
    ↓
Post Actions
```

### Déclencher le pipeline
```bash
# Manuellement
Jenkins → jenkinsfiles → Build Now

# Automatiquement via webhook GitHub
GitHub → Settings → Webhooks → Add webhook
Payload URL : http://172.17.0.1:8080/github-webhook/
Content type : application/json
```

---

## 🧪 Tests Unitaires

Les tests unitaires couvrent l'ensemble des **Controllers** et **Services** de l'application avec **JUnit 5** et **Mockito**.

### Structure des tests
```
src/test/java/tn/esprit/rh/achat/
├── controllers/
│   ├── CategorieProduitControllerTest.java
│   ├── FactureRestControllerTest.java
│   ├── FournisseurRestControllerTest.java
│   ├── OperateurControllerTest.java
│   ├── ProduitRestControllerTest.java
│   ├── ReglementRestControllerTest.java
│   ├── SecteurActiviteControllerTest.java
│   └── StockRestControllerTest.java
└── services/
    ├── CategorieProduitServiceImplTest.java
    ├── FactureServiceImplTest.java
    ├── FournisseurServiceImplTest.java
    ├── OperateurServiceImplTest.java
    ├── ProduitServiceImplTest.java
    ├── ReglementServiceImplTest.java
    ├── SecteurActiviteServiceImplTest.java
    └── StockServiceImplTest.java
```

### Lancer les tests
```bash
mvn test
```

### Coverage JaCoCo
Le coverage est calculé automatiquement par JaCoCo et envoyé à SonarQube à chaque build Jenkins.

```bash
# Rapport coverage local
mvn test
open target/site/jacoco/index.html
```

### Résultats
```
Tests run: 110, Failures: 0, Errors: 0, Skipped: 0
```

---

## 📊 Monitoring

### Prometheus

Accéder à l'interface : http://172.17.0.1:9090

Requêtes PromQL utiles :
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

### Grafana — Dashboards

Accéder à l'interface : http://172.17.0.1:3000

| Dashboard | ID | Description |
|---|---|---|
| Node Exporter Full | 1860 | Métriques système Linux (CPU, RAM, disque) |
| Jenkins Performance | 9964 | Performance et état du pipeline CI/CD |
| Spring Boot Stats | 11378 | Métriques applicatives Spring Boot |

**Importer un dashboard :**
```
Grafana → Dashboards → New → Import → entrer l'ID → Load → Import
```

---

## 📧 Alerting avec MailHog

MailHog est un serveur SMTP de test qui intercepte les **alertes Grafana** sans envoyer de vrais emails.

### Fonctionnement

```
Prometheus  →  collecte les métriques
     ↓
  Grafana   →  détecte un seuil dépassé (ex: CPU > 80%)
     ↓
  Alerte    →  envoie un email SMTP (port 1025)
     ↓
  MailHog   →  intercepte et affiche l'email ✅
     ↓
Interface web → http://172.17.0.1:8025
```

### Alertes configurées dans Grafana

| Alerte | Seuil | Sévérité |
|---|---|---|
| CPU élevé | > 80% | Critical |
| RAM élevée | > 85% | Warning |
| Service indisponible | up == 0 | Critical |
| Build Jenkins échoué | result != 1 | Warning |

### Configuration SMTP dans Grafana
```ini
[smtp]
enabled = true
host = mailhog:1025
from_address = grafana@achat.local
```

### Accéder aux alertes reçues
```
http://172.17.0.1:8025
```

---

## 🔐 Sécurité

### Outils DevSecOps intégrés

| Outil | Rôle | Intégration |
|---|---|---|
| **SonarQube** | Qualité et vulnérabilités du code | Pipeline Jenkins |
| **OWASP Dependency Check** | CVE dans les dépendances Maven | Pipeline Jenkins |
| **Trivy** | Vulnérabilités de l'image Docker | Pipeline Jenkins |

### Trivy — Scan image Docker
```bash
docker run --rm \
  -v /var/run/docker.sock:/var/run/docker.sock \
  aquasec/trivy image achat:1.0.0
```

### OWASP Dependency Check
```bash
mvn dependency-check:check \
  -DnvdApiKey=VOTRE_CLE \
  -Dformat=HTML
```
Obtenir une clé NVD gratuite : https://nvd.nist.gov/developers/request-an-api-key

### Résumé des vulnérabilités (Trivy)

| Sévérité | Nombre | Principaux composants |
|---|---|---|
| 🔴 CRITICAL | 6 | Tomcat 9.0.50, Spring4Shell |
| 🟠 HIGH | 37 | Logback, Jackson, Hibernate |
| 🟡 MEDIUM | 36 | MySQL connector, Tomcat |
| 🟢 LOW | 9 | Tomcat, Spring context |

> **Recommandation :** Mettre à jour Spring Boot vers 2.7.18+ corrige ~70% des CVE.

### Durcissement Docker

```yaml
# Dans docker-compose.yml
app-achat:
  security_opt:
    - no-new-privileges:true   # Empêche l'escalade de privilèges
  read_only: true              # Filesystem en lecture seule
  tmpfs:
    - /tmp                     # Seul /tmp est accessible en écriture
```

### Jenkins Credentials configurés

| ID | Type | Usage |
|---|---|---|
| nexus-cred | Username/Password | Publication Nexus |
| sonar-token | Secret text | Analyse SonarQube |
| nvd-api-key | Secret text | OWASP Dependency Check |

---

## 📁 Structure du projet

```
Achat-Devops/
├── docker/
│   └── Dockerfile                  # Image Docker de l'application
├── monitoring/
│   └── prometheus.yml              # Configuration Prometheus
├── src/
│   ├── main/
│   │   ├── java/tn/esprit/rh/      # Code source Spring Boot
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       ├── controllers/            # Tests Controllers (8 classes)
│       └── services/               # Tests Services (8 classes)
├── docker-compose.yml              # Orchestration des services
├── Jenkinsfile                     # Pipeline CI/CD (14 stages)
├── pom.xml                         # Dépendances Maven
└── README.md                       # Documentation
```

---

## 🔧 Dépannage

### L'application ne démarre pas
```bash
docker compose logs app-achat
docker ps | grep mysql
docker compose restart
```

### MySQL unhealthy
```bash
docker inspect mysqldb | grep Health
docker compose logs mysqldb
```

### Conflit de conteneurs
```bash
docker rm -f node-exporter prometheus grafana mysqldb app-achat mailhog || true
docker compose up -d
```

### Pipeline Jenkins échoue
```
Jenkins → Pipeline → Workspace → Wipe out current workspace → Relancer
```

### SonarQube inaccessible
```bash
# L'URL dans Jenkins doit être :
http://172.17.0.1:9000  # PAS 192.168.x.x
```

### MailHog n'affiche pas les alertes
```bash
# Vérifier que MailHog tourne
docker ps | grep mailhog
# Vérifier la config SMTP Grafana → port 1025
```

---

## 👥 Équipe

Projet réalisé dans le cadre du module DevOps — **SESAME Technology**

| Rôle | Responsabilité |
|---|---|
| Product Owner | Priorités backlog, validation livrables |
| Scrum Master | Facilitation, suivi avancement |
| Développeur DevOps | Pipeline Jenkins, Docker, Monitoring, Alerting |
| Développeur Sécurité | OWASP, Trivy, SonarQube, rapport sécurité |
