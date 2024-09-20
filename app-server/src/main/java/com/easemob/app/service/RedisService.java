package com.easemob.app.service;

import java.util.List;
import java.util.Set;

public interface RedisService {
    /**
     * 保存声网频道信息
     * @param isRandomUid 是否为随机生成的uid
     * @param channelName 频道名称
     * @param uid uid是纯数字的声网用户id，即请求中携带的agoraUserId，
     *            如果agoraUserId值为0或为null，由服务端随机生成用于申请声网token
     */
    void saveAgoraChannelInfo(boolean isRandomUid, String channelName, String uid);

    /**
     * 保存uid与环信id的映射
     * @param uid 声网用户id
     * @param easemobUserId 环信id
     */
    void saveUidMapper(String uid, String easemobUserId);

    /**
     * 获取声网频道信息
     * @param channelName 频道名称
     * @return uid列表
     */
    Set<String> getAgoraChannelInfo(String channelName);

    /**
     * 获取存uid与环信id的映射
     * @param uid 声网用户id
     * @return 环信id
     */
    String getUidMapper(String uid);

    /**
     * 保存手机短信码
     *
     * @param phoneNumber 手机号
     * @param smsCode 短信码
     * @param resourceIp 请求来源IP
     */
    void saveSmsCode(String phoneNumber, String smsCode, String resourceIp);

    /**
     * 获取手机短信码列表
     *
     * @param phoneNumber 手机号
     * @return 短信码
     */
    List<Object> getSmsCodeRecord(String phoneNumber);

    /**
     * 检查短信验证码次数限制
     *
     * @param phoneNumber phoneNumber
     */
    void checkSmsCodeLimit(String phoneNumber, String resourceIp);

    /**
     * 检查uid是否存在
     * @param agoraUid agoraUid
     * @return Boolean
     */
    Boolean checkIfUidExists(String agoraUid);

    /**
     * 保存uid
     * @param agoraUid agoraUid
     */
    void saveUid(String agoraUid);

    /**
     * 设置用户状态，包括用户在线状态，匹配状态，匹配到的用户
     *
     * @param appkey appkey
     * @param chatUsername chatUsername
     * @param hashKey hashKey
     * @param value value
     */
    void setUserStatus(String appkey, String chatUsername, String hashKey, String value);

    /**
     * 获取用户状态
     *
     * @param appkey appkey
     * @param chatUsername chatUsername
     * @param hashKey hashKey
     * @return status
     */
    String getUserStatus(String appkey, String chatUsername, String hashKey);

    /**
     * 随机匹配用户
     *
     * @param appkey appkey
     * @param matchUser matchUser
     * @return user
     */
    String randomMatchUser(String appkey, String matchUser);

    /**
     * 获取匹配用户列表数量
     *
     * @param appkey appkey
     * @return count
     */
    Long getMatchListCount(String appkey);

    /**
     * 将用户添加到匹配列表
     *
     * @param appkey appkey
     * @param chatUsername chatUsername
     */
    void addUserToMatchList(String appkey, String chatUsername);

    /**
     * 从匹配列表中移除用户
     *
     * @param appkey appkey
     * @param chatUsername chatUsername
     */
    void removeUserToMatchList(String appkey, String chatUsername);

    /**
     * 用户匹配加锁
     *
     * @param appkey appkey
     * @param chatUsername chatUsername
     * @return true/false
     */
    Boolean matchLock(String appkey, String chatUsername);

    /**
     * 用户匹配解锁
     *
     * @param appkey appkey
     * @param chatUsername chatUsername
     */
    void matchUnLock(String appkey, String chatUsername);

    /**
     * 随机匹配用户加锁
     *
     * @param appkey appkey
     * @return true/false
     */
    Boolean randomMatchUserLock(String appkey);

    /**
     * 随机匹配用户解锁
     *
     * @param appkey appkey
     */
    void randomMatchUserUnLock(String appkey);

}
