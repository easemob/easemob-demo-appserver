# Easemob IM App Server Demo

## 📖 项目简介

本项目为 **环信 IM Demo** 的后端服务参考实现。它演示了如何使用环信服务端 SDK 构建一个完整的 App 后端，涵盖用户体系、群组管理等核心功能。开发者可以基于此项目快速搭建自己的 IM 应用服务端。

### 核心功能
*   **用户体系**：用户注册/登录（对接短信验证码流程）、用户信息管理。
*   **头像管理**：用户头像上传与存储、群组头像获取。
*   **环信集成**：生成用户 Token、同步用户数据到环信 IM 服务器。

---

## 🚀 快速启动 (Docker 推荐)

本项目提供了高度优化的 **All-in-One Docker 镜像**，将 Java 应用、MySQL 8.0 和 Redis 整合在同一个轻量级容器中（< 800MB）。我们提供了全自动化的启动脚本，无需本地安装任何依赖（仅需 Docker），即可实现**一键构建与运行**。

### 1. 环境准备
*   [Docker Desktop](https://www.docker.com/products/docker-desktop/)

### 2. 配置应用信息
首次启动前，请确保项目根目录下存在 `.env` 配置文件（脚本会自动检测，若不存在会从 `.env.example` 复制）。

打开 `.env` 文件，填入您的环信应用信息（可在 [环信管理后台](https://console.easemob.com/) 获取）：
```ini
# 环信 AppKey
APPLICATION_APPKEY=your_org_name#your_app_name
# AppKey 对应的 Client ID
APPLICATION_CLIENTID=your_client_id
# AppKey 对应的 Client Secret
APPLICATION_CLIENTSECRET=your_client_secret
```

### 3. 一键启动
根据您的操作系统选择启动方式，脚本会自动完成 **构建镜像** -> **清理旧环境** -> **启动新容器** 的全过程：

*   **Windows 用户**:
    双击运行项目根目录下的 `start.bat` 脚本。

*   **macOS / Linux 用户**:
    在终端中运行：
    ```bash
    sh start.sh
    ```

### 4. 访问服务
启动成功后，所有服务将在同一个容器内运行，并对外提供服务：
*   **App Server**: `http://localhost:8096`
*   **MySQL**: `localhost:3307` (默认密码: `123456`，数据库自动初始化)
*   **Redis**: `localhost:6379`

> **注意**: 该模式下数据默认存储在容器内部。如果需要持久化 MySQL 数据，可以在 `start.bat` 或 `start.sh` 中的 `docker run` 命令添加 `-v mysql_data:/var/lib/mysql` 参数。

---

## 🛠 本地开发环境搭建 (源码部署)

如果您需要修改代码或进行二次开发，请按照以下步骤配置本地开发环境。

### 1. 环境依赖
请确保本地已安装以下软件：
*   **JDK 21**: [下载地址](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
*   **Maven 3.8+**: [下载地址](https://maven.apache.org/download.cgi)
*   **MySQL 8.0+**: [下载地址](https://dev.mysql.com/downloads/mysql/)
*   **Redis 6.0+**: [下载地址](https://redis.io/download/)

### 2. 数据库初始化
1.  连接到您的本地 MySQL 数据库。
2.  创建数据库 `app_server`：
    ```sql
    CREATE DATABASE app_server CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    ```
3.  导入表结构数据。执行项目目录下的 SQL 脚本：
    *   文件路径：`doc/create_tables.sql`
    *   命令行导入示例：
        ```bash
        mysql -u root -p app_server < doc/create_tables.sql
        ```

### 3. 项目配置
修改配置文件 `app-server/src/main/resources/application.properties`：

1.  **配置环信应用信息**：
    ```properties
    application.appkey=your_org#your_app
    application.clientId=your_client_id
    application.clientSecret=your_client_secret
    
    # REST API 服务域名 (通常无需修改，除非使用私有部署)
    application.baseHttpUri=http://a1.easemob.com
    application.intranet.base.https.uri=https://a1.easemob.com
    ```
2.  **配置数据库连接**：
    ```properties
    spring.datasource.url=jdbc:mysql://127.0.0.1:3306/app_server?useSSL=false&useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    spring.datasource.username=root
    spring.datasource.password=your_password
    ```
3.  **配置 Redis 连接**（如果本地 Redis 设置了密码）：
    ```properties
    # 默认连接本地 localhost:6379，无密码
    # 如需修改，请在配置文件中添加或修改 spring.redis 相关配置
    ```

### 4. 编译与运行
进入项目根目录，执行以下 Maven 命令：

```bash
# 1. 编译并打包 (跳过测试以加快速度)
mvn clean install -DskipTests

# 2. 运行服务
# 打包完成后 jar 包位于 app-server/target 目录下
java -jar app-server/target/app-server-0.0.1-SNAPSHOT.jar
```

---

## 📚 API 接口文档

服务默认运行在 `8096` 端口。以下是核心接口说明。

> 完整服务端 REST API 文档请参考：[环信服务端集成文档](https://doc.easemob.com/document/server-side/overview.html)

### 1. 用户登录 (Login)
用于获取用户 Token，客户端 SDK 需使用此 Token 登录环信服务器。

*   **URL**: `http://localhost:8096/inside/app/user/login/V2`
*   **Method**: `POST`
*   **Content-Type**: `application/json`

**请求示例**:
```bash
curl -X POST 'http://localhost:8096/inside/app/user/login/V2' \
-H 'Content-Type: application/json' \
-d '{"phoneNumber":"15942098909", "smsCode":"123456"}'
```

**参数说明**:
| 参数名 | 类型 | 必填 | 说明 |
|---|---|---|---|
| phoneNumber | String | 是 | 手机号 |
| smsCode | String | 是 | 短信验证码 (注：当前Demo未接入真实短信服务，可任意填写) |

**响应示例**:
```json
{
    "code": 200,
    "token": "YWMt...",
    "phoneNumber": "15942098909",
    "chatUserName": "624afd8ff4",
    "avatarUrl": "http://a1.easemob.com/1152260105225458/demo/chatfiles/80d86270-f695-11f0-a9d4-41861fe06944"
}
```

### 2. 上传用户头像 (Upload Avatar)

*   **URL**: `http://localhost:8096/inside/app/user/{chatUsername}/avatar/upload`
*   **Method**: `POST`
*   **Content-Type**: `multipart/form-data`

**请求示例**:
```bash
curl -X POST http://localhost:8096/inside/app/user/624afd8ff4/avatar/upload \
-H 'Content-Type: multipart/form-data' \
-F "file=@/path/to/image.jpg"
```

**响应示例**:
```json
{
    "code": 200,
    "avatarUrl": "http://.../avatar.jpg"
}
```

### 3. 获取群组头像 (Get Group Avatar)

*   **URL**: `http://localhost:8096/inside/app/group/{groupId}/avatarurl`
*   **Method**: `GET`

**请求示例**:
```bash
curl -X GET http://localhost:8096/inside/app/group/302456782258179/avatarurl
```

**响应示例**:
```json
{
    "code": 200,
    "avatarUrl": "http://.../group_avatar.jpg"
}
```

---

## 🏗 技术栈

| 组件 | 版本 | 说明 |
|---|---|---|
| **JDK** | 21 | 编程语言基础环境 |
| **Spring Boot** | 4.0.1 | (原文档标注 4.0.1 疑有误，暂按主流高版本理解，实际以 pom.xml 为准) |
| **Spring Cloud** | 2025.1.0 | 微服务架构支持 |
| **Spring Data JPA** | 4.0.2 | 持久层框架 |
| **MySQL** | 8.0+ | 关系型数据库 |
| **Redis** | 6.0+ | 缓存服务 |
| **Lombok** | 1.18.42 | 代码简化工具 |
| **Hutool** | 5.8.35 | Java 工具包 |

---

## 🔗 相关资源
*   [环信管理后台](https://console.easemob.com/user/login)
*   [环信开发者文档](http://docs-im.easemob.com/im/start)
