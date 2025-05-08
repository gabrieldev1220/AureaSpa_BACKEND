FROM openjdk:21-jdk-slim
ENV APP_JAR_NAME tu-proyecto.jar
RUN mkdir /app
COPY target/backendspa-0.0.1-SNAPSHOT.jar /app/
WORKDIR /app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "tu-proyecto.jar"]