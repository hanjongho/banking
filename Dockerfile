FROM openjdk:8-jre-slim

ARG JAR_FILE=banking-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} myboot.jar
ENTRYPOINT ["java","-jar","/myboot.jar"]
