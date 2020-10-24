FROM openjdk:latest
#need to add maven
# need to add run command to package

ADD target/auth-course-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

EXPOSE 8080
