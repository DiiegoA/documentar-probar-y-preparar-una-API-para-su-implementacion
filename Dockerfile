# Usa una imagen base de Java
FROM openjdk:21-jdk-slim

# Configurar el directorio de trabajo
WORKDIR /app

# Copia el archivo JAR en el contenedor
COPY target/api-0.0.1-SNAPSHOT.jar app.jar

# Expone el puerto 8080
EXPOSE 9090

# Define las variables de entorno necesarias para la base de datos
ENV DB_HOST=mysql-db
ENV DB_PORT=3306
ENV DB_NAME=vollmed_api
ENV DB_USER=root
ENV DB_PASSWORD=Lauracamila95*


# Ejecuta la aplicación con el perfil de producción
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
