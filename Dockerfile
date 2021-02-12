FROM openjdk:11
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} propostas.jar
ENTRYPOINT ["java","-Xmx512m","-jar","/propostas.jar"]