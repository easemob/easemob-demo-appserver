package com.easemob.app.config.redis;

public final class RedisKeyConstants {
    public static final String AGORA_CHANNEL_INFO = "agora:channel:info:%s:%s";

    public static final String AGORA_UID = "agora:uid:%s:%s";

    public static final String PHONE_SMS_CODE = "phone:sms:code:%s";

    public static final String PHONE_SMS_CODE_RECORD = "phone:number:record:%s";

    public static final String PHONE_SMS_CODE_VALIDITY = "phone:sms:code:validity:%s";

    public static final String PHONE_SMS_CODE_SEND_COUNT_LIMIT_DAY = "phone:sms:code:send:count:limit:day:%s";

    public static final String PHONE_SMS_CODE_SEND_COUNT_LIMIT_HOUR = "phone:sms:code:send:count:limit:hour:%s";

    public static final String PHONE_SMS_CODE_SEND_COUNT_LIMIT_MINUTE = "phone:sms:code:send:count:limit:minute:%s";

    public static final String PHONE_SMS_CODE_SEND_COUNT_LIMIT_IP = "phone:sms:code:send:count:limit:ip:%s";

    public static final String ONE_TO_ONE_VIDEO_USER_MATCH_LIST = "one:to:one:video:user:match:list:%s";

    public static final String ONE_TO_ONE_VIDEO_USER_STATUS = "one:to:one:video:user:status:%s:%s";

    public static final String ONE_TO_ONE_VIDEO_USER_MATCH_LOCK = "one:to:one:video:user:match:lock:%s:%s";

    public static final String ONE_TO_ONE_VIDEO_RANDOM_MATCH_USER_LOCK = "one:to:one:video:random:match:user:lock:%s";


    private RedisKeyConstants() {}
}
