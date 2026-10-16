# ====================== BUILD STAGE ======================
FROM maven:3.9.11-eclipse-temurin-25-alpine AS build
WORKDIR /build

# Copy pom first (better layer caching)
COPY pom.xml .
#RUN mvn dependency:go-offline -B

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# Rename JAR to fixed name (much more reliable than wildcard)
RUN mv target/emmaus_api-*.jar target/app.jar


# ====================== RUNTIME STAGE ======================
FROM eclipse-temurin:25-alpine

ARG APP_VERSION=0.0.1
ENV APP_VERSION=${APP_VERSION}

WORKDIR /app

# Timezone (Africa/Addis_Ababa) - configured for Alpine
ENV TZ=Africa/Addis_Ababa
RUN apk add --no-cache tzdata && \
    ln -snf /usr/share/zoneinfo/${TZ} /etc/localtime && \
    echo ${TZ} > /etc/timezone

# Copy the JAR from build stage
COPY --from=build /build/target/app.jar /app/app.jar

EXPOSE 8088

# JVM options optimized for containers
ENV JAVA_OPTS="\
-XX:+UseContainerSupport \
-XX:MaxRAMPercentage=65.0 \
-XX:+ExitOnOutOfMemoryError \
-XX:+HeapDumpOnOutOfMemoryError \
-Djava.security.egd=file:/dev/./urandom"

# Security: run as non-root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser


CMD java ${JAVA_OPTS} -jar app.jar

