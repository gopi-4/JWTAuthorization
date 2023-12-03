FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY target/*.jar JWTAuthorization.jar
ENTRYPOINT ["java","-jar","/JWTAuthorization.jar"]
EXPOSE 8085