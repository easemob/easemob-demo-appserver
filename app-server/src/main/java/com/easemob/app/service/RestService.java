package com.easemob.app.service;

import com.easemob.app.model.AppUserPresenceStatus;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.util.List;
import java.util.Map;

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
     * @return token
     */
    String getChatUserToken(String appkey, String chatUserName);

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
     * 获取用户presence在线状态
     *
     * @param appkey appkey
     * @param chatUserNames chatUserNames
     * @return list
     */
    List<AppUserPresenceStatus> getUserPresenceStatus(String appkey, List<String> chatUserNames);

    /**
     * 设置用户属性
     *
     * @param appkey       appkey
     * @param chatUserName chatUserName
     * @param metadata    metadata
     */
    void setUserMetadata(String appkey, String chatUserName, MultiValueMap<String, Object> metadata);

    /**
     * 检查 chat 用户的 token 权限
     * @param appkey appkey
     * @param chatUserName chatUserName
     * @param token token
     * @return boolean
     */
    boolean checkUserTokenPermissions(String appkey, String chatUserName, String token);

    /**
     * 发送单聊cmd消息
     *
     * @param appkey appkey
     * @param from from
     * @param chatUserName chatUserName
     * @param action action
     * @param isRouteOnline isRouteOnline
     * @param ext ext
     */
    void sendCmdMessageToUser(String appkey, String from, String chatUserName, String action, boolean isRouteOnline, Map<String, Object> ext);



}
