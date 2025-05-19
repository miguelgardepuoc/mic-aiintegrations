FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY boot/target/*.jar app.jar

EXPOSE 8087

ENTRYPOINT ["java", "-jar", "app.jar"]