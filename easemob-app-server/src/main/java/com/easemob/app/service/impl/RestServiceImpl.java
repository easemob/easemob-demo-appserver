package com.easemob.app.service.impl;

import com.easemob.app.config.Rest2Properties;
import com.easemob.app.service.RestService;
import com.easemob.app.utils.RestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
public class RestServiceImpl implements RestService {

    @Value("${application.baseUri}")
    private String baseUri;

    @Autowired
    private Rest2Properties rest2Properties;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override public void registerChatUserName(String appkey, String chatUserName) {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];
        String url = baseUri + "/" + orgName + "/" + appName + "/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(
                Objects.requireNonNull(RestUtil.getToken(appkey, restTemplate, rest2Properties)));

        Map<String, String> body = new HashMap<>();
        body.put("username", chatUserName);
        body.put("password", "123");

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
        try {
            restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        } catch (HttpClientErrorException e) {
            log.error("register chat user. chatUserName : {}, error : {}", chatUserName, e.getMessage());
            throw new RestClientException("Register chat user error.");
        }
    }

    @Override public boolean checkIfChatUserNameExists(String appkey, String chatUserName) {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];
        String url = baseUri + "/" + orgName + "/" + appName + "/users/" + chatUserName;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        // exchangeToken() get Agora Chat app token
        headers.setBearerAuth(Objects.requireNonNull(RestUtil.getToken(appkey, restTemplate, rest2Properties)));

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(null, headers);
        ResponseEntity<Map> responseEntity = null;
        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return false;
            }
            log.error("get chat user. chatUserName : {}, error : {}", chatUserName, e.getMessage());
            throw new RestClientException("Get chat user error.");
        } catch (Exception e) {
            log.error("get chat user. chatUserName : {}, error : {}", chatUserName, e.getMessage());
            throw new RestClientException("Get chat user error.");
        }

        return responseEntity.getStatusCode().is2xxSuccessful();
    }

    @Override public String getChatUserUuid(String appkey, String chatUserName) {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];
        String url = baseUri + "/" + orgName + "/" + appName + "/users/" + chatUserName;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        // exchangeToken() get Agora Chat app token
        headers.setBearerAuth(Objects.requireNonNull(RestUtil.getToken(appkey, restTemplate, rest2Properties)));

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(null, headers);
        ResponseEntity<Map> responseEntity;
        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        } catch (RestClientException e) {
            log.error("get chat user. chatUserName : {}, error : {}", chatUserName, e.getMessage());
            throw new RestClientException("Get chat user error.");
        }

        List<Map<String, Object>> results = (List<Map<String, Object>>) responseEntity.getBody().get("entities");
        return (String) results.get(0).get("uuid");
    }
}
