package com.easemob.agora.limit;

import com.easemob.agora.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppUserLimitService {

    @Autowired
    private RedisService redisService;

    public Long getTokenReachedLimit(String userAccount) {
        return this.redisService.appUserGetCountOfToken(userAccount);
    }

}
