# AgoraAppServer
AgoraAppServer用来获取声网Token

## 获取声网token

**API说明:** 获取声网token。

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
