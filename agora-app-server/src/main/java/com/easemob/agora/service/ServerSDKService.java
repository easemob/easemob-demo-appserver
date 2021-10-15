package com.easemob.agora.service;

public interface ServerSDKService {
    /**
     * 为用户注册 chat 用户名
     * @param chatUserName chat用户名
     */
    void registerChatUserName(String chatUserName);

    /**
     * 检查 chat 用户名是否存在
     * @param chatUserName chat用户名
     * @return boolean
     */
    boolean checkIfChatUserNameExists(String chatUserName);

    /**
     * 获取 chat 用户id
     * @param chatUserName chat用户名
     * @return uuid
     */
    String getChatUserId(String chatUserName);

    /**
     * 生成声网chatUserToken
     * @param chatUserName chat用户名
     * @param chatUserId chat用户id
     * @return
     */
    String generateAgoraChatUserToken(String chatUserName, String chatUserId);

    /**
     * 生成声网rtcToken
     * @param channelName 频道名称
     * @param agorauid 声网uid
     * @return
     */
    String generateAgoraRtcToken(String channelName, Integer agorauid);
}
