FROM maven:3-eclipse-temurin AS builder

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package

FROM niceos/openjdk25:25.0.0 AS runner

WORKDIR /app

COPY --from=builder ./app/target/billing-service-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 8081
EXPOSE 9091

ENTRYPOINT ["java", "-jar", "app.jar"]
