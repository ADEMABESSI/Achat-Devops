FROM openjdk:17-jdk-alpine
ARG JAR_FILE=achat-1.0.jar
ARG APP_PORT=8082
ENV APP_PORT=${APP_PORT}
ENV SERVER_PORT=${APP_PORT}
EXPOSE ${APP_PORT}
ADD target/${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
