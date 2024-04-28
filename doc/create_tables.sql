CREATE TABLE `easemob_app_user_info_new`
(
    `id`                 bigint(20) NOT NULL AUTO_INCREMENT,
    `appkey`             varchar(512) NOT NULL COMMENT 'appkey',
    `phone_number`       varchar(11)  NOT NULL COMMENT '手机号',
    `avatar_url`         varchar(200) DEFAULT NULL COMMENT '用户头像url',
    `chat_user_name`     varchar(32)  NOT NULL COMMENT '环信用户名',
    `chat_user_password` varchar(32)  NOT NULL COMMENT '环信用户密码',
    `agora_uid`          varchar(20)  NOT NULL COMMENT '声网用户id',
    `updated_at`         datetime(6) NOT NULL COMMENT '用户更新时间',
    `created_at`         datetime(6) NOT NULL COMMENT '用户创建时间',
    PRIMARY KEY (`id`, `phone_number`, `chat_user_name`),
    UNIQUE KEY `uniq_appkey_phone_number` (`appkey`,`phone_number`),
    KEY `idx_appkey_phone_number` (`appkey`(100),`phone_number`),
    KEY `idx_appkey_chat_username` (`appkey`(100),`chat_user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
