package com.easemob.agora.config.auth.username;

import com.easemob.agora.model.UserAuth;
import com.easemob.agora.utils.RestManger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;

@Slf4j
@Component
public class UsernameAuthenticationProvider implements AuthenticationProvider {

    private final RestManger restManger;

    public UsernameAuthenticationProvider(RestManger restManger) {
        this.restManger = restManger;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        //1、获取用户名和密码
        String username = authentication.getName();
        UserAuth userAuth = (UserAuth) authentication.getCredentials();

        //2、 验证用户名密码是否正确
        String token = restManger.getToken(username, userAuth.getToken(), userAuth.getOrgName(),
                userAuth.getAppName());
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        //3、返回验证好的authentication
        UsernameAuthenticationToken result;
        result = new UsernameAuthenticationToken(
                username, authentication.getCredentials(), Collections.singletonList(
                () -> "user"));
        result.setDetails(authentication.getDetails());
        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernameAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
