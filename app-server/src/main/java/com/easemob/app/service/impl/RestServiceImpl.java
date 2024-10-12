package com.easemob.app.service.impl;

import com.easemob.app.config.ApplicationConfig;
import com.easemob.app.model.AppUserOnlineStatus;
import com.easemob.app.model.AppUserPresenceStatus;
import com.easemob.app.service.RestService;
import com.easemob.app.utils.RestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.*;

@Slf4j
@Service
public class RestServiceImpl implements RestService {

    @Value("${application.baseUri}")
    private String baseUri;

    @Autowired
    private ApplicationConfig applicationConfig;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override public void registerChatUserName(String appkey, String chatUserName, String chatUserPassword) {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];
        String url = baseUri + "/" + orgName + "/" + appName + "/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(
                Objects.requireNonNull(RestUtil.getToken(appkey, restTemplate, applicationConfig)));

        Map<String, String> body = new HashMap<>();
        body.put("username", chatUserName);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
        try {
            restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        } catch (Exception e) {
            log.error("register chat user. chatUserName : {}, error : {}", chatUserName, e.getMessage());
            throw new RestClientException("Register chat user error.");
        }

        log.info("register chat user success. chatUserName : {}", chatUserName);
    }

    @Override public String getChatUserToken(String appkey, String chatUserName) {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];
        String url = baseUri + "/" + orgName + "/" + appName + "/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(Objects.requireNonNull(RestUtil.getToken(appkey, restTemplate, applicationConfig)));

        Map<String, Object> body = new HashMap<>();
        body.put("grant_type", "inherit");
        body.put("username", chatUserName);
        body.put("autoCreateUser", false);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> responseEntity;

        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        } catch (Exception e) {
            log.error("Get chat user | inherit. appkey : {}, chatUserName : {}, error : {}", appkey, chatUserName, e.getMessage());
            throw new RestClientException("Get chat user token error.");
        }

        return (String) responseEntity.getBody().get("access_token");
    }

    @Override public String uploadFile(String appkey, String id, File file) {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];
        String intranetUrl = baseUri + "/" + orgName + "/" + appName + "/chatfiles";

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(Objects.requireNonNull(RestUtil.getToken(appkey, restTemplate, applicationConfig)));

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(file));

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> responseEntity;

        try {
            responseEntity = restTemplate.exchange(intranetUrl, HttpMethod.POST, entity, Map.class);
        } catch (Exception e) {
            log.error("upload chat file error. appkey : {}, id :{}, error : {}", appkey, id, e.getMessage());
            throw new RestClientException("upload chat file error.");
        }

         List<Map<String, String>> entities =
                 (List<Map<String, String>>) responseEntity.getBody().get("entities");
        String fileUuid = entities.get(0).get("uuid");
        String fileUrl = baseUri + "/" + orgName + "/" + appName + "/chatfiles" + "/" + fileUuid;

        return fileUrl;
    }

    @Override public List<AppUserOnlineStatus> getUserOnlineStatus(String appkey,
            List<String> chatUserNames) {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];
        String url = baseUri + "/" + orgName + "/" + appName + "/users/batch/status";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(
                Objects.requireNonNull(RestUtil.getToken(appkey, restTemplate, applicationConfig)));

        Map<String, List<String>> body = new HashMap<>();
        body.put("usernames", chatUserNames);

        HttpEntity<Map<String, List<String>>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> responseEntity;
        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        } catch (Exception e) {
            log.error("get user presence status fail. appkey : {}, chatUserNames : {}, error : {}", appkey, chatUserNames, e.getMessage());
            throw new RestClientException("Get user presence status error.");
        }

        List<Map<String, String>> result = (List<Map<String, String>>) responseEntity.getBody().get("data");
        List<AppUserOnlineStatus> appUserOnlineStatuses = new ArrayList<>();
        for (Map<String, String> userStatus : result) {
            String username = userStatus.keySet().iterator().next();
            Boolean onlineStatus = userStatus.values().iterator().next().equals("online");

            appUserOnlineStatuses.add(new AppUserOnlineStatus(username, onlineStatus));
        }

        return appUserOnlineStatuses;
    }

    @Override
    public void setUserMetadata(String appkey, String chatUserName, MultiValueMap<String, Object> metadata) {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];
        String url = baseUri + "/" + orgName + "/" + appName + "/metadata/user/" + chatUserName;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBearerAuth(Objects.requireNonNull(RestUtil.getToken(appkey, restTemplate, applicationConfig)));

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(metadata, headers);

        try {
            restTemplate.exchange(url, HttpMethod.PUT, entity, Map.class);
        } catch (Exception e) {
            log.error("set user metadata avatar url. appkey : {}, chatUserName : {}, error : {}", appkey, chatUserName, e.getMessage());
            throw new RestClientException("Set user metadata error.");
        }
    }

    @Override
    public boolean checkUserTokenPermissions(String appkey, String chatUserName, String token) {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];
        String url = baseUri + "/" + orgName + "/" + appName + "/users/" + chatUserName;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(token);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(null, headers);

        try {
            restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        } catch (Exception e) {
            log.error("get chat user error. appkey : {}, chatUserName : {}, error : {}", appkey, chatUserName, e.getMessage());
            return false;
        }

        return true;
    }

    @Override public void sendCmdMessageToUser(String appkey, String from, String chatUserName,
            String action, boolean isRouteOnline, Map<String, Object> ext) {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];
        String url = baseUri + "/" + orgName + "/" + appName + "/messages/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(Objects.requireNonNull(RestUtil.getToken(appkey, restTemplate, applicationConfig)));

        Map<String, Object> body = new HashMap<>();
        body.put("from", from);
        List<String> tos = new ArrayList<>();
        tos.add(chatUserName);
        body.put("to", tos);
        body.put("type", "cmd");
        if (isRouteOnline) {
            body.put("routetype", "ROUTE_ONLINE");
        }
        Map<String, String> messageBody = new HashMap<>();
        messageBody.put("action", action);
        body.put("body", messageBody);
        if (ext != null) {
            body.put("ext", ext);
        }

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        } catch (Exception e) {
            log.error("send cmd message to user. appkey : {}, chatUserName : {}, error : {}", appkey, chatUserName, e.getMessage());
            throw new RestClientException("Send cmd message to user error.");
        }
    }

}
