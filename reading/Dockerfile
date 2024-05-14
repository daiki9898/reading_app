# Build stage
FROM maven:3-eclipse-temurin-21 AS build
RUN FILE_PATH=$(find / -name "default.jpg") && \
    echo "File path is $FILE_PATH" && \
    cp $FILE_PATH /tmp/
COPY ./ /home/app
COPY /tmp/default.jpg /home/app/static/
RUN cd /home/app && mvn clean package -DskipTests

# Package stage
FROM eclipse-temurin:21-alpine
COPY --from=build /home/app/target/reading-0.0.1-SNAPSHOT.jar /usr/local/lib/reading.jar

# Copy the image from the temporary directory to the final directory in the package stage
COPY --from=build /home/app/static/default.jpg /usr/local/lib/static/img/default.jpg

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/usr/local/lib/reading.jar"]