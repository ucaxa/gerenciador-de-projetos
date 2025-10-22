# Etapa 1: Build da aplicação com Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copiar arquivos de configuração primeiro para aproveitar cache
COPY pom.xml .
COPY src ./src

# Compilar e empacotar o JAR
RUN mvn clean package -DskipTests

# Etapa 2: Imagem final com JRE apenas
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copiar o JAR gerado da etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Criar usuário não-root para segurança
RUN groupadd -r spring && useradd -r -g spring spring
USER spring

# Expor porta padrão do Spring Boot
EXPOSE 8080

# Healthcheck via actuator
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Executar a aplicação com perfil docker
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=docker", "app.jar"]
