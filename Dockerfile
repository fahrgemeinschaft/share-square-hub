
FROM openjdk:11.0.3-jdk-slim-stretch AS BUILDER
ARG SHARE2_OAUTH_CLIENT_ID=123456
ENV SHARE2_OAUTH_CLIENT_ID=123456
ARG SHARE2_OAUTH_CLIENT_SECRET=123456
ENV SHARE2_OAUTH_CLIENT_SECRET=123456
COPY . .
RUN ./gradlew clean bootJar

#Made to run in Openshift
FROM openjdk:11.0.3-jdk-slim-stretch
MAINTAINER zero@dividebyzero.cc

ARG SHARE2_OAUTH_CLIENT_ID=123456
ENV SHARE2_OAUTH_CLIENT_ID=123456

ARG SHARE2_OAUTH_CLIENT_SECRET=123456
ENV SHARE2_OAUTH_CLIENT_SECRET=123456

RUN mkdir -p /home/app
WORKDIR /home/app
COPY --from=BUILDER /build/libs/share-square-hub.jar ./app.jar
EXPOSE 8081
ENTRYPOINT java -XX:+PrintFlagsFinal -Djava.security.egd=file:/dev/./urandom $JAVA_OPTIONS -jar app.jar
