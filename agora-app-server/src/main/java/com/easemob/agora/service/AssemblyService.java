package com.easemob.agora.service;

import com.easemob.agora.model.AppUserInfo;

public interface AssemblyService {
    /**
     * 为用户注册账号
     * @param userAccount 用户的账号
     * @param userPassword 用户的密码
     */
    void registerUserAccount(String userAccount, String userPassword);

    /**
     * 随机生成声网用户id，保证唯一
     * @return agoraUid
     */
    String generateUniqueAgoraUid();

    /**
     * 根据用户账号从DB获取对象
     * @param userAccount 用户的账号
     * @return AppUserInfo
     */
    AppUserInfo getAppUserInfoFromDB(String userAccount);

    /**
     * 检查用户账号是否存在于DB
     * @param userAccount 用户的账号
     * @return boolean
     */
    boolean checkIfUserAccountExistsDB(String userAccount);

    /**
     * 检查声网用户id是否存在于DB
     * @param agoraUid 声网uid
     * @return boolean
     */
    boolean checkIfAgoraUidExistsDB(String agoraUid);

    /**
     * 将用户信息存入DB
     * @param userAccount 用户的账号
     * @param userPassword 用户的密码
     * @param easemobUserName 环信用户名
     * @param easemobUserId 环信用户id
     * @param agoraUid 声网uid
     */
    void saveAppUserToDB(String userAccount, String userPassword, String easemobUserName, String easemobUserId, String agoraUid);


    AppUserInfo checkAppUserInfo(AppUserInfo appUserInfo);
}
