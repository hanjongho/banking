FROM openjdk:8-jdk-alpine
ARG JAR_FILE=build/libs/banking-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} banking.jar
ENTRYPOINT ["java","-jar","/banking.jar"]