FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
VOLUME /tmp
RUN addgroup -S app && adduser -S -G app appuser

COPY *.jar /app/accountmovements.jar
USER appuser

ENTRYPOINT ["/opt/java/openjdk/bin/java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=70", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/accountmovements.jar"]
