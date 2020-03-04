package com.easemob.app.service;

public interface ChatGptService {

    /**
     * 向 chat gpt 发送单聊消息
     *
     * @param appKey         appKey
     * @param username       用户名
     * @param messageContent 消息内容
     * @return chat gpt 回复的消息
     */
    String sendChatMessage(String appKey, String username, String messageContent);

    /**
     * 向 chat gpt 发送群组消息
     *
     * @param appKey         appKey
     * @param username       用户名
     * @param groupId        群组 id
     * @param messageContent 消息内容
     * @return chat gpt 回复的消息
     */
    String sendChatGroupMessage(String appKey, String username, String groupId,
            String messageContent);

}
