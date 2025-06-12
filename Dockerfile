FROM eclipse-temurin:17-jdk

WORKDIR /home

COPY target/F1Hub.jar app.jar
COPY .env .env
EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
