# Build stage
FROM maven:3-eclipse-temurin-21 AS build
COPY ./ /home/app
RUN cd /home/app && mvn clean package -DskipTests

# Package stage
FROM eclipse-temurin:21-alpine
COPY --from=build /home/app/src/main/resources/static/img /usr/local/lib/static/img/
COPY --from=build /home/app/target/reading-0.0.1-SNAPSHOT.jar /usr/local/lib/reading.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/usr/local/lib/reading.jar"]