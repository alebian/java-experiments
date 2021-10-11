FROM openjdk:17-jdk

WORKDIR /app
COPY build/libs/java-experiments-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]