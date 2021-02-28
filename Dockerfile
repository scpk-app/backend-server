FROM openjdk:11
WORKDIR /server/
COPY target/scpk-1.1.0.jar /server/
VOLUME /server/config/
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "scpk-1.1.0.jar"]