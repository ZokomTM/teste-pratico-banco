# Etapa de build
FROM maven:3.9.1 AS builder
WORKDIR /app

# Copia o arquivo pom.xml e o diretório src para o container
COPY pom.xml .
COPY src ./src

# Compila o projeto e gera o arquivo .jar
RUN mvn clean package -DskipTests

# Etapa final - imagem mais leve
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copia o .jar construído na etapa de build para a imagem final
COPY --from=builder /app/target/*.jar app.jar

# Exponha a porta do serviço (ajuste se necessário)
EXPOSE 8080

# Comando para iniciar o aplicativo
ENTRYPOINT ["java", "-jar", "app.jar"]
