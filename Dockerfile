FROM gradle:jdk-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon 

FROM eclipse-temurin
EXPOSE 8080

RUN mkdir /opt/app

COPY --from=build /home/gradle/src/build/libs/*.jar /opt/app

CMD ["java", "-jar", "/opt/app/r5.jar"] 