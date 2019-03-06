FROM maven:3-jdk-11 as compile-stage

WORKDIR /build
COPY . .
RUN mvn install --batch-mode -DskipTests=true -DfinalName=server

FROM openjdk:11-jre-slim as run-stage
WORKDIR /app
COPY --from=compile-stage /build/server/target/server.jar .

EXPOSE 8080
CMD ["java", "-jar", "/app/server.jar"]