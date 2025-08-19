FROM tomcat:9.0.107-jdk17-temurin

# 앱 배포
COPY build/libs/*SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

# HTTPS 커넥터가 포함된 server.xml (아래 2번에 내용 제공)
COPY server.xml /usr/local/tomcat/conf/server.xml

EXPOSE 8080 8443
