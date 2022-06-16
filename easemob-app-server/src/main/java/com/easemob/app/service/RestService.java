package com.easemob.app.service;

public interface RestService {
    /**
     * 为用户注册 chat 用户名
     * @param appkey appkey
     * @param chatUserName chat用户名
     */
    void registerChatUserName(String appkey, String chatUserName);

    /**
     * 检查 chat 用户名是否存在
     * @param appkey appkey
     * @param chatUserName chat用户名
     * @return boolean
     */
    boolean checkIfChatUserNameExists(String appkey, String chatUserName);

    /**
     * 获取 chat 用户的 uuid
     * @param appkey appkey
     * @param chatUserName chatUserName
     * @return uuid
     */
    String getChatUserUuid(String appkey, String chatUserName);

}
