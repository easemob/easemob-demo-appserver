package com.easemob.app.api;

import com.alibaba.fastjson.JSONObject;
import com.easemob.app.service.CallBackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class CallBackController {

    private static final String APPKEY = "appkey";
    private static final String FROM = "from";
    private static final String TO = "to";
    private static final String CHAT_TYPE = "chat_type";
    private static final String GROUP_ID = "group_id";
    private static final String PAYLOAD = "payload";

    private CallBackService callBackService;

    public CallBackController(CallBackService callBackService) {
        this.callBackService = callBackService;
    }

    /**
     * 开通发送后消息回调时，以该 API 作为回调地址，那么通过 IM 发送的消息就会通过该 API 回调给您。
     *
     * @param callbackMessage 接收到的回调消息
     * @return ResponseEntity
     */
    @PostMapping("/app/chat/chat-gpt/interact/callback")
    public ResponseEntity receiveCallBackMessage(@RequestBody JSONObject callbackMessage) {

        if (callbackMessage == null) {
            log.error("receive callback message error | callback message is null.");
            throw new IllegalArgumentException("callback message is null.");
        }

        String appKey = checkCallbackMessageParameter(callbackMessage.get(APPKEY));
        String from = checkCallbackMessageParameter(callbackMessage.get(FROM));
        String to = checkCallbackMessageParameter(callbackMessage.get(TO));
        String chatType = checkCallbackMessageParameter(callbackMessage.get(CHAT_TYPE));
        String groupId = checkCallbackMessageParameter(callbackMessage.get(GROUP_ID));

        JSONObject payload = callbackMessage.getJSONObject(PAYLOAD);
        if (payload == null) {
            log.error("receive callback message error | payload is null. callbackMessage : {}", callbackMessage);
            throw new IllegalArgumentException("payload is null");
        }

        callBackService.receiveCallBackMessage(appKey, from, to, chatType, groupId, payload);

        return ResponseEntity.ok().build();
    }

    private String checkCallbackMessageParameter(Object parameter) {
        if (parameter == null) {
            log.error("receive callback message error | parameter is null.");
            throw new IllegalArgumentException("parameter is null");
        } else {
            return parameter.toString();
        }
    }
}
