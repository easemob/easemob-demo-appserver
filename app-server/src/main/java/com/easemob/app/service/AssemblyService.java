package com.easemob.app.service;

import com.easemob.app.model.AppUserOneToOneVideoInfo;

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
     * 根据appKey、手机号从1v1视频通话DB获取对象
     *
     * @param appKey      appKey
     * @param phoneNumber 手机号
     * @return AppUserOneToOneVideoInfo
     */
    AppUserOneToOneVideoInfo getAppUserOneToOneVideoInfoFromDB(String appKey, String phoneNumber);

    /**
     * 根据appKey、用户名从1v1视频通话DB获取对象
     *
     * @param appKey      appKey
     * @param chatUsername chatUsername
     * @return AppUserOneToOneVideoInfo
     */
    AppUserOneToOneVideoInfo getAppUserOneToOneVideoInfoByChatUsername(String appKey, String chatUsername);

    /**
     * 将用户信息存入1v1视频通话DB
     *
     * @param appKey       appKey
     * @param phoneNumber  phoneNumber
     * @param chatUsername 环信用户名
     * @param agoraUid     agoraUid
     * @param avatarUrl    avatarUrl
     */
    void saveAppUserToOneToOneVideoToDB(String appKey, String phoneNumber, String chatUsername,
            String agoraUid, String avatarUrl);

    /**
     * 更新1v1视频通话用户DB信息
     *
     * @param appUserOneToOneVideoInfo appUserOneToOneVideoInfo
     */
    void updateAppUserToOneToOneVideoToDB(AppUserOneToOneVideoInfo appUserOneToOneVideoInfo);

}
