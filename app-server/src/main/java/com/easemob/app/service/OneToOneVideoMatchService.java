package com.easemob.app.service;

import com.easemob.app.model.OneToOneVideoMatchInfo;

public interface OneToOneVideoMatchService {

    /**
     * 匹配用户
     *
     * @param appkey appkey
     * @param phoneNumber phoneNumber
     * @param sendCancelMatchNotify sendCancelMatchNotify
     * @param token token
     * @return OneToOneVideoMatchInfo
     */
    OneToOneVideoMatchInfo matchUser(String appkey, String phoneNumber, boolean sendCancelMatchNotify, String token);

    /**
     * 取消匹配用户
     *
     * @param appkey appkey
     * @param phoneNumber phoneNumber
     * @param token token
     */
    void unMatchUser(String appkey, String phoneNumber, String token);

    /**
     * 获取用户匹配状态
     *
     * @param appkey appkey
     * @param chatUsername chatUsername
     * @return
     */
    String getUserMatchStatus(String appkey, String chatUsername);

}
