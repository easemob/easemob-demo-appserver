package com.easemob.app.service;

import com.easemob.app.model.ChatGroupListResponse;
import reactor.util.function.Tuple2;

import java.io.BufferedInputStream;
import java.io.File;
import java.util.List;

public interface RestService {
    /**
     * 为用户注册 chat 用户名
     * @param appkey appkey
     * @param chatUserName chat用户名
     * @param chatUserPassword chat用户密码
     */
    void registerChatUserName(String appkey, String chatUserName, String chatUserPassword);

    /**
     * 获取 chat 用户的 token
     * @param appkey appkey
     * @param chatUserName chatUserName
     * @param chatUserPassword chatUserPassword
     * @return uuid
     */
    String getChatUserToken(String appkey, String chatUserName, String chatUserPassword);

    /**
     * 添加好友
     *
     * @param appkey appkey
     * @param chatUserName chatUserName
     */
    void addContact(String appkey, String chatUserName);

    /**
     * 创建群组
     *
     * @param appkey appkey
     * @param chatUserName chatUserName
     * @return chatGroupId
     */
    String createChatGroup(String appkey, String chatUserName);

    /**
     * 发送单聊消息
     *
     * @param appkey appkey
     * @param chatUserName chatUserName
     * @param messageContent messageContent
     */
    void sendMessageToUser(String appkey, String chatUserName, String messageContent);

    void sendMessageToUser(String appkey, String from, String chatUserName, String messageContent);

    /**
     * 发送群聊消息
     *
     * @param appkey appkey
     * @param chatGroupId chatGroupId
     * @param messageContent messageContent
     */
    void sendMessageToChatGroup(String appkey, String chatGroupId, String messageContent);

    ChatGroupListResponse getUsers(String appkey, String cursor);

    /**
     * 获取群组 custom
     *
     * @param appkey appkey
     * @param chatGroupId chatGroupId
     * @return ChatGroup
     */
    String getChatGroupCustom(String appkey, String chatGroupId);

    /**
     * 更新群组自定义属性
     *
     * @param appkey appkey
     * @param chatGroupId chatGroupId
     * @param custom custom
     */
    void updateGroupCustom(String appkey, String chatGroupId, String custom);

    /**
     * 删除群组
     *
     * @param appkey appkey
     * @param chatGroupId chatGroupId
     */
    void deleteChatGroup(String appkey, String chatGroupId);

    /**
     * 获取指定appkey的clientId和clientSecret
     * @param appkey
     * @return
     */
    Tuple2<String, String> getAppSecret(String appkey);

    /**
     * 获取群组成员
     *
     * @param appkey appkey
     * @param chatGroupId chatGroupId
     * @return List<String>
     */
    List<String> getChatGroupMembers(String appkey, String chatGroupId);

    /**
     * 上传文件(群组、用户头像)
     *
     * @param appkey
     * @param id
     * @param file
     * @return
     */
     String uploadFile(String appkey, String id, File file);

    /**
     * 下载文件(用户头像)
     *
     * @param appkey
     * @param urlPath
     * @return
     */
     BufferedInputStream downloadThumbImage(String appkey, String urlPath);
}
