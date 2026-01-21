# Fail-safe Dockerfile
# 使用最基础的 Ubuntu 镜像，避免 JDK 镜像源问题
# 手动下载并安装 JDK，确保可控

# 基础镜像源可通过构建参数替换
ARG DOCKER_MIRROR=docker.io

# ==========================================
# Stage 1: Build the application
# ==========================================
FROM ${DOCKER_MIRROR}/library/ubuntu:22.04 AS build

WORKDIR /app

# 1. 安装基础工具
# 替换为阿里云源以加速
RUN sed -i 's/archive.ubuntu.com/mirrors.aliyun.com/g' /etc/apt/sources.list && \
    sed -i 's/security.ubuntu.com/mirrors.aliyun.com/g' /etc/apt/sources.list && \
    apt-get update && \
    apt-get install -y wget tar maven && \
    rm -rf /var/lib/apt/lists/*

# 2. 下载并安装 OpenJDK 21 (使用 Amazon Corretto 国内镜像，通常更稳定)
# 备用下载地址：https://corretto.aws/downloads/latest/amazon-corretto-21-x64-linux-jdk.tar.gz
RUN wget https://corretto.aws/downloads/latest/amazon-corretto-21-x64-linux-jdk.tar.gz -O jdk.tar.gz && \
    mkdir -p /opt/java && \
    tar -xzf jdk.tar.gz -C /opt/java --strip-components=1 && \
    rm jdk.tar.gz

ENV JAVA_HOME=/opt/java
ENV PATH=$JAVA_HOME/bin:$PATH

# 3. 构建应用
COPY maven-settings.xml /usr/share/maven/conf/settings.xml
COPY pom.xml .
COPY app-server/pom.xml app-server/
COPY app-server/src app-server/src
COPY doc doc

# 使用 -Dsql.skip=true 跳过数据库初始化插件（构建时无数据库环境）
# 使用 -e 显示详细错误信息
RUN mvn -e clean package -pl app-server -am -DskipTests -Dsql.skip=true

# ==========================================
# Stage 2: Create the runtime image
# ==========================================
FROM ${DOCKER_MIRROR}/library/ubuntu:22.04

# 安装基础依赖
RUN sed -i 's/archive.ubuntu.com/mirrors.aliyun.com/g' /etc/apt/sources.list && \
    sed -i 's/security.ubuntu.com/mirrors.aliyun.com/g' /etc/apt/sources.list && \
    apt-get update && \
    apt-get install -y ca-certificates && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# 从构建阶段复制 JDK 和 应用
COPY --from=build /opt/java /opt/java
COPY --from=build /app/app-server/target/*.jar app.jar

ENV JAVA_HOME=/opt/java
ENV PATH=$JAVA_HOME/bin:$PATH
ENV LANG=C.UTF-8

# 暴露端口
EXPOSE 8096

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]
