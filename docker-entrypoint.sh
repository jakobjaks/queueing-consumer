#! /bin/bash

 JAVA_OPTS="-Dcom.sun.management.jmxremote.ssl=false \
 -Dcom.sun.management.jmxremote.authenticate=false \
 -Dcom.sun.management.jmxremote.port=9090 \
 -Dcom.sun.management.jmxremote.rmi.port=9090 \
 -Dcom.sun.management.jmxremote.local.only=true \
 -Djava.net.preferIPv4Stack=true \
 -Djava.rmi.server.hostname=${}"

exec java $JAVA_OPTS -jar queue-consumer-1.0-SNAPSHOT.jar server config.yml
