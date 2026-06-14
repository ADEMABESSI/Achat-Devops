<<<<<<< HEAD
FROM openjdk:17-jdk-alpine
ARG JAR_FILE=achat-1.0.jar
=======
FROM openjdk:15-jdk-alpine

ARG JAR_FILE=achat-0.0.1-SNAPSHOT.jar
>>>>>>> e10c32a (Update Dockerfile)
ARG APP_PORT=8082
ENV APP_PORT=${APP_PORT}
ENV SERVER_PORT=${APP_PORT}
EXPOSE ${APP_PORT}
ADD target/${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
# Créer un utilisateur non-root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
# Changer les permissions
COPY --chown=appuser:appgroup app.jar /app/app.jar
# Utiliser l'utilisateur non-root
USER appuser
