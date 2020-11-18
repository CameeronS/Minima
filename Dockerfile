#FROM openjdk:11
FROM adoptopenjdk/openjdk11:x86_64-alpine-jdk-11.0.9_11-slim as build-stage
RUN apk add wget
WORKDIR /usr/src/minima/
COPY gradle gradle
COPY gradlew settings.gradle build.gradle .classpath ./
COPY gradle/wrapper/gradle-wrapper-docker.properties gradle/wrapper/gradle-wrapper.properties
WORKDIR /usr/src/minima/gradle/wrapper/
RUN wget https://services.gradle.org/distributions/gradle-6.7.1-bin.zip
WORKDIR /usr/src/minima/
# Call gradlew before copying the source code to only download the gradle distribution once (layer will be cached)
#RUN ./gradlew --no-daemon -v
COPY lib lib
COPY src src
COPY test test
# minimal jar (750 kb) without dapp server -> build/libs/minima.jar
#RUN ./gradlew --no-daemon jar
# fatjar with all deps -> build/libs/minima-all.jar
RUN ./gradlew --no-daemon jar shadowJar
RUN md5sum build/libs/*
RUN ls -l build/libs/*
RUN stat build/libs/minima-all.jar
#RUN tar -cf minimajar.tar build/libs/minima-all.jar

FROM adoptopenjdk/openjdk11:x86_64-alpine-jdk-11.0.9_11-slim as production-stage
COPY --from=build-stage /usr/src/minima/build/libs/minima-all.jar /opt/minima/minima.jar
#COPY --from=build-stage /usr/src/minima/minimajar.tar /opt/minima/minimajar.tar
WORKDIR /opt/minima
#RUN tar -xf minimajar.tar
#RUN mv build/libs/minima-all.jar minima.jar
RUN touch -a -m -t 202011010000.00 minima.jar
#RUN rm minimajar.tar
RUN md5sum *.jar
RUN ls -l *.jar
RUN stat minima.jar
CMD ["java", "-jar", "minima.jar"]

