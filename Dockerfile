# Build stage
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon
COPY src src

# Quan trọng: build bootJar (không tạo plain.jar)
RUN ./gradlew bootJar -x test --no-daemon

# Runtime
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN addgroup --system --gid 1001 appuser && adduser --system --uid 1001 --gid 1001 appuser
ENV SPRING_PROFILES_ACTIVE=prod
# Chỉ copy đúng boot jar
COPY --from=build /app/build/libs/*-SNAPSHOT.jar /app/app.jar
# (hoặc) COPY --from=build /app/build/libs/*boot*.jar /app/app.jar

RUN chown -R appuser:appuser /app
USER appuser
EXPOSE 8080

# Tạm thời tắt HEALTHCHECK để tránh nhiễu khi debug
# HEALTHCHECK NONE

ENTRYPOINT ["java","-jar","/app/app.jar"]