FROM openjdk:17-jdk-slim

WORKDIR /app

# Copia o wrapper Maven e o pom.xml
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .

# Baixa as dependências do Maven (para cache)
RUN ./mvnw dependency:go-offline

# Copia o código fonte da aplicação
COPY src src

# Builda a aplicação
RUN ./mvnw package -DskipTests

# Executa a aplicação
CMD ["java", "-jar", "target/capo-finance-backend-0.0.1-SNAPSHOT.jar"]
