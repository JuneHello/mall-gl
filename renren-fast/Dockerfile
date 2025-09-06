FROM openjdk:8-jdk
WORKDIR /app
COPY target/renren-fast.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
