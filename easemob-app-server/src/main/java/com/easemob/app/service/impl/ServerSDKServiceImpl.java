package com.easemob.app.service.impl;

import com.easemob.app.exception.ASGetChatUserIdException;
import com.easemob.app.exception.ASGetChatUserNameException;
import com.easemob.app.exception.ASRegisterChatUserNameException;
import com.easemob.app.service.ServerSDKService;
import com.easemob.im.server.EMException;
import com.easemob.im.server.EMService;
import com.easemob.im.server.api.token.agora.AccessToken2;
import com.easemob.im.server.exception.EMNotFoundException;
import com.easemob.im.server.model.EMUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ServerSDKServiceImpl implements ServerSDKService {
    private static final String EASEMOB_USER_PASSWORD = "123456";

    @Value("${agora.token.expire.period.seconds}")
    private int expirePeriod;

    @Autowired
    private EMService serverSdk;

    @Override
    public void registerChatUserName(String chatUserName) {
        try {
            this.serverSdk.user().create(chatUserName, EASEMOB_USER_PASSWORD).block();
            log.info("register chatUserName success :{}", chatUserName);
        } catch (EMException e) {
            log.error("register chatUserName fail. chatUserName : {}, e : {}", chatUserName, e.getMessage());
            throw new ASRegisterChatUserNameException(chatUserName + " register fail.");
        }
    }

    @Override
    public boolean checkIfChatUserNameExists(String chatUserName) {
        try {
            this.serverSdk.user().get(chatUserName).block();
        } catch (EMException e) {
            if (e.getClass() == EMNotFoundException.class) {
                log.info("chatUserName not exists :{}", chatUserName);
                return false;
            }
            log.error("get chatUserName fail. chatUserName : {}, e : {}", chatUserName, e.getMessage());
            throw new ASGetChatUserNameException(chatUserName + " get fail.");
        }
        return true;
    }

    @Override
    public String getChatUserId(String chatUserName) {
        EMUser user;

        try {
            user = this.serverSdk.user().get(chatUserName).block();
        } catch (EMException e) {
            log.error("get chatUserId fail. chatUserName : {}, e : {}", chatUserName, e.getMessage());
            throw new ASGetChatUserIdException(chatUserName + " get chatUserId fail.");
        }
        return user.getUuid();
    }

    @Override
    public String generateAgoraChatUserToken(String chatUserName, String chatUserId) {
        EMUser user = new EMUser(chatUserName, chatUserId, true);

        return this.serverSdk.token().getUserToken(user, this.expirePeriod, null, EASEMOB_USER_PASSWORD);
    }

    @Override
    public String generateAgoraRtcToken(String channelName, Integer agorauid) {
        EMUser bob = new EMUser("bob", "da921111-ecf9-11eb-9af3-296ff79acb67", true);

        return this.serverSdk.token().getUserToken(bob, this.expirePeriod, token -> {
            AccessToken2.ServiceRtc serviceRtc = new AccessToken2.ServiceRtc(channelName, String.valueOf(agorauid));
            serviceRtc.addPrivilegeRtc(AccessToken2.PrivilegeRtc.PRIVILEGE_JOIN_CHANNEL, this.expirePeriod);
            token.addService(serviceRtc);
        }, null);
    }
}
