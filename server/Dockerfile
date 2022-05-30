FROM openjdk:17

# для отладки порт
EXPOSE 5005
# рабочий порт
EXPOSE 9010

COPY ./build/libs /opt

CMD java -jar /opt/api-0.0.1-SNAPSHOT.jar
