# 使用预先构建的包含 wget 和 openjdk8-jre 的镜像 就不需要在运行时安装这些包了，因为它们已经包含在基础镜像中
FROM openjdk:8-jdk-alpine

# 复制JAR文件到容器
COPY task-1.0-SNAPSHOT-pro.war /app/

# 设置工作目录
WORKDIR /app

# 暴露端口号
EXPOSE 8081

# 启动应用程序
CMD ["java", "-jar", "task-1.0-SNAPSHOT-pro.war"]
