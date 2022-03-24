package com.easemob.agora.service;

import java.util.Set;

public interface RedisService {
    /**
     * 保存声网频道信息
     * @param isRandomUid 是否为随机生成的uid
     * @param channelName 频道名称
     * @param uid uid是纯数字的声网用户id，即请求中携带的agoraUserId，
     *            如果agoraUserId值为0或为null，由服务端随机生成用于申请声网token
     */
    void saveAgoraChannelInfo(boolean isRandomUid, String channelName, String uid);

    /**
     * 保存uid与环信id的映射
     * @param uid 声网用户id
     * @param easemobUserId 环信id
     */
    void saveUidMapper(String uid, String easemobUserId);

    /**
     * 获取声网频道信息
     * @param channelName 频道名称
     * @return uid列表
     */
    Set<String> getAgoraChannelInfo(String channelName);

    /**
     * 获取存uid与环信id的映射
     * @param uid 声网用户id
     * @return 环信id
     */
    String getUidMapper(String uid);
}
