FROM eclipse-temurin:17-jre as build
ARG JAR_FILE=target/*.jar
WORKDIR /app
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
