FROM adoptopenjdk/openjdk14:slim
MAINTAINER Lokesh Venkatesan, lvenkatesan@canalbarge.com
ADD target/CopyInfluxData.jar app.jar
ENTRYPOINT [ "sh", "-c", "java  -jar /app.jar --spring.profiles.active=$profile"]