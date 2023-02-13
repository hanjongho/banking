FROM openjdk:8-jre-slim

ENTRYPOINT ["java","-jar","build/libs/banking-0.0.1-SNAPSHOT.jar"]
