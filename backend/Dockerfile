# 使用OpenJDK 17作为基础镜像
FROM openjdk:17-jre-slim

# 设置工作目录
WORKDIR /app

# 复制Maven构建的jar文件
COPY target/cocktail-maker-backend-1.0.0.jar app.jar

# 创建上传目录
RUN mkdir -p /app/uploads

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 暴露端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/api/actuator/health || exit 1

# 启动应用
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]