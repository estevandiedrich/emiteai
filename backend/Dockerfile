# Use a imagem oficial do Java 17
FROM eclipse-temurin:17-jdk-alpine

# Diretório de trabalho dentro do container
WORKDIR /app

# Copie o build do projeto para o container
COPY build/libs/emiteai-backend-0.0.1-SNAPSHOT.jar app.jar

# Exponha a porta do Spring Boot
EXPOSE 8080

# Comando de inicialização
ENTRYPOINT ["java", "-jar", "app.jar"]

