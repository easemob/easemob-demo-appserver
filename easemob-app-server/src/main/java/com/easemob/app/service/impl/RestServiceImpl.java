package com.easemob.app.service.impl;

import com.easemob.app.service.RestService;
import com.easemob.app.service.TokenService;
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

    @Value("${application.agora.chat.baseUrl}")
    private String baseUrl;

    @Value("${easemob.chat.robot.name}")
    private String chatRobotName;

    @Value("${easemob.chat.default.group.member.bella}")
    private String defaultChatGroupMemberBella;

    @Value("${easemob.chat.default.group.member.miles}")
    private String defaultChatGroupMemberMiles;

    private static final String DEFAULT_CHAT_GROUP_NAME = "AI Chatbot'group";

    @Autowired
    private TokenService tokenService;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override public void registerChatUserName(String appkey, String chatUserName) {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];
        String url = baseUrl + "/" + orgName + "/" + appName + "/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(tokenService.getAppToken().getToken());

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
        String url = baseUrl + "/" + orgName + "/" + appName + "/users/" + chatUserName;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(tokenService.getAppToken().getToken());

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

    @Override
    public void addContact(String appkey, String chatUserName, String contactName) {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];
        String url = baseUrl + "/" + orgName + "/" + appName + "/users/" + chatUserName + "/contacts/users/" + contactName;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(tokenService.getAppToken().getToken());

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(null, headers);

        try {
            restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        } catch (Exception e) {
            log.error("chat user add contact. appkey : {}, chatUserName : {}, error : {}", appkey, chatUserName, e.getMessage());
            throw new RestClientException("Add contact error.");
        }
    }

    @Override
    public String createChatGroup(String appkey, String chatUserName) {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];
        String url = baseUrl + "/" + orgName + "/" + appName + "/chatgroups";

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(tokenService.getAppToken().getToken());

        Map<String, Object> body = new HashMap<>();
        body.put("groupname", DEFAULT_CHAT_GROUP_NAME);
        body.put("public", false);
        body.put("owner", chatUserName);
        body.put("custom", "default");

        List<String> members = new ArrayList<>();
        members.add(defaultChatGroupMemberBella);
        members.add(defaultChatGroupMemberMiles);
        members.add(chatRobotName);
        body.put("members", members);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> responseEntity;

        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        } catch (Exception e) {
            log.error("create chat group. appkey : {}, chatUserName : {}, error : {}", appkey, chatUserName, e.getMessage());
            throw new RestClientException("Create chat group error.");
        }

        Map<String, String> data = (Map<String, String>) responseEntity.getBody().get("data");
        return data.get("groupid");
    }

    @Override public String getChatUserUuid(String appkey, String chatUserName) {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];
        String url = baseUrl + "/" + orgName + "/" + appName + "/users/" + chatUserName;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(tokenService.getAppToken().getToken());

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

    @Override
    public void sendTextMessageToUser(String appkey, String from, String to, String messageContent,
            Map<String, Object> ext) {

        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];
        String url = baseUrl + "/" + orgName + "/" + appName + "/messages/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(tokenService.getAppToken().getToken());

        Map<String, Object> body = new HashMap<>();
        body.put("from", from);
        List<String> tos = new ArrayList<>();
        tos.add(to);
        body.put("to", tos);
        body.put("type", "txt");
        Map<String, String> messageBody = new HashMap<>();
        messageBody.put("msg", messageContent);
        body.put("body", messageBody);
        if (ext != null) {
            body.put("ext", ext);
        }

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        } catch (Exception e) {
            log.error("send message to user. chatUserName : {}, error : {}", to, e.getMessage());
            throw new RestClientException("Send message to user error.");
        }
    }

    @Override public void sendTextMessageToGroup(String appkey, String from, String groupId,
            String messageContent, Map<String, Object> ext) {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];
        String url = baseUrl + "/" + orgName + "/" + appName + "/messages/chatgroups";

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(tokenService.getAppToken().getToken());

        Map<String, Object> body = new HashMap<>();
        body.put("from", from);
        List<String> tos = new ArrayList<>();
        tos.add(groupId);
        body.put("to", tos);
        body.put("type", "txt");
        Map<String, String> messageBody = new HashMap<>();
        messageBody.put("msg", messageContent);
        body.put("body", messageBody);
        if (ext != null) {
            body.put("ext", ext);
        }

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        } catch (Exception e) {
            log.error("send message to group. groupId : {}, error : {}", groupId, e.getMessage());
            throw new RestClientException("Send message to group error.");
        }

    }

    @Override public void sendTextMessageToGroupMember(String appkey, String from, String groupId,
            String groupMember, String messageContent, Map<String, Object> ext) {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];
        String url = baseUrl + "/" + orgName + "/" + appName + "/messages/chatgroups/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(tokenService.getAppToken().getToken());

        Map<String, Object> body = new HashMap<>();
        body.put("from", from);
        List<String> tos = new ArrayList<>();
        tos.add(groupId);
        body.put("to", tos);
        body.put("type", "txt");
        Map<String, String> messageBody = new HashMap<>();
        messageBody.put("msg", messageContent);
        body.put("body", messageBody);
        List<String> users = new ArrayList<>();
        users.add(groupMember);
        body.put("users", users);
        if (ext != null) {
            body.put("ext", ext);
        }

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        } catch (Exception e) {
            log.error("send message to group member. groupId : {}, error : {}", groupId, e.getMessage());
            throw new RestClientException("Send message to group member error.");
        }
    }

}
