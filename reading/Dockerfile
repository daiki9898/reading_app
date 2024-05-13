# Build stage
FROM maven:3-eclipse-temurin-21 AS build
COPY ./src/main/resources/static/img/default.jpg /tmp/default.jpg
COPY ./ /home/app
RUN cd /home/app && mvn clean package -DskipTests

# Package stage
FROM eclipse-temurin:21-alpine
COPY --from=build /home/app/target/reading-0.0.1-SNAPSHOT.jar /usr/local/lib/reading.jar

# Copy the image from the temporary directory to the final directory in the package stage
COPY --from=build /tmp/default.jpg /usr/local/lib/static/img/default.jpg

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/usr/local/lib/reading.jar"]