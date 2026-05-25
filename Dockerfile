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

# Copiar wallet completo (cwallet.sso + tnsnames.ora + sqlnet.ora + ojdbc.properties)
COPY src/main/resources/wallet /app/wallet

COPY --from=buildstage /app/target/plataforma-educativa-1.0.0.jar /app/app.jar

RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

# IMPORTANTE:
# - TNS_ADMIN apunta al directorio con cwallet.sso y tnsnames.ora.
# - oracle.net.wallet_location indica al driver que use el SSO wallet
#   (cwallet.sso permite acceso sin password, así no dependemos de .jks).
# - oracle.net.ssl_server_dn_match=true para validar el DN del servidor ADB.
ENV TNS_ADMIN=/app/wallet
ENV JAVA_OPTS="-Xms256m -Xmx512m \
  -Doracle.net.tns_admin=/app/wallet \
  -Doracle.net.wallet_location=/app/wallet \
  -Doracle.net.ssl_server_dn_match=true"

CMD ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
