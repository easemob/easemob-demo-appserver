package com.easemob.app.service;

import com.alibaba.fastjson.JSONObject;

public interface CallBackService {

    /**
     * 接收回调消息
     *
     * @param appKey          appKey
     * @param from            消息的发送方
     * @param to              消息接收方
     * @param chatType        消息类型
     * @param groupId         群组id
     * @param callBackPayload 回调的消息内容
     */
    void receiveCallBackMessage(String appKey, String from, String to, String chatType,
            String groupId, JSONObject callBackPayload);

}
