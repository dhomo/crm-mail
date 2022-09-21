FROM gradle:jdk17 AS builder
COPY --chown=gradle:gradle . /home/gradle/project
WORKDIR /home/gradle/project
RUN gradle bootJar --no-daemon


FROM eclipse-temurin:17-jre-alpine
# рабочий порт
EXPOSE 9010
COPY --from=builder /home/gradle/project/build/libs/app.jar /opt/app.jar
CMD java -jar /opt/app.jar
