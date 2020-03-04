package com.easemob.app.service;

import java.util.Map;

public interface RestService {
    /**
     * 为用户注册 chat 用户名
     *
     * @param appkey       appkey
     * @param chatUserName chat用户名
     */
    void registerChatUserName(String appkey, String chatUserName);

    /**
     * 检查 chat 用户名是否存在
     *
     * @param appkey       appkey
     * @param chatUserName chat用户名
     * @return boolean
     */
    boolean checkIfChatUserNameExists(String appkey, String chatUserName);

    /**
     * 添加好友
     *
     * @param appkey       appkey
     * @param chatUserName chatUserName
     * @param contactName  contactName
     */
    void addContact(String appkey, String chatUserName, String contactName);

    /**
     * 创建群组
     *
     * @param appkey       appkey
     * @param chatUserName chatUserName
     * @return chatGroupId
     */
    String createChatGroup(String appkey, String chatUserName);

    /**
     * 获取 chat 用户的 uuid
     *
     * @param appkey       appkey
     * @param chatUserName chatUserName
     * @return uuid
     */
    String getChatUserUuid(String appkey, String chatUserName);

    /**
     * 向用户发送消息
     *
     * @param appkey         appkey
     * @param from           from
     * @param to             to
     * @param messageContent messageContent
     */
    void sendTextMessageToUser(String appkey, String from, String to, String messageContent,
            Map<String, Object> ext);

    /**
     * 向群组发送消息
     *
     * @param appkey         appkey
     * @param from           from
     * @param groupId        groupId
     * @param messageContent messageContent
     */
    void sendTextMessageToGroup(String appkey, String from, String groupId, String messageContent,
            Map<String, Object> ext);

    /**
     * 向群组指定用户发送消息
     *
     * @param appkey         appkey
     * @param from           from
     * @param groupId        groupId
     * @param groupMember    groupMember
     * @param messageContent messageContent
     */
    void sendTextMessageToGroupMember(String appkey, String from, String groupId,
            String groupMember, String messageContent, Map<String, Object> ext);

}
