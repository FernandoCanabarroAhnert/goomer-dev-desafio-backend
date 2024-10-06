FROM openjdk:21-jdk

WORKDIR /app

COPY target/desafio-goomer-0.0.1-SNAPSHOT.jar /app/desafio-goomer.jar

EXPOSE 8080

CMD ["java", "-jar", "desafio-goomer.jar"]