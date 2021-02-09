package com.easemob.agora.config.auth.token;

import com.easemob.agora.utils.RestManger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;

@Slf4j
@Component
public class TokenAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private RestManger restManger;

    @Override
    public Authentication authenticate(Authentication authentication) {
        //1、 获取用户名和令牌
        String username = authentication.getName();
        String token = authentication.getCredentials().toString();

        //2、对 username 和令牌进行校验
        String name = restManger.getUser(username, token);
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        //3、返回经过认证的Authentication
        AuthenticationToken result =
                new AuthenticationToken(username, authentication.getCredentials(),
                        Collections.singletonList(() -> "user"));
        result.setDetails(authentication.getDetails());
        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return AuthenticationToken.class.isAssignableFrom(authentication);
    }
}
