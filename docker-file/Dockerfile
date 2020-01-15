#FROM openjdk:8 as builder
#ADD . /code/
#RUN \
#    apt-get update && \
#    apt-get install gradle -y && \
#    cd /code/ && \
#    ./gradle clean build -x test && \
#    mv /code/build/libs/registry*.jar /registry.jar

FROM openjdk:8-jre-alpine

ENV APP_DIR=/deploy
ENV APP=voiceassistant.jar
ENV APP_PORT=8080
ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    JAVA_OPTS=""

COPY target/*.jar $APP_DIR/$APP

EXPOSE $APP_PORT
WORKDIR $APP_DIR

CMD java \
        ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom \
        -Dspring.profiles.active=prod \
        -jar $APP



