![logo](./docs/img/214x70.png)


# Easemob IM App Server


## 简介

该服务为 环信直播间Demo 提供后端服务，可作为 App 使用环信SDK实现直播间的服务器端实现示例。

```
1、每一个直播间，都唯一对应一个聊天室， 直播间复用了对应聊天室的所有资源，包括聊天室详情及成员列表等；
2、新建一个直播间会同时新建一个聊天室；新建一个直播间时，该直播间的直播状态为“未直播”，当前直播场次ID为0（直播场次ID默认为0，没开始一场直播，直播场次ID加1）；
3、删除直播间后，聊天室成员会被移出聊天室，直播间所有信息会被删除，用户不可再加入该直播间；
4、（1.0.3.Final版本新增功能）直播间增加persistent属性，默认为true，当设置为false时，直播间停播一个小时后，会被自动删除；
5、（1.0.3.Final版本新增功能）直播间支持点播类型。详情请查看直播间API文档。
```

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


## Docker Deploy

* 如您需要使用 Docker 部署服务，可以参考此流程。
* [Dockerfile](./easemob-im-live-server/Dockerfile)
* [Properties for Docker service](./easemob-im-live-server/docker/application-docker.properties)

```
# 打包
mvn clean install -DskipTests

# 进入Dockerfile目录
cd easemob-im-live-server/

# build docker image
mvn com.spotify:dockerfile-maven-plugin:build

# 至此服务的docker image已经build完成，启动服务需要先启动本地或docker的MySQL服务，并正确配置application-docker.properties中的mysql数据源地址。
# run docker service
docker run -p 8080:8080 easemob/easemob-im-live-server
```


## 模块说明

#### [liveroom模块](./easemob-im-live-server/src/main/java/com/easemob/live/server/liveroom)

- 提供直播间Rest Api服务，包含controller、model、service、exception等。

#### [rest模块](./easemob-im-live-server/src/main/java/com/easemob/live/server/rest)

- 直播间服务需调用环信REST接口，该模块提供调用环信REST服务，包含token、user、chatroom API的调用，封装了调用所需的RequestBody、ResponseBody。

#### [qiniu-service](./qiniu-service)

- 获取七牛云直播推拉流地址。


## 环信文档

[服务端REST文档](http://docs-im.easemob.com/im/server/ready/intro)


## Release Note

### 1.0.3.Final
- 直播间支持过期自动清理
- 直播间支持点播功能
- 直播间支持Docker部署

### 1.0.2.Final
- 支持获取七牛云推拉流地址
