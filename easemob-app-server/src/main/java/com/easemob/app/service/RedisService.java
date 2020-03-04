package com.easemob.app.service;

import com.easemob.app.model.ChatGptMessage;

import java.util.List;
import java.util.Set;

public interface RedisService {
    /**
     * 保存声网频道信息
     *
     * @param isRandomUid 是否为随机生成的 uid
     * @param channelName 频道名称
     * @param uid         uid 是纯数字的声网用户 id，即请求中携带的 agoraUserId，
     *                    如果 agoraUserId 值为 0 或为null，由服务端随机生成用于申请 agora token
     */
    void saveAgoraChannelInfo(boolean isRandomUid, String channelName, String uid);

    /**
     * 保存uid与环信id的映射
     *
     * @param uid           声网用户id
     * @param easemobUserId 环信id
     */
    void saveUidMapper(String uid, String easemobUserId);

    /**
     * 获取声网频道信息
     *
     * @param channelName 频道名称
     * @return uid列表
     */
    Set<String> getAgoraChannelInfo(String channelName);

    /**
     * 获取存uid与环信id的映射
     *
     * @param uid 声网用户id
     * @return 环信id
     */
    String getUidMapper(String uid);

    /**
     * 自减发送消息到 chatGpt 的次数
     *
     * @param appKey   appKey
     * @param username username
     */
    void decrNumberOfSendMessageToChatGpt(String appKey, String username);

    /**
     * 检查是否达到发送消息到 chatGpt 的次数限制
     *
     * @param appKey   appKey
     * @param username username
     * @return boolean
     */
    boolean checkIfSendMessageToChatGptLimit(String appKey, String username);

    /**
     * 添加消息到 redis，用于存储群组消息上下文
     *
     * @param appkey  appkey
     * @param groupId groupId
     * @param message 消息内容
     */
    void addGroupMessageToRedis(String appkey, String groupId, ChatGptMessage message);

    /**
     * 获取群组消息上下文
     *
     * @param appkey  appkey
     * @param groupId groupId
     * @return List
     */
    List<String> getGroupContextMessages(String appkey, String groupId);

    /**
     * 获取群组消息上下文的数量
     *
     * @param appkey  appkey
     * @param groupId groupId
     * @return String
     */
    Long getGroupContextMessagesCount(String appkey, String groupId);
}
