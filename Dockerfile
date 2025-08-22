# 使用OpenJDK 21作为基础镜像
FROM openjdk:21-jdk-slim

# 设置工作目录
WORKDIR /app

# 复制Maven配置文件
COPY pom.xml .
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn

# 复制源代码
COPY src src

# 设置环境变量
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# 构建应用
RUN ./mvnw clean package -DskipTests

# 暴露端口
EXPOSE 8081

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar target/accountlife-0.0.1-SNAPSHOT.jar"] 