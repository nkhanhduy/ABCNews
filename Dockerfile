# ==============================================================================
# Multi-stage Dockerfile cho ABCNews (Tomcat 10.1 + Java 17)
# ==============================================================================

# STAGE 1: Build ứng dụng với Maven & Eclipse Temurin 17
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /build

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy mã nguồn & đóng gói file WAR
COPY src ./src
RUN mvn clean package -DskipTests -B

# STAGE 2: Runtime Image với Apache Tomcat 10.1 (Jakarta EE 10)
FROM tomcat:10.1-jdk17-temurin-jammy
LABEL maintainer="Nguyen Khanh Duy <khanhndts02168@gmail.com>"
LABEL description="ABCNews Enterprise CMS Web Application"

# Dọn dẹp ứng dụng mặc định của Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

# Sao chép file WAR từ stage builder vào thư mục webapps của Tomcat dưới dạng ROOT.war
COPY --from=builder /build/target/*.war /usr/local/tomcat/webapps/ROOT.war

# Thiết lập biến môi trường JVM tối ưu
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -Dfile.encoding=UTF-8"

# Expose cổng HTTP mặc định của Tomcat
EXPOSE 8080

# Khởi chạy Tomcat
CMD ["catalina.sh", "run"]
