package com.easemob.agora.utils;

import com.alibaba.fastjson.JSONObject;
import com.easemob.agora.config.ApplicationConfig;
import com.easemob.agora.exception.ASRegisterChatUserNameException;
import com.easemob.agora.exception.ASResourceLimitedException;
import com.easemob.im.server.EMService;
import com.easemob.im.server.api.token.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author skyfour
 */
@Slf4j
@Component
public class RestManger {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ApplicationConfig applicationConfig;

    @Autowired
    private EMService serverSdk;

    private static final String REGISTER_USER = "%s/%s/%s/users";

    public void registerChatUser(String chatUsername, String chatUserPassword) {
        if (StringUtils.isEmpty(chatUsername) || StringUtils.isEmpty(chatUserPassword)) {
            throw new IllegalArgumentException("chatUsername or chatUserPassword cannot be empty!");
        }

        String appkey = applicationConfig.getAppkey();
        String[] splitAppkey = appkey.split("#");

        String url = String.format(REGISTER_USER, applicationConfig.getBaseUri(),
                splitAppkey[0], splitAppkey[1]);

        Token token = this.serverSdk.getContext().getTokenProvider().fetchAppToken().block();
        Map<String, String> body = new HashMap<>();
        body.put("username", chatUsername);
        body.put("password", chatUserPassword);

        ResponseEntity<Map> responseEntity;
        try {
            responseEntity = restTemplate
                    .exchange(url, HttpMethod.POST, getHeaderAndBody(body, token.getValue()), Map.class);
        }catch (HttpClientErrorException ex) {
            JSONObject jsonObject = JSONObject.parseObject(ex.getResponseBodyAsString());
            String result = null;
            if (jsonObject != null) {
                if (jsonObject.get("error_description") != null) {
                    result = jsonObject.getString("error_description");
                }
            } else {
                throw new ASRegisterChatUserNameException(ex.getMessage());
            }

            if (ex.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                throw new InvalidParameterException(result);
            } else if (ex.getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS)) {
                throw new ASResourceLimitedException(result);
            } else {
                throw new ASRegisterChatUserNameException(ex.getMessage());
            }
        } catch (Exception e) {
            log.error("register user failed. appkey : {}, chatUsername : {}", appkey, chatUsername, e);
            throw new ASRegisterChatUserNameException(e.getMessage());
        }

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            log.error("register user | failed connection to im-rest2-server, statusCode : {}",
                    responseEntity.getStatusCode());
            throw new ASRegisterChatUserNameException("statusCode : " + responseEntity.getStatusCode());
        }
    }

    private HttpEntity<Object> getHeaderAndBody(Object body, String token) {

        HttpHeaders requestHeaders = new HttpHeaders();
        if (!StringUtils.isEmpty(token)) {
            requestHeaders.add("Authorization", "Bearer " + token);
        }
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, requestHeaders);
    }
}
