# Imagen base con Maven y OpenJDK 21
FROM maven:3.9.6-eclipse-temurin-21 AS builder

# Crear directorio de trabajo
WORKDIR /app

# Copiar archivos del proyecto
COPY . .

# Construir el JAR
RUN mvn clean package -DskipTests

# Imagen final para ejecutar la aplicación
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copiar el JAR generado desde la imagen anterior
COPY --from=builder /app/target/*.jar app.jar

# Exponer el puerto
EXPOSE 8080

# Ejecutar la aplicación
CMD ["java", "-jar", "app.jar"]