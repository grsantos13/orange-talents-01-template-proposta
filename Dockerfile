# Gerando o artefato
FROM maven:latest AS builder
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package

# Rodando a imagem
FROM openjdk:11
COPY --from=builder /usr/src/app/target/propostas.jar /usr/app/propostas.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/usr/app/propostas.jar"]