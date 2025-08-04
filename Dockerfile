FROM tomcat:9.0.107-jdk17-temurin

COPY build/libs/*SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080