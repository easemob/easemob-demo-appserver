package com.easemob.app.service;

import com.easemob.app.model.AppUserInfoNew;

public interface AssemblyService {

    /**
     * 随机生成声网用户id，保证唯一
     *
     * @param appKey appKey
     * @return agoraUid
     */
    String generateUniqueAgoraUid(String appKey);

    /**
     * 随机生成环信用户名，保证唯一
     *
     * @param appKey appKey
     * @return chatUsername
     */
    String generateUniqueChatUsername(String appKey);

    /**
     * 根据appKey、手机号从DB获取对象
     *
     * @param appKey      appKey
     * @param phoneNumber 手机号
     * @return AppUserInfoNew
     */
    AppUserInfoNew getAppUserInfoNewFromDB(String appKey, String phoneNumber);

    /**
     * 根据appKey、chat用户名从DB获取对象
     *
     * @param appKey       appKey
     * @param chatUserName chatUserName
     * @return AppUserInfoNew
     */
    AppUserInfoNew getAppUserInfoNewByChatUserName(String appKey, String chatUserName);

    boolean checkIfChatUsernameExistsDB(String appKey, String chatUsername);

    /**
     * 检查声网用户id是否存在于DB
     *
     * @param appKey   appKey
     * @param agoraUid 声网uid
     * @return boolean
     */
    boolean checkIfAgoraUidExistsDB(String appKey, String agoraUid);

    /**
     * 将用户信息存入DB
     *
     * @param appKey           appKey
     * @param phoneNumber      用户的手机号
     * @param chatUsername     环信用户名
     * @param chatUserPassword 环信用户密码
     * @param agoraUid         声网uid
     */
    void saveAppUserNewToDB(String appKey, String phoneNumber, String chatUsername,
            String chatUserPassword, String agoraUid);

    /**
     * 将用户db信息更新
     * @param appUserInfo 头像url
     */
    void updateAppUserInfoToDB(AppUserInfoNew appUserInfo);

}
