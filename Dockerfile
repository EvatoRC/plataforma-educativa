# ---- Etapa 1: Build ----
FROM eclipse-temurin:21-jdk AS buildstage

RUN apt-get update && apt-get install -y maven --no-install-recommends && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src /app/src
RUN mvn clean package -DskipTests -B

# ---- Etapa 2: Runtime ----
FROM eclipse-temurin:21-jre

RUN groupadd -r appgroup && useradd -r -g appgroup appuser

WORKDIR /app

COPY src/main/resources/wallet /app/wallet

# Importar el certificado de Oracle al truststore de Java
RUN keytool -import -alias oracle-adb \
    -file /app/wallet/ewallet.pem \
    -keystore $JAVA_HOME/lib/security/cacerts \
    -storepass changeit -noprompt || true

COPY --from=buildstage /app/target/plataforma-educativa-1.0.0.jar /app/app.jar

RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

ENV JAVA_OPTS="-Xms256m -Xmx512m \
  -Doracle.net.ssl_server_dn_match=false \
  -Doracle.net.wallet_location=/app/wallet \
  -Djavax.net.ssl.trustStore=/app/wallet/truststore.jks \
  -Djavax.net.ssl.trustStorePassword=changeit \
  -Djavax.net.ssl.keyStore=/app/wallet/keystore.jks \
  -Djavax.net.ssl.keyStorePassword=changeit"

CMD ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]