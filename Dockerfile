FROM openjdk:8-jre-slim

WORKDIR /app

COPY ./banking-0.0.1-SNAPSHOT.jar .

ENTRYPOINT ["java","-jar","-Dserver.port=8090","banking-0.0.1-SNAPSHOT.war"]