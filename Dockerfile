FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

COPY src ./src
COPY WebContent/WEB-INF/lib ./WebContent/WEB-INF/lib

RUN mkdir -p build/classes && \
    curl -L -o /tmp/jakarta.servlet-api.jar \
    https://repo1.maven.org/maven2/jakarta/servlet/jakarta.servlet-api/6.0.0/jakarta.servlet-api-6.0.0.jar && \
    find src -name "*.java" > sources.txt && \
    javac \
    -cp "/tmp/jakarta.servlet-api.jar:WebContent/WEB-INF/lib/*" \
    -d build/classes \
    @sources.txt


FROM tomcat:10.1-jdk17-temurin

RUN rm -rf /usr/local/tomcat/webapps/ROOT

COPY WebContent /usr/local/tomcat/webapps/ROOT
COPY --from=build /app/build/classes /usr/local/tomcat/webapps/ROOT/WEB-INF/classes

EXPOSE 8080

CMD ["catalina.sh", "run"]