FROM maven:3.6.0-jdk-11-slim AS build
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean install

FROM openjdk:11
COPY config.yml /var/queue-consumer/
COPY --from=build /usr/src/app/target/queue-consumer-1.0-SNAPSHOT.jar /var/queue-consumer/
EXPOSE 9090
WORKDIR /var/queue-consumer
ENV JAVA_TOOL_OPTIONS "-Dcom.sun.management.jmxremote.ssl=false \
 -Dcom.sun.management.jmxremote.authenticate=false \
 -Dcom.sun.management.jmxremote.port=9090 \
 -Dcom.sun.management.jmxremote.rmi.port=9090 \
 -Dcom.sun.management.jmxremote.local.only=true \
 -Djava.rmi.server.hostname=127.0.0.1"
CMD ["java", "-jar", "-Dcom.sun.management.jmxremote -Done-jar.silent=true -Dcom.sun.management.jmxremote.port=9090 -Dcom.sun.management.jmxremote.rmi.port=9090 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostname=localhost -Dcom.sun.management.jmxremote.host=localhost -Dcom.sun.management.jmxremote.local.only=false", "queue-consumer-1.0-SNAPSHOT.jar", "server", "config.yml"]