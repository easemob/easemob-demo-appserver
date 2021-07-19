package com.easemob.agora.service.impl;

import com.easemob.agora.exception.ASGetEasemobUserIdException;
import com.easemob.agora.exception.ASGetEasemobUserNameException;
import com.easemob.agora.exception.ASRegisterEasemobUserNameException;
import com.easemob.agora.service.ServerSDKService;
import com.easemob.im.server.EMException;
import com.easemob.im.server.EMService;
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
    public void registerEasemobUserName(String easemobUserName) {
        try {
            this.serverSdk.user().create(easemobUserName, EASEMOB_USER_PASSWORD).block();
            log.info("register easemobUserName success :{}", easemobUserName);
        } catch (EMException e) {
            throw new ASRegisterEasemobUserNameException(String.format("register easemobUserName %s fail. Message : %s", easemobUserName ,e.getMessage()));
        }
    }

    @Override
    public boolean checkIfEasemobUserNameExists(String easemobUserName) {
        try {
            this.serverSdk.user().get(easemobUserName).block();
        } catch (EMException e) {
            if (e.getClass() == EMNotFoundException.class) {
                log.info("easemobUserName not exists :{}", easemobUserName);
                return false;
            }
            throw new ASGetEasemobUserNameException(String.format("get easemobUserName %s fail. Message : %s", easemobUserName ,e.getMessage()));
        }
        return true;
    }

    @Override
    public String getEasemobUserId(String easemobUserName) {
        EMUser user;
        try {
            user = this.serverSdk.user().get(easemobUserName).block();
        } catch (EMException e) {
            throw new ASGetEasemobUserIdException(String.format("get easemobUserId %s fail. Message : %s", easemobUserName ,e.getMessage()));
        }
        return user.getUuid();
    }

    @Override
    public String generateAgoraChatUserToken(String easemobUserName, String easemobUserId) {
        EMUser user = new EMUser(easemobUserName, easemobUserId, true);
        return this.serverSdk.token().getUserToken(user, this.expirePeriod, null, null);
    }
}
