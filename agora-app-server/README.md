# AgoraAppServer

## 介绍
AgoraAppServer是用来获取声网Token的开源项目。

## 功能
AgoraAppServer提供了获取声网Token、获取频道内环信id与声网id映射关系的能力。

## 技术选择
* [Spring Boot](https://spring.io/projects/spring-boot)

## 主要组件
* [Jedis](https://tool.oschina.net/uploads/apidocs/redis/clients/jedis/Jedis.html)
* [AgoraIO Tools](https://github.com/AgoraIO/Tools/tree/dev/accesstoken2/DynamicKey/AgoraDynamicKey/java)

## 准备
在获取声网token之前，需要准备环信AppKey、REST API访问地址(restServer)、声网App Id(agroaAppId)、声网APP证书(agoraCert)

获取环信appkey、restServer：
- 1.如果您有环信管理后台账号并创建过应用，请先登录环信管理后台，点击[这里](https://console.easemob.com/user/login)，然后到"应用列表" -> 点击"查看"即可获取到appkey。
- 2.如果您没有环信管理后台账号，请先注册账号，点击[这里](https://console.easemob.com/user/register)，注册成功后请登录，然后点击"添加应用"，添加成功后点击"查看"即可获取到appkey。
- 3.然后到"即时通讯" -> "服务概览"，拿到REST API访问地址

获取agroaAppId、appCert：
- 1.如果您有声网Console后台账号并创建过项目，请先登录声网Console后台，点击[这里](https://sso.agora.io/cn/login/)，然后到"项目列表" -> 找到自己的项目点击"编辑"图标后，即可看到App ID、APP证书。
- 2.如果您没有声网Console后台账号，请先注册账号，点击[这里](https://sso.agora.io/cn/v4/signup)，注册成功后按照步骤1操作。

## 配置
配置文件中需要的参数来源于"准备"中获取到的环信appkey、REST API访问地址(restServer)、声网App Id(agroaAppId)、声网APP证书(agoraCert)
- 服务配置文件参考：[application.properties](./agora-app-server/src/main/resources/application.properties)
    - AppKey组成规则：${orgName}#${appName}，拿到AppKey后可得到对应的orgName和appName；
    ```
        ## 环信console 获取自己的rest域名
        application.restServer=xxx
        ## 环信console 注册获取的orgname
        application.orgName=xxx
        ## 环信console 注册获取的appname
        application.appName=xxx
        
        ## 声网console获取appid
        application.agoraAppId=xxx
        ## 声网console获取appcert
        application.agoraCert=xxx
        ## 声网token过期时间(自已定义，不能超过1天)
        agora.token.expire.period.seconds=86400
        
        ## 本地redis
        spring.redis.channel.host=localhost
        spring.redis.channel.port=6379
        spring.redis.channel.password=123456
        spring.redis.channel.timeout=10000
        spring.redis.channel.expireTime=86400
    ```

## 使用

上述准备好，启动服务即可使用。

## API

**API说明:** 获取声网Token。

**Path:** `http://localhost:8080/token/rtcToken/v1`

**HTTP Method:** `GET`

**Permission:** App管理员

**Request Parameters:**

| 参数 | 类型 | 说明  |
| --- | --- | --- |
| userAccount | String | 用户名（环信的用户名） |
| channelName | String | 要加入的声网频道名称 |
| appkey      | String| 环信的appkey |
| agoraUserId | Integer | 声网用户id(传0，会随机生成一个1~Integer.MAX_VALUE的数字) |

**Request Headers:** 

| 参数 | 说明  |
| --- | --- |
| Content-Type  | application/json |
| Authorization | Bearer ${token} |

**Request Body示例:** 无

**Request Body参数说明:** 无


**请求示例:**

```
curl -X GET "http://localhost:8080/token/rtcToken/v1?userAccount=test1&channelName=live&appkey=easemob-demo%23test&agoraUserId=0" -H 'Authorization: Bearer YWMthkKMWmqAEeuWOHGo9_t4wU1-S6DcShHjkNXh_7qs2vUf00sg7jER6bLYLZbUGGVWAwMAAAF3hKuxYwBPGgCO8gCSzYq-vmRwQVc23oJ2n1HxZmHJIJPedliCLokptg'
```

**Response Parameters:**

| 参数 | 类型 | 说明  |
| --- | --- | --- |
| code | String | 结果状态码 |
| accessToken | String | 声网token |
| expireTime | int | 过期时间单位（秒） |

**返回示例:**

```json
{
    "code": "RES_0K",
    "accessToken": "006970CA35de60c44645bbae8a215061b33IACMDSlu5iFb6KRUFHJfPGkwABytAJfHZb3PGR7S+2cGUwYf3+74PfJ8IgD4AIB/3H0vYAQAAQBsOi5gAgBsOi5gAwBsOi5gBABsOi5g",
    "expireTime": 86400,
    "agoraUserId":123
}
```


## 获取声网频道信息

**API说明:** 根据频道名称获取频道详情。

**Path:** `http://localhost:8080/channel/mapper`

**HTTP Method:** `GET`

**Permission:** App管理员

**Request Parameters:**

| 参数 | 类型 | 说明  |
| --- | --- | --- |
| channelName | String | 要加入的声网频道名称 |
| userAccount | String | 用户名（环信的用户名） |
| appkey      | String| 环信的appkey |

**Request Headers:** 

| 参数 | 说明  |
| --- | --- |
| Content-Type  | application/json |
| Authorization | Bearer ${token} |

**Request Body示例:** 无

**Request Body参数说明:** 无


**请求示例:**

```
curl -X GET "http://localhost:8080/channel/mapper?channelName=live&appkey=easemob-demo%23test&userAccount=test1" -H 'Authorization: Bearer YWMthkKMWmqAEeuWOHGo9_t4wU1-S6DcShHjkNXh_7qs2vUf00sg7jER6bLYLZbUGGVWAwMAAAF3hKuxYwBPGgCO8gCSzYq-vmRwQVc23oJ2n1HxZmHJIJPedliCLokptg'
```

**Response Parameters:**

| 参数 | 类型 | 说明  |
| --- | --- | --- |
| code | String | 结果状态码 |
| channelName | String | 声网频道名称 |
| result | JSON| agoraUserId与环信id的映射 |

**返回示例:**

```json
{
    "code": "RES_0K",
    "channelName": "channelName",
    "result": {"123":"1"}
}
```
