FROM openjdk:8-jre-slim

WORKDIR /app

COPY /build/libs/banking-0.0.1-SNAPSHOT.jar .

ENTRYPOINT ["java","-jar","-Dserver.port=8090","/build/libs/banking-0.0.1-SNAPSHOT.jar"]
