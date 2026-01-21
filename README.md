# easemob-im-app-server

## 简介

该服务为 环信 Demo 提供后端服务，可作为 App 使用环信SDK实现环信 Demo 的服务器端实现示例。

- 该服务目前提供的功能有

```
1、用户登录；
2、上传用户头像；
3、获取群组头像；
```

## 技术选择与版本信息
* [JDK 21](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
* [Spring Boot 4.0.1](https://docs.spring.io/spring-boot/system-requirements.html)
* [Spring Cloud 2025.1.0](https://spring.io/projects/spring-cloud#learn)
* [Spring Data JPA 4.0.2](https://spring.io/projects/spring-data-jpa)
* [lombok 1.18.42](https://projectlombok.org/)
* [Hutool 5.8.35](https://github.com/dromara/hutool)
* [Fastjson2 2.0.51](https://github.com/alibaba/fastjson2)  
* [Commons Lang3 3.18.0](https://commons.apache.org/proper/commons-lang/)
* [Guava 33.4.0-jre](https://github.com/google/guava)
* [Jackson 2.19.1](https://github.com/FasterXML/jackson)
* [mysql-connector-j 9.1.0](https://github.com/mysql/mysql-connector-j/tree/9.1.0)


## 数据库使用说明
* 数据库选择：MySQL 8.0+
* 使用MySQL存储用户信息，需要先创建数据库`app_server`
* 建表SQL见 [建表SQL](./doc/create_tables.sql)

## 使用

- 若初次使用环信，需前往[环信IM开发者注册页](https://console.easemob.com/user/register) 注册成为环信IM开发者；

- 注册成为环信IM开发者后，登录[环信IM管理后台](https://console.easemob.com/user/login) 创建一个应用(App)，之后在App详情页可获得AppKey以及AppKey的clientId和clientSecret；

- 管理后台的使用可参考文档：[环信管理后台使用指南](http://docs-im.easemob.com/im/quickstart/essential/console)

- 成为环信IM开发者并成功注册App后，可在自己的服务器部署服务
 - 服务配置文件参考：[application.properties](./app-server/src/main/resources/application.properties)

    - AppKey组成规则：orgName#appName，拿到AppKey后可得到对应的orgName和appName；

    - 使用自己的orgName和appName以及AppKey的clientId和clientSecret修改配置文件，如下：
    - 其中baseHttpUri和intranet.base.https.uri中的XXX是REST API服务的服务器域名前缀
    ```
        application.appkey=XXX
        application.baseHttpUri=http://XXX.easemob.com
        application.clientId=XXX
        application.clientSecret=XXX
        application.intranet.base.https.uri.=https://XXX.easemob.com
    ```

    - 安装MySQL 8.0+，并根据[建表SQL](./doc/create_tables.sql)创建数据库及表，设置服务配置文件：
    ```
        spring.datasource.driver-class-name=com.mysql.jdbc.Driver
        spring.datasource.url=jdbc:mysql://127.0.0.1:3306/app_server?useSSL=false&useUnicode=true&characterEncoding=utf8
        spring.datasource.username=root
        spring.datasource.password=123456
    ```

    - 启动服务即可

## maven一键打包操作流程
- 按照上述操作完成JDK、Spring Boot、MySQL等环境的安装配置；
- 进入项目根目录，终端执行如下命令，即可完成'一键式'构建流程，涵盖：代码编译、测试运行、打包jar文件等内容；
```
mvn clean install
```

- 打包完成后，在`app-server/target`目录下可找到打包好的jar文件；

## Docker 环境快速部署

本服务已提供完整的 Docker 运行环境，支持一键启动。

### 前置要求
* Docker & Docker Compose
* Windows/Linux/MacOS

### 启动步骤
1. **Windows**: 双击运行根目录下的 `start.bat`。
   **Linux/Mac**: 在终端执行 `docker-compose up -d --build`。
2. 首次运行时，脚本会自动检测并生成 `.env` 配置文件。
3. 请打开 `.env` 文件，填入您的环信 AppKey、Client ID 以及 Client Secret。
4. 保存文件后，再次运行启动脚本即可。

### 服务信息
* **App Server**: http://localhost:8096
* **MySQL**: 端口 3307 (账号 root / 密码 123456)
* **Redis**: 端口 6379

### 镜像源说明
项目已配置智能镜像源切换，默认使用阿里云/华为云镜像，确保国内构建速度。

## 环信文档

[服务端REST文档](https://doc.easemob.com/document/server-side/overview.html)

## API

### 用户登录

用户登录并获取用户 token，用于客户端 sdk 登录环信服务器。

说明：目前用户登录使用手机号+短信验证码的方式，发送短信验证码服务需要自己进行对接，目前 app-server 内没有对用户登录的短信验证码进行验证，发送短信验证码以及用户登录对短信验证码验证需要自己进行处理。

**Path:** `http://localhost:8096/inside/app/user/login/V2`

**HTTP Method:** `POST`

**Request Headers:**

| Param        | description      |
| ------------ | ---------------- |
| Content-Type | application/json |

**Request Body example:**

{"phoneNumber":"15942098909", "smsCode":"123456"}

**Request Body params:**

| Param       | Data Type | description |
|-------------| --------- |-------------|
| phoneNumber | String    | 手机号         |
| smsCode     | String    | 短信验证码       |

**request example:**

```
curl -X POST -H 'Content-Type: application/json' -H 'Accept: application/json' 'http://localhost:8096/inside/app/user/login/V2' -d '{"phoneNumber":"15942098909", "smsCode":"123456"}'
```

**Response Parameters:**

| Param           | Data Type | description                |
| --------------- |-----------|----------------------------|
| code            | Integer   | 响应状态码                      |
| token     | String    | 用户 token，用于客户端 sdk 登录环信服务器 |
| phoneNumber     | String    | 手机号                        |
| chatUserName | String      | 环信 id                      |
| avatarUrl | String    | 用户头像 url                   |

**response example:**

```json
{
    "code": 200,
    "token": "xxx",
    "phoneNumber": "xxx",
    "chatUserName": "xxx",
    "avatarUrl": "xxx"
}
```

---

### 上传用户头像

**Path:** `http://localhost:8096/inside/app/user/{chatUsername}/avatar/upload`

**HTTP Method:** `POST`

**Request Headers:**

| Param        | description      |
| ------------ | ---------------- |
| Content-Type | multipart/form-data |

**Request Body example:**
file=@/Users/XXX/image.jpg

**Request Body params:**

| Param   | description |
|---------|-------------|
| file    | 头像本地路径      |

**request example:**

```
curl -X POST http://localhost:8096/inside/app/user/jack/avatar/upload -H 'content-type: multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW' -F file=@/Users/XXX/image.jpg
```

**Response Parameters:**

| Param           | Data Type | description                |
| --------------- |-----------|----------------------------|
| code            | Integer   | 响应状态码                      |
| avatarUrl | String    | 用户头像 url                   |

**response example:**

```json
{
    "code": 200,
    "avatarUrl": "xxx"
}
```

---

### 获取群组头像

**Path:** `http://localhost:8096/inside/app/group/{groupId}/avatarurl`

**HTTP Method:** `GET`

**request example:**

```
curl -X GET http://localhost:8096/inside/app/group/242023244300303/avatarurl
```

**Response Parameters:**

| Param           | Data Type | description |
| --------------- |-----------|-------------|
| code            | Integer   | 响应状态码       |
| avatarUrl | String    | 群组头像 url    |

**response example:**

```json
{
    "code": 200,
    "avatarUrl": "xxx"
}
```

