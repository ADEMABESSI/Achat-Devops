FROM eclipse-temurin:17-jre-alpine

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

ARG JAR_FILE=achat-1.1.jar
ARG APP_PORT=8082

ENV APP_PORT=${APP_PORT}
ENV SERVER_PORT=${APP_PORT}
ENV JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom"

WORKDIR /app

COPY target/${JAR_FILE} app.jar

RUN chown appuser:appgroup /app/app.jar

USER appuser

EXPOSE ${APP_PORT}

HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
  CMD wget -qO- http://localhost:${APP_PORT}/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
