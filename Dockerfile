FROM gradle:9.3.0-jdk21 AS builder
WORKDIR /app

ARG SERVICE_DIR
ARG SERVICE_NAME

COPY build.gradle ./

COPY ${SERVICE_DIR} ./${SERVICE_NAME}
COPY Scoring-Contract ./scoring-contract

# Минимальный settings.gradle — только нужные подпроекты
RUN printf "rootProject.name = 'scoring-app'\ninclude '${SERVICE_NAME}'\ninclude 'scoring-contract'\n" > settings.gradle

RUN gradle :${SERVICE_NAME}:clean :${SERVICE_NAME}:bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
ARG SERVICE_NAME
COPY --from=builder /app/${SERVICE_NAME}/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
