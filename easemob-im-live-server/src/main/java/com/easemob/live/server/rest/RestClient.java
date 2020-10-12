package com.easemob.live.server.rest;

import com.easemob.live.server.liveroom.api.LiveRoomInfo;
import com.easemob.live.server.rest.chatroom.*;
import com.easemob.live.server.rest.token.GetTokenRequest;
import com.easemob.live.server.rest.token.GetTokenResponse;
import com.easemob.live.server.rest.user.GetUserStatusResponse;
import com.easemob.live.server.rest.user.UserStatus;
import com.easemob.live.server.utils.HttpTemplate;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author shenchong@easemob.com 2020/2/19
 */
@Slf4j
@Service
public class RestClient {

    private RestTemplate restTemplate;

    private final Cache<String, String> tokenCache;

    private final String orgName;
    private final String appName;
    private final String clientId;
    private final String clientSecret;
    private final String basePath;

    public RestClient(RestProperties properties) {

        this.orgName = properties.getAppkey().getOrgName();
        this.appName = properties.getAppkey().getAppName();
        this.clientId = properties.getAppkey().getClientId();
        this.clientSecret = properties.getAppkey().getClientSecret();

        // 缓存token
        this.tokenCache = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.DAYS).build();

        // 构建basePath, baseUrl/{orgName}/{appName}/
        this.basePath = String.format("%s/%s/%s/",
                properties.getBaseUrl(),
                properties.getAppkey().getOrgName(),
                properties.getAppkey().getAppName()
        );

        // 初始化RestTemplate
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(properties.getConnectTimeout());
        requestFactory.setReadTimeout(properties.getReadTimeout());
        this.restTemplate = new RestTemplate(requestFactory);
    }

    /**
     * @return app admin token
     */
    public String retrieveAppToken() {

        String key = orgName + "#" + appName;
        String cachedToken = tokenCache.getIfPresent(key);
        if (cachedToken != null) {
            return cachedToken;
        }

        GetTokenRequest request = GetTokenRequest.builder()
                .grantType(GetTokenRequest.GrantType.CLIENT_CREDENTIALS)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();

        String url = basePath + "token";

        GetTokenResponse tokenResponse = HttpTemplate
                .execute(restTemplate, url, HttpMethod.POST, request, GetTokenResponse.class);

        tokenCache.put(key, tokenResponse.getAccessToken());

        return tokenResponse.getAccessToken();
    }

    /**
     * 创建聊天室
     * @param token app 管理员 token
     * @return String 聊天室ID
     */
    public String createChatroom(CreateChatroomRequest request, String token) {

        String url = basePath + "chatrooms";

        return HttpTemplate
                .execute(restTemplate, url, HttpMethod.POST, token, request,
                        CreateChatroomResponse.class)
                .getData()
                .getId();
    }

    /**
     * 获取聊天室详情
     * @param chatroomId 聊天室ID
     * @param token 用户及以上身份token
     * @return LiveRoomInfo
     */
    public LiveRoomInfo retrieveChatroomInfo(String chatroomId, String token) {

        String url = basePath + "chatrooms/" + chatroomId;

        return HttpTemplate
                .execute(restTemplate, url, HttpMethod.GET, token, GetChatroomResponse.class)
                .getData()
                .get(0);
    }

    /**
     * 转让聊天室
     * @param chatroomId 聊天室ID
     * @return boolean 是否转让成功
     */
    public Boolean assignChatroomOwner(String chatroomId, String newowner, String token) {

        AssignChatroomOwnerRequest request = AssignChatroomOwnerRequest.builder()
                .newowner(newowner)
                .build();

        String url = basePath + "chatrooms/" + chatroomId;

        return HttpTemplate
                .execute(restTemplate, url, HttpMethod.PUT, token, request,
                        AssignChatroomOwnerResponse.class)
                .getData()
                .getNewowner();
    }

    /**
     * 修改聊天室详情
     * @param chatroomId 聊天室ID
     * @return boolean 是否转让成功
     */
    public void modifyChatroom(String chatroomId, ModifyChatroomRequest request, String token) {

        String url = basePath + "chatrooms/" + chatroomId;

        HttpTemplate.execute(restTemplate, url, HttpMethod.PUT, token, request, RestResponse.class);
    }

    /**
     * 删除聊天室
     * @param chatroomId 聊天室ID
     * @param token owner user token or app admin token
     * @return boolean 是否删除成功
     */
    public Boolean deleteChatroom(String chatroomId, String token) {

        String url = basePath + "chatrooms/" + chatroomId;

        return HttpTemplate
                .execute(restTemplate, url, HttpMethod.DELETE, token, DeleteChatroomResponse.class)
                .getData()
                .getSuccess();
    }

    /**
     * 查询用户在线状态
     * @param username 用户环信ID
     * @return UserStatus
     */
    public UserStatus userStatus(String username, String token) {

        String url = String.format("%susers/%s/status", basePath, username.toLowerCase());

        return HttpTemplate
                .execute(restTemplate, url, HttpMethod.GET, token, GetUserStatusResponse.class)
                .getData()
                .getOrDefault(username.toLowerCase(), UserStatus.OFFLINE);
    }
}
