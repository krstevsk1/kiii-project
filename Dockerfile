FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests

RUN ls -la target


FROM amazoncorretto:21-alpine3.15-jdk

WORKDIR /app

COPY --from=build /app/target/*.jar /app/application.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/application.jar"]
