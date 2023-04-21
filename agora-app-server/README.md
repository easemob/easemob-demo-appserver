# AgoraAppServer

## 介绍
AgoraAppServer是用来获取声网 Rtc Token 的开源项目以及简单的维护了声网频道内 User Account（即 Chat User） 与 AgoraUid 的映射关系。

## 功能
- AppServer 为用户生成 Rtc Token，用户拿到 Rtc Token 可以在客户端加入声网频道使用音视频功能。
- AppServer 维护 Channel 内 User Account 与 AgoraUid 的映射关系，拿到映射关系设置给客户端 EaseCallKit 使用。

## 技术选择
* [Spring Boot](https://spring.io/projects/spring-boot)

## 主要组件
* Redis

## 准备
在获取声网 token 之前，需要准备环信 AppKey、声网 AppId（AppId）、声网 APP证书（AppCert）

* 获取环信 AppKey：
    - 1.如果您有环信管理后台账号并创建过应用，请先登录环信管理后台，点击[这里](https://console.easemob.com/user/login)，然后到"应用列表" -> 点击"查看"即可获取到appkey。
    - 2.如果您没有环信管理后台账号，请先注册账号，点击[这里](https://console.easemob.com/user/register)，注册成功后请登录，然后点击"添加应用"，添加成功后点击"查看"即可获取到appkey。

* 获取 AppId、AppCert：
    - 1.如果您有声网 Console 后台账号并创建过项目，请先登录声网 Console  后台，点击[这里](https://sso.agora.io/cn/login/)，然后到"项目列表" -> 找到自己的项目点击"编辑"图标后，即可看到 App ID、APP 证书。
    - 2.如果您没有声网Console后台账号，请先注册账号，点击[这里](https://sso.agora.io/cn/v4/signup)，注册成功后按照步骤1操作。

* 您需要自己来实现用户登录时的认证、授权由您自己来完成。

## 配置
配置文件中需要的参数来源于"准备"中获取到的环信 appkey、声网 AppId（AppId）、声网 APP证书（AppCert）。

* 服务配置文件参考：[application.properties](https://github.com/easemob/easemob-im-app-server/blob/master/agora-app-server/src/main/resources/application.properties)
    ```
        ## 到环信 console 获取自己 AppKey 对应的 Rest 域名
        application.restServer=xxx
        
        ## 环信 console 注册获取的 orgname
        application.orgName=xxx
        ## 环信 console 注册获取的 appname
        application.appName=xxx
        ## 声网 console 获取 appid
        application.agoraAppId=XXX
        ## 声网 console 获取 appcert
        application.agoraCert=XXX
        
        ## redis
        spring.redis.channel.host=localhost
        spring.redis.channel.port=6379
        spring.redis.channel.password=123456
        spring.redis.channel.timeout=10000
        ### channel 映射关系过期时间
        spring.redis.channel.expireTime=86400
        
    ```

## 使用

上述准备好，启动服务即可使用。

## API

### 获取声网 Rct Token。
在获取 Rtc Token 时，会将 User Account 与 AgoraUid 的映射 存入 Redis。

**Path:** `http://localhost:8086/token/rtc/channel/{channelName}/agorauid/{agoraUid}`

**HTTP Method:** `GET`

**Request Headers:** 

| 参数 | 说明  |
| --- | --- |
| Accept | application/json |

**路径参数说明:** 
| 参数 | 类型 | 说明  |
| --- | --- | --- |
| channelName | String | 声网频道名称|
| agoraUid | Integer | 声网 uid，如果不传服务端会随机生成 |

**查询参数说明:** 
| 参数 | 类型 | 说明  |
| --- | --- | --- |
| userAccount | String | Chat User 用户名 |


**请求示例:**

```
curl -X GET -H 'Accept: application/json' 'http://localhost:8086/token/rtc/channel/{channelName}/agorauid/{agoraUid}?userAccount=xxx'
```

**Response Parameters:**

| 参数 | 类型 | 说明  |
| --- | --- | --- |
| code | String | 结果状态码 |
| token | String | 声网 Rtc Token |
| expireTime | Integer | Token有效期，单位:秒 |

**返回示例:**

```json
{
    "code": "RES_0K",
    "token": "xxx",
    "expireTime": 86400
}
```

---

### 根据声网频道名称获取 User Account 与 Agora Uid 映射。

**Path:** `http://localhost:8086/agora/channel/mapper`

**HTTP Method:** `GET`

**Request Headers:** 

| 参数 | 说明  |
| --- | --- |
| Accept | application/json |

**查询参数说明:** 
| 参数 | 类型 | 说明  |
| --- | --- | --- |
| channelName | String | 声网频道名称|

**请求示例:**

```
curl -X GET -H 'Accept: application/json' 'http://localhost:8086/agora/channel/mapper?channelName=xxx'
```


**Response Parameters:**

| 参数 | 类型 | 说明  |
| --- | --- | --- |
| code | String | 结果状态码 |
| channelName | String | 声网频道名称 |
| result | Json | 频道内 agoraUid 与 chat user 的映射数据，key 为 agoraUid，value 为 userAccount |

**返回示例:**

```json
{
    "code": "RES_0K",
    "channelName": "channelName1",
    "result": {
        "12312344": "t2"
    }
}
```
