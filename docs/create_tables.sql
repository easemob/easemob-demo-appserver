create database app_server character set utf8mb4;

create table live_room_details (
    id bigint not null comment '直播间ID，即对应的聊天室 ID，聊天室唯一标识符，由环信服务器生成',
    name varchar(512) not null comment '直播间名称，即对应的聊天室名称，任意字符串',
    description varchar(512) comment '直播间描述，即对应的聊天室描述，任意字符串',
    created bigint comment '直播间创建时间戳',
    owner varchar(512) comment '直播间主播的username，也是对应聊天室的所有者',
    showid bigint comment '直播场次ID',
    status integer comment '直播状态',
    cover varchar(512) comment '直播间封面Url',
    affiliations_count integer comment '直播间人数',
    ext varchar(1024) comment '直播间扩展参数',
    primary key (id),
    index SHOW_STATUS (status)
) engine=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE live_room_details ADD persistent bit NOT NULL DEFAULT 1;

ALTER TABLE live_room_details ADD video_type integer NOT NULL DEFAULT 0;
