FROM openjdk:8-jre-slim

ENTRYPOINT ["java","-jar","/banking-0.0.1-SNAPSHOT.jar"]
