# ---- Etapa 1: Build ----
FROM eclipse-temurin:21-jdk AS buildstage

RUN apt-get update && apt-get install -y maven --no-install-recommends && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copiar dependencias primero (caché de Docker)
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src /app/src
RUN mvn clean package -DskipTests -B

# ---- Etapa 2: Runtime ----
FROM eclipse-temurin:21-jre

RUN groupadd -r appgroup && useradd -r -g appgroup appuser

WORKDIR /app

# Copiar wallet de Oracle Cloud
COPY src/main/resources/wallet /app/wallet

# Copiar JAR compilado
COPY --from=buildstage /app/target/plataforma-educativa-1.0.0.jar /app/app.jar

RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

ENV JAVA_OPTS="-Xms256m -Xmx512m"

CMD ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
