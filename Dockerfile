FROM maven:3.6.0-jdk-11-slim AS build
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean install

FROM openjdk:11
COPY config.yml /var/queue-consumer/
COPY target/queue-consumer-1.0-SNAPSHOT.jar /var/queue-consumer/
EXPOSE 9090
WORKDIR /var/queue-consumer
ENV JAVA_TOOL_OPTIONS "-Dcom.sun.management.jmxremote.ssl=false \
 -Dcom.sun.management.jmxremote.authenticate=false \
 -Dcom.sun.management.jmxremote.port=9090 \
 -Dcom.sun.management.jmxremote.rmi.port=9090 \
 -Dcom.sun.management.jmxremote.host=0.0.0.0 \
 -Djava.rmi.server.hostname=0.0.0.0"
CMD ["java", "-jar", "-Dcom.sun.management.jmxremote -Done-jar.silent=true -Dcom.sun.management.jmxremote.port=9090 -Dcom.sun.management.jmxremote.rmi.port=9090 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostname=0.0.0.0 -Dcom.sun.management.jmxremote.host=0.0.0.0 -Dcom.sun.management.jmxremote.local.only=false", "queue-consumer-1.0-SNAPSHOT.jar", "server", "config.yml"]