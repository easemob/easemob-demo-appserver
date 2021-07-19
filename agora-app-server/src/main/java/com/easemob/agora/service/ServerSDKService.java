package com.easemob.agora.service;

public interface ServerSDKService {
    /**
     * 为用户注册环信用户名
     * @param easemobUserName 环信用户名
     */
    void registerEasemobUserName(String easemobUserName);

    /**
     * 检查环信用户名是否存在
     * @param easemobUserName 环信用户名
     * @return boolean
     */
    boolean checkIfEasemobUserNameExists(String easemobUserName);

    /**
     * 获取环信用户id
     * @param easemobUserName 环信用户名
     * @return uuid
     */
    String getEasemobUserId(String easemobUserName);

    /**
     * 生成声网chatUserToken
     * @param easemobUserName 环信用户名
     * @param easemobUserId 环信用户id
     * @return
     */
    String generateAgoraChatUserToken(String easemobUserName, String easemobUserId);
}
