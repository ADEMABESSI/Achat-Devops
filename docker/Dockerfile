# ============================================================
# Dockerfile sécurisé — Semaine 6 DevSecOps
# ============================================================

# Étape 1 : Build (multi-stage pour réduire la surface d'attaque)
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /build
COPY target/*.jar app.jar

# Étape 2 : Image finale minimale (JRE seulement, pas JDK)
FROM eclipse-temurin:17-jre-alpine

# Sécurité : créer un utilisateur non-root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

ARG JAR_FILE=achat-1.1.jar
ARG APP_PORT=8082

ENV APP_PORT=${APP_PORT}
ENV SERVER_PORT=${APP_PORT}
# Désactiver le DNS caching agressif de Java (bonne pratique sécurité)
ENV JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom"

WORKDIR /app

# Copier uniquement le JAR
COPY --from=builder /build/app.jar app.jar

# Donner les droits à l'utilisateur non-root
RUN chown appuser:appgroup /app/app.jar

# Passer à l'utilisateur non-root
USER appuser

EXPOSE ${APP_PORT}

# Healthcheck pour détecter si l'app est tombée
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
  CMD wget -qO- http://localhost:${APP_PORT}/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]