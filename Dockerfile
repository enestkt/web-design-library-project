FROM maven:3.9.6-eclipse-temurin-22-alpine AS build
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:22-jdk-alpine
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]