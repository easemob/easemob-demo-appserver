# AgoraAppServer
AgoraAppServer用来获取声网Token
环信AppServer通过http接口获取Token

**Path:** `http://localhost:8080/token/rtcToken/v1`

**HTTP Method:** `GET`

**Permission:** App管理员

**Request Parameters:**

```json
| 参数 | 类型 | 说明  |
| --- | --- | --- |
| userAccount | String | 用户名（环信的用户名） |
| channelName | String | 要加入的声网频道名称 |
| appkey      | String| 环信的appkey |
| agoraUserId | Integer | 声网用户id(传0，会随机生成一个1~Integer.MAX_VALUE的数字) |
```

**Request Headers:** 

| 参数 | 说明  |
| --- | --- |
| Content-Type  | application/json |
| Authorization | Bearer ${token} |

**Request Body示例:**

**Request Body参数说明:** 无

**请求示例:**

```
curl -X GET "http://localhost:8080/token/rtcToken/v1?userAccount=test1&channelName=live&appkey=easemob-demo%23test&agoraUserId=0" -H 'Authorization: Bearer YWMthkKMWmqAEeuWOHGo9_t4wU1-S6DcShHjkNXh_7qs2vUf00sg7jER6bLYLZbUGGVWAwMAAAF3hKuxYwBPGgCO8gCSzYq-vmRwQVc23oJ2n1HxZmHJIJPedliCLokptg'
```

**返回示例:**

```json
{
    "code": "RES_0K",
    "accessToken": "006970CA35de60c44645bbae8a215061b33IACMDSlu5iFb6KRUFHJfPGkwABytAJfHZb3PGR7S+2cGUwYf3+74PfJ8IgD4AIB/3H0vYAQAAQBsOi5gAgBsOi5gAwBsOi5gBABsOi5g",
    "expireTime": 86400,
    "agoraUserId":123
}
