FROM maven:3.8.8-eclipse-temurin-8 AS builder

WORKDIR /src
COPY . ./
RUN mvn -q -DskipTests package

FROM eclipse-temurin:8-jre

WORKDIR /app
COPY --from=builder /src/s-pay-mall-ddd-app/target/s-pay-mall-ddd-app.jar /app/app.jar
EXPOSE 8070

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
