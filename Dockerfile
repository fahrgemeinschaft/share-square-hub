
FROM openjdk:11.0.3-jdk-slim-stretch AS BUILDER
COPY . .
RUN ./gradlew clean bootJar

#Made to run in Openshift
FROM openjdk:11.0.3-jdk-slim-stretch
MAINTAINER zero@dividebyzero.cc

RUN mkdir -p /home/app
WORKDIR /home/app
COPY --from=BUILDER /build/libs/share-square-hub.jar ./app.jar
EXPOSE 8080
ENTRYPOINT java -XX:+PrintFlagsFinal -Djava.security.egd=file:/dev/./urandom $JAVA_OPTIONS -jar app.jar
