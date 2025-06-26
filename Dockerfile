ARG BUILD_HOME=/kafka-mqtt-bridge

FROM gradle:jdk21-corretto AS build-image

ARG BUILD_HOME
ENV APP_HOME=$BUILD_HOME
WORKDIR $APP_HOME

COPY --chown=gradle:gradle build.gradle settings.gradle $APP_HOME/
COPY --chown=gradle:gradle src $APP_HOME/src
COPY --chown=gradle:gradle config $APP_HOME/config

RUN gradle --no-daemon build shadowJar -x test

FROM openjdk:21-jdk-slim

ARG BUILD_HOME
ENV APP_HOME=$BUILD_HOME
COPY --from=build-image $APP_HOME/build/libs/*all.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]