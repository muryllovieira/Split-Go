# --- Build: compila o jar com o JDK ---
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Dependências primeiro para aproveitar o cache de camadas
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw && ./mvnw -B -q dependency:go-offline

COPY src src
# Testes rodam fora do container (Testcontainers precisa do Docker do host)
RUN ./mvnw -B -q package -DskipTests

# --- Runtime: apenas o JRE, imagem menor ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S app && adduser -S app -G app
COPY --from=build /app/target/*.jar app.jar
USER app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
