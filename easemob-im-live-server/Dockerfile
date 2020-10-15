FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE=target/*.jar
ARG CONFIG_FILE=docker/*.properties
COPY ${JAR_FILE} easemob-im-live-server.jar
COPY ${CONFIG_FILE} config/
ENV JAVA_OPTS=""
ENTRYPOINT java ${JAVA_OPTS} -Dspring.config.location=config/ -Dspring.profiles.active=docker -jar /easemob-im-live-server.jar
