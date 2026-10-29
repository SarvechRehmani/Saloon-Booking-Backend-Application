FROM maven:3.9.9-eclipse-temurin-21 AS build

ARG SERVICE_DIR
WORKDIR /workspace
COPY . .
RUN set -eux; \
    test -n "$SERVICE_DIR"; \
    mvn -B -f "$SERVICE_DIR/pom.xml" -DskipTests package; \
    JAR_FILE="$(find "$SERVICE_DIR/target" -maxdepth 1 -type f -name '*.jar' ! -name '*.original' -print -quit)"; \
    test -n "$JAR_FILE"; \
    cp "$JAR_FILE" /workspace/app.jar

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/app.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
