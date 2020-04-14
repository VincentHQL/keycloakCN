FROM openjdk:8-jdk-alpine
MAINTAINER VincentHQL <229323147@qq.com>
VOLUME /tmp
ENV JAVA_OPTS="-Xmx200m"
ADD ./target/*.jar /app/app.jar
CMD ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
EXPOSE 8080