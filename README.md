# easemob-im-app-server

## 简介

该服务为 环信 Demo 提供后端服务，可作为 App 使用环信SDK实现 1v1 视频通话环信 Demo 的服务器端实现示例。

- 该服务目前提供的主要功能有

```
1、发送短信验证码；
2、用户登录；
3、上传用户头像；
4、1v1视频匹配用户；
5、1v1视频取消匹配；
6、1v1视频获取用户匹配状态；
7、获取声网 rtc token
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
        application.1v1.video.appkey=XXX
        application.baseUri=https://XXX.easemob.com
        application.clientId=XXX
        application.clientSecret=XXX
    ```
    - 到声网获取[声网](https://www.shengwang.cn/?utm_campaign=pinzhuan&utm_medium=cpc&utm_source=baidu&utm_content=pinzhuan&utm_term=pinzhuan)AppId和AppCert，修改配置文件，如下：
    ```
        ### 声网token过期时间(自已定义，不能超过1天)
        agora.token.expire.period.seconds=86400
        ### 声网console获取appid
        application.agoraAppId=XXX
        ### 声网console获取appcert
        application.agoraCert=XXX
    ```

    - 安装MySQL，并根据[建表SQL](./doc/create_tables.sql)创建数据库及表，设置服务配置文件：
    ```
        spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
        spring.datasource.url=jdbc:mysql://127.0.0.1:3306/app_server?useSSL=false&autoReconnect=true&useUnicode=true&characterEncoding=utf8
        spring.datasource.username=root
        spring.datasource.password=123456789
        spring.datasource.hikari.maximum-pool-size=50
        spring.datasource.hikari.minimum-idle=20
    ```
  - 本地启动redis，设置服务配置文件：
    ```
        spring.redis.channel.nodes=tcp://127.0.0.1:6379
        spring.redis.channel.password=
        spring.redis.channel.timeout=10000
        spring.redis.channel.expireTime=86400
    ```

    - 启动服务即可


## 环信文档

[服务端REST文档](https://doc.easemob.com/document/server-side/overview.html)

## API

### 发送短信验证码

说明：目前用户登录使用手机号+短信验证码的方式，发送短信验证码服务需要自己进行对接，目前 app-server 内没有对用户登录的短信验证码进行验证，发送短信验证码以及用户登录对短信验证码验证需要自己进行处理。

**Path:** `http://localhost:8096/inside/app/sms/send/{phoneNumber}`

**HTTP Method:** `POST`

**Request Headers:**

| Param        | description      |
| ------------ | ---------------- |
| Content-Type | application/json |

**request example:**

```
curl -X POST -H 'Content-Type: application/json' -H 'Accept: application/json' 'http://localhost:8096/inside/app/sms/send/15942098909'
```

**Response Parameters:**

| Param           | Data Type | description                |
| --------------- |-----------|----------------------------|
| code            | Integer   | 响应状态码                      |

**response example:**

```json
{
    "code": 200
}
```

---

### 用户登录

用户登录并获取用户 token，用于客户端 sdk 登录环信服务器。

说明：目前用户登录使用手机号+短信验证码的方式，发送短信验证码服务需要自己进行对接，目前 app-server 内没有对用户登录的短信验证码进行验证，发送短信验证码以及用户登录对短信验证码验证需要自己进行处理。

**Path:** `http://localhost:8096/inside/app/user/1v1/video/login`

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
curl -X POST -H 'Content-Type: application/json' -H 'Accept: application/json' 'http://localhost:8096/inside/app/user/1v1/video/login' -d '{"phoneNumber":"15942098909", "smsCode":"123456"}'
```

**Response Parameters:**

| Param           | Data Type | description                |
| --------------- |-----------|----------------------------|
| code            | Integer   | 响应状态码                      |
| token     | String    | 用户 token，用于客户端 sdk 登录环信服务器 |
| phoneNumber     | String    | 手机号                        |
| chatUserName | String      | 环信 id                      |
| avatarUrl | String    | 用户头像 url                   |
| agoraUid | String    | 声网 uid                     |

**response example:**

```json
{
    "code": 200,
    "token": "xxx",
    "phoneNumber": "xxx",
    "chatUserName": "xxx",
    "avatarUrl": "xxx",
    "agoraUid": "xxx"
}
```

---

### 上传用户头像

**Path:** `http://localhost:8096/inside/app/user/{phoneNumber}/1v1/video/avatar/upload`

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
curl -X POST http://localhost:8096/inside/app/user/15942098909/1v1/video/avatar/upload -H 'content-type: multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW' -F file=@/Users/XXX/image.jpg
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

### 1v1视频匹配用户

**Path:** `http://localhost:8096/inside/app/user/1v1/video/match`

**HTTP Method:** `POST`

**Request Headers:**

| Param        | description      |
| ------------ | ---------------- |
| Content-Type | application/json |
| Authorization | Bearer {userToken} |

**Request Body example:**

{"phoneNumber":"15942098909", "sendCancelMatchNotify":true}

**Request Body params:**

| Param       | Data Type | description |
|-------------|-----------|-------------|
| phoneNumber | String    | 手机号         |
| sendCancelMatchNotify     | boolean   | 是否发送取消匹配的cmd消息通知，默认为true       |

**request example:**

```
curl -X POST -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'Authorization: Bearer xxx' 'http://localhost:8096/inside/app/user/1v1/video/match' -d '{"phoneNumber":"15942098909", "sendCancelMatchNotify":true}'
```

**Response Parameters:**

| Param           | Data Type | description  |
| --------------- |-----------|--------------|
| code            | Integer   | 响应状态码        |
| rtcToken     | String    | 声网 rtc token |
| channelName     | String    | 声网频道名称       |
| matchedUser | String      | 匹配到的用户名(手机号) |
| matchedChatUser | String    | 配到的用户名对应的环信用户     |
| agoraUid | String    | 声网 uid       |

**response example:**

```json
{
    "code": 200,
    "agoraUid": "xxx",
    "channelName": "xxx",
    "rtcToken": "xxx",
    "matchedUser": "xxx",
    "matchedChatUser" : "xxx"
}
```

---

### 1v1视频取消匹配

**Path:** `http://localhost:8096/inside/app/user/1v1/video/match`

**HTTP Method:** `DELETE`

**Request Headers:**

| Param        | description      |
| ------------ | ---------------- |
| Content-Type | application/json |
| Authorization | Bearer {userToken} |

**request example:**

```
curl -X DELETE -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'Authorization: Bearer xxx' 'http://localhost:8096/inside/app/user/1v1/video/match'
```

**Response Parameters:**

| Param           | Data Type | description  |
| --------------- |-----------|--------------|
| code            | Integer   | 响应状态码        |

**response example:**

```json
{
    "code": 200
}
```

---

### 1v1视频获取用户匹配状态

**Path:** `http://localhost:8096/inside/app/user/{chatUsername}/1v1/video/match/status`

**HTTP Method:** `GET`

**Request Headers:**

| Param  | description      |
|--------| ---------------- |
| Accept | application/json |

**request example:**

```
curl -X GET -H 'Accept: application/json' 'http://localhost:8096/inside/app/user/tom/1v1/video/match/status'
```

**Response Parameters:**

| Param           | Data Type | description |
| --------------- |-----------|-------------|
| code            | Integer   | 响应状态码       |
| matchStatus            | String    | 匹配状态， unmatch：未匹配，matching：匹配中，matched：已匹配      |

**response example:**

```json
{
    "code": 200,
    "matchStatus": "unmatch"
}
```

---

### 获取声网 rtc token
此 api 由服务器端自动生成 agoraUid 进行返回，只需要传 channelName 和 phoneNumber 即可获取 rtc token。

**Path:** `http://localhost:8096/inside/token/rtc/channel/{channelName}/phoneNumber/{phoneNumber}/1v1video`

**HTTP Method:** `GET`

**Request Headers:**

| Param  | description      |
|--------| ---------------- |
| Accept | application/json |

**request example:**

```
curl -X GET -H 'Accept: application/json' 'http://localhost:8096/inside/token/rtc/channel/textchannel/phoneNumber/15942082992/1v1video'
```

**Response Parameters:**

| Param           | Data Type | description |
| --------------- |-----------|-------------|
| code            | Integer   | 响应状态码       |
| accessToken            | String    | rtc token   |
| expireTimestamp            | long      | token 过期时间  |

**response example:**

```json
{
    "code": 200,
    "accessToken": "xxx",
    "expireTimestamp": 1710471789995
}
}
```

---


