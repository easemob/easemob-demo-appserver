![logo](./docs/img/214x70.png)


# Easemob IM App Server


## 简介

该服务为 环信直播间Demo 提供后端服务，可作为 App 使用环信SDK实现直播间的服务器端实现示例。

- 该服务目前提供的功能有

```
1、创建直播间；
2、修改直播间详情；
3、获取直播间详情；
4、获取直播间列表；
5、获取正在直播的直播间列表；
6、开启直播；
7、结束直播；
8、转让直播间；
9、删除直播间；
10、获取推拉流地址。
```

## 技术选择

* [Spring Boot](https://spring.io/projects/spring-boot)
* [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
* [lombok](https://projectlombok.org/)


## 主要组件

* MySQL


## 数据库使用说明

* 使用MySQL存储直播间信息
* 建表SQL见 [建表SQL](./docs/create_tables.sql)


## 直播间API文档

[直播间Demo服务端API文档](./docs/直播间API文档.md)


## 环信直播间文档

[环信直播间集成文档](http://docs-im.easemob.com/im/other/integrationcases/live-chatroom)


## 使用

- 若初次使用环信，需前往 [环信IM开发者注册页](https://console.easemob.com/user/register) 注册成为环信IM开发者；

- 注册成为环信IM开发者后，登录[环信IM管理后台](https://console.easemob.com/user/login) 创建一个应用(App)，之后在App详情页可获得AppKey以及AppKey的clientId和clientSecret；

    - 管理后台的使用可参考文档：[环信管理后台使用指南](http://docs-im.easemob.com/im/quickstart/essential/console)

- 成为环信IM开发者并成功注册App后，可在自己的服务器部署服务

    - 服务配置文件参考：[application.properties](./easemob-im-live-server/src/main/resources/application.properties)
    
    - AppKey组成规则：${orgName}#${appName}，拿到AppKey后可得到对应的orgName和appName；
    
    - 使用自己的orgName和appName以及AppKey的clientId和clientSecret修改配置文件，如下：
    ```
        easemob.live.rest.appkey.orgName=easemob-demo
        easemob.live.rest.appkey.appName=chatdemoui
        easemob.live.rest.appkey.clientId=xxxx
        easemob.live.rest.appkey.clientSecret=xxxx
    ```
    
    - 安装MySQL，并根据[建表SQL](./docs/create_tables.sql)创建数据库及表，设置服务配置文件：
    ```
        spring.datasource.url=jdbc:mysql://127.0.0.1:3306/app_server?useSSL=false&useUnicode=true&characterEncoding=utf8
        spring.datasource.username=root
        spring.datasource.password=123456
    ```
    
    - 启动服务即可


## 模块说明

#### [liveroom模块](./easemob-im-live-server/src/main/java/com/easemob/live/server/liveroom)

- 提供直播间Rest Api服务，包含controller、model、service、exception等。

#### [rest模块](./easemob-im-live-server/src/main/java/com/easemob/live/server/rest)

- 直播间服务需调用环信REST接口，该模块提供调用环信REST服务，包含token、user、chatroom API的调用，封装了调用所需的RequestBody、ResponseBody。

#### [qiniu-service](./qiniu-service)

- 获取七牛云直播推拉流地址。


## 环信文档

[服务端REST文档](http://docs-im.easemob.com/im/server/ready/intro)
