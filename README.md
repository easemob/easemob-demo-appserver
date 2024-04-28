# easemob-inside-app-server

## 简介

该服务为 环信 Demo 提供后端服务，可作为 App 使用环信SDK实现环信 Demo 的服务器端实现示例。

- 该服务目前提供的功能有

```
1、用户登录；
2、上传用户头像；
3、获取群组头像；
```

## 技术选择

* [Spring Boot](https://spring.io/projects/spring-boot)
* [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
* [lombok](https://projectlombok.org/)


## 主要组件

* MySQL


## 数据库使用说明

* 使用MySQL存储用户信息
* 建表SQL见 [建表SQL](./doc/create_tables.sql)

## 使用

- 若初次使用环信，需前往 [环信IM开发者注册页](https://console.easemob.com/user/register) 注册成为环信IM开发者；

- 注册成为环信IM开发者后，登录[环信IM管理后台](https://console.easemob.com/user/login) 创建一个应用(App)，之后在App详情页可获得AppKey以及AppKey的clientId和clientSecret；

    - 管理后台的使用可参考文档：[环信管理后台使用指南](http://docs-im.easemob.com/im/quickstart/essential/console)

- 成为环信IM开发者并成功注册App后，可在自己的服务器部署服务

    - 服务配置文件参考：[application.properties](./app-server/src/main/resources/application.properties)

    - AppKey组成规则：${orgName}#${appName}，拿到AppKey后可得到对应的orgName和appName；

    - 使用自己的orgName和appName以及AppKey的clientId和clientSecret修改配置文件，如下：
    ```
        application.appkey=XXX
        application.baseHttpUri=http://XXX.easemob.com
        application.clientId=XXX
        application.clientSecret=XXX
        application.intranet.base.https.uri.=https://XXX.easemob.com
    ```

    - 安装MySQL，并根据[建表SQL](./doc/create_tables.sql)创建数据库及表，设置服务配置文件：
    ```
        spring.datasource.driver-class-name=com.mysql.jdbc.Driver
        spring.datasource.url=jdbc:mysql://127.0.0.1:3306/app_server?useSSL=false&useUnicode=true&characterEncoding=utf8
        spring.datasource.username=root
        spring.datasource.password=123456
    ```

    - 启动服务即可


## 环信文档

[服务端REST文档](https://doc.easemob.com/document/server-side/overview.html)

## API

### 用户登录

用户登录并获取用户 token，用于客户端 sdk 登录环信服务器。

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

