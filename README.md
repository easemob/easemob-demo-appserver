![logo](./docs/img/214x70.png)

# easemob-app-server

## 简介

为客户端 Demo 提供群聊和单聊机器人功能，搭建 Agora Chat 与 ChatGPT4.0 的通讯桥梁。

* AppServer 内对接 OpenAI 的 ChatGPT4.0 模型，可以对模型参数进行配置。
* AppServer 对接 Agora Chat 发送后消息回调服务，将 @群聊机器人 和给单聊机器人发送的消息筛选出来（群聊消息需要保存一定条数的消息上下文）。
* AppServer 将消息发送给 ChatGPT4.0。
* AppServer 内调用 Rest 发送消息 API 以机器人身份将 ChatGPT4.0 回复的消息发送给群组和用户。

- 该服务目前主要提供的功能有

```
1、用户注册；
2、用户登录；
3、接收发送后消息回调；
```

## 技术选择

* [Spring Boot](https://spring.io/projects/spring-boot)
* [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
* [lombok](https://projectlombok.org/)
* [OpenAI API](https://platform.openai.com/docs/guides/text-generation/chat-completions-api)

## 主要组件

* MySQL
* Redis


## 数据库使用说明

* 使用MySQL存储用户信息
* 建表SQL见 [建表SQL](./docs/create_tables.sql)

## 使用

- 若初次使用 Agora Chat，需前往 [agora console](https://sso2.agora.io/en/v6/signup) 注册成为 Agora 开发者；

- 注册成为 Agora 开发者后，登录[agora console](https://console.agora.io/v2) 创建一个应用，之后在应用详情页可获得 App ID 和 App Cert；
- 开通 Agora Chat 服务，获取 Chat Appkey；
- 注册用户[Agora RESTful API](https://docs.agora.io/en/agora-chat/restful-api/user-system-registration?platform=react-native#registering-a-user) chatbot_ai (聊天机器人), Bella(群组默认成员), Miles(群组默认成员) 三个 Agora chat 用户用于业务层使用；
- 开通 Agora Chat [Post-delivery callbacks](https://docs.agora.io/en/agora-chat/develop/setup-webhooks?platform=react-native#post-delivery-callbacks) 服务，用于 AppServer 接收 Agora Chat 服务的消息，注意在配置回调规则时需要将 Rest 消息配置成不进行消息回调；

  - 成为 Agora 开发者并成功注册应用以及获取 Chat Appkey 后，可在自己的服务器部署服务
    - 服务配置文件参考：[application.properties](./easemob-app-server/src/main/resources/application.properties)
    - 使用自己的 appkey, baseUrl, appId, appCert 修改配置文件，如下：
    ```
        ## agora console 注册 chat 获取的 appkey以及对应的数据中心 baseUrl
        application.agora.chat.appkey=xxx#xxx
        application.agora.chat.baseUrl=http://xxx.chat.agora.io

        ## agora console 获取 appId
        application.agora.appId=xxx
        ## agora console 获取 appCert
        application.agora.appCert=xxx
    ```
    
    - 设置 ChatGPT, 使用自己的 OpenAI key 等信息修改配置文件，如下：
    ```
        chatgpt.api.key=xxx
        chatgpt.url=https://api.openai.com/v1/chat/completions
        chatgpt.model=gpt-3.5-turbo
        chatgpt.max.tokens=1700
        chatgpt.temperature=0.1
    ```

  - 安装MySQL，并根据[建表SQL](./docs/create_tables.sql)创建数据库及表，设置服务配置文件：
    ```
        spring.datasource.driver-class-name=com.mysql.jdbc.Driver
        spring.datasource.url=jdbc:mysql://127.0.0.1:3306/app_server?useSSL=false&autoReconnect=true&useUnicode=true&characterEncoding=utf8
        spring.datasource.username=root
        spring.datasource.password=123456
        spring.datasource.hikari.maximum-pool-size=100
        spring.datasource.hikari.minimum-idle=20
    ```

  - 启动服务即可

## API

### 用户注册

在 AppServer 注册用户。

**Path:** `http://localhost:8095/app/chat/user/register`

**HTTP Method:** `POST`

**Request Headers:**

| Param        | description      |
| ------------ | ---------------- |
| Content-Type | application/json |

**Request Body example:**

{"userAccount":"tom", "userPassword":"123456"}

**Request Body params:**

| Param       | Data Type | description |
|-------------| --------- |-------------|
| userAccount | String    | 用户账号名       |
| userPassword     | String    | 用户账号密码      |

**request example:**

```
curl -X POST -H 'Content-Type: application/json' -H 'Accept: application/json' 'http://localhost:8095/app/chat/user/register' -d '{"userAccount":"tom", "userPassword":"123456"}'
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

AppServer 用户登录。

**Path:** `http://localhost:8095/app/chat/user/login`

**HTTP Method:** `POST`

**Request Headers:**

| Param        | description      |
| ------------ | ---------------- |
| Content-Type | application/json |

**Request Body example:**

{"userAccount":"tom", "userPassword":"123456"}

**Request Body params:**

| Param       | Data Type | description |
|-------------| --------- |-------------|
| userAccount | String    | 用户账号名       |
| userPassword     | String    | 用户账号密码      |

**request example:**

```
curl -X POST -H 'Content-Type: application/json' -H 'Accept: application/json' 'http://localhost:8095/app/chat/user/login' -d '{"userAccount":"tom", "userPassword":"123456"}'
```

**Response Parameters:**

| Param           | Data Type | description                |
| --------------- |-----------|----------------------------|
| code            | Integer   | 响应状态码                      |
| accessToken     | String    | 用户 token，用于客户端 sdk 登录环信服务器 |
| chatUserName | String      | agora chat 用户名             |
| agoraUid | String    | agora uid                  |

**response example:**

```json
{
    "code": 200,
    "accessToken": "xxx",
    "expireTimestamp": 1716868248791,
    "chatUserName": "tom",
    "agoraUid": "1296866895"
}
```
