package com.easemob.app.service.impl;

import com.easemob.app.config.ApplicationConfig;
import com.easemob.app.exception.ASNotFoundException;
import com.easemob.app.model.ChatGroupListResponse;
import com.easemob.app.service.RestService;
import com.easemob.app.utils.RestUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.io.*;
import java.util.*;

@Slf4j
@Service
public class RestServiceImpl implements RestService {

    private static final String DEFAULT_CONTACT_NAME = "huanhuan";

    private static final String DEFAULT_CHAT_GROUP_NAME = "四海之内皆兄弟";

    private static final String DEFAULT_CHAT_GROUP_MEMBER_NAME_AXIN = "axin001";

    private static final String DEFAULT_CHAT_GROUP_MEMBER_NAME_SHUYI = "shuyi001";

    @Value("${application.baseHttpUri}")
    private String baseUri;

    @Value("${application.intranet.base.https.uri}")
    private String intranetBaseUri;

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

    @Override
    public String getChatUserToken(String appkey, String chatUserName, String chatUserPassword) {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];
        String url = baseUri + "/" + orgName + "/" + appName + "/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(
                Objects.requireNonNull(RestUtil.getToken(appkey, restTemplate, applicationConfig)));

        Map<String, String> body = new HashMap<>();
        body.put("grant_type", "inherit");
        body.put("username", chatUserName);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> responseEntity;

        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        } catch (Exception e) {
            log.error("Get chat user. appkey : {}, chatUserName : {}, error : {}", appkey, chatUserName, e.getMessage());
            throw new RestClientException("Get chat user token error.");
        }

        return (String) responseEntity.getBody().get("access_token");
    }

    @Override
    public void addContact(String appkey, String chatUserName) {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];
        String url = baseUri + "/" + orgName + "/" + appName + "/users/" + chatUserName + "/contacts/users/" + DEFAULT_CONTACT_NAME;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(Objects.requireNonNull(RestUtil.getToken(appkey, restTemplate, applicationConfig)));

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
        String url = baseUri + "/" + orgName + "/" + appName + "/chatgroups";

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(Objects.requireNonNull(RestUtil.getToken(appkey, restTemplate, applicationConfig)));

        Map<String, Object> body = new HashMap<>();
        body.put("groupname", DEFAULT_CHAT_GROUP_NAME);
        body.put("public", false);
        body.put("owner", chatUserName);
        body.put("custom", "default");

        List<String> members = new ArrayList<>();
        members.add(DEFAULT_CHAT_GROUP_MEMBER_NAME_AXIN);
        members.add(DEFAULT_CHAT_GROUP_MEMBER_NAME_SHUYI);
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

    @Override
    public void sendMessageToUser(String appkey, String chatUserName, String messageContent) {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];
        String url = baseUri + "/" + orgName + "/" + appName + "/messages/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(Objects.requireNonNull(RestUtil.getToken(appkey, restTemplate, applicationConfig)));

        Map<String, Object> body = new HashMap<>();
        body.put("from", DEFAULT_CONTACT_NAME);
        List<String> tos = new ArrayList<>();
        tos.add(chatUserName);
        body.put("to", tos);
        body.put("type", "txt");
        Map<String, String> messageBody = new HashMap<>();
        messageBody.put("msg", messageContent);
        body.put("body", messageBody);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        } catch (Exception e) {
            log.error("send message to user. appkey : {}, chatUserName : {}, error : {}", appkey, chatUserName, e.getMessage());
            throw new RestClientException("Send message to user error.");
        }
    }

    @Override
    public void sendMessageToUser(String appkey, String from, String chatUserName, String messageContent) {
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
        body.put("type", "txt");
        Map<String, String> messageBody = new HashMap<>();
        messageBody.put("msg", messageContent);
        body.put("body", messageBody);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        } catch (Exception e) {
            log.error("send message to user. appkey : {}, chatUserName : {}, error : {}", appkey, chatUserName, e.getMessage());
            throw new RestClientException("Send message to user error.");
        }
    }

    @Override
    public void sendMessageToChatGroup(String appkey, String chatGroupId, String messageContent) {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];
        String url = baseUri + "/" + orgName + "/" + appName + "/messages/chatgroups";

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(Objects.requireNonNull(RestUtil.getToken(appkey, restTemplate, applicationConfig)));

        Map<String, Object> body = new HashMap<>();
        body.put("from", DEFAULT_CHAT_GROUP_MEMBER_NAME_AXIN);
        List<String> tos = new ArrayList<>();
        tos.add(chatGroupId);
        body.put("to", tos);
        body.put("type", "txt");
        Map<String, String> messageBody = new HashMap<>();
        messageBody.put("msg", messageContent);
        body.put("body", messageBody);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        } catch (Exception e) {
            log.error("send message to chat group. appkey : {}, chatGroupId : {}, error : {}", appkey, chatGroupId, e.getMessage());
            throw new RestClientException("Send message to chat group error.");
        }
    }

    @Override public ChatGroupListResponse getUsers(String appkey, String cursor) {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];

        String url;
        if (StringUtils.isBlank(cursor)) {
            url = baseUri + "/" + orgName + "/" + appName + "/users?limit=100";
        } else {
            url = baseUri + "/" + orgName + "/" + appName + "/users?limit=100&cursor=" + cursor;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(Objects.requireNonNull(RestUtil.getToken(appkey, restTemplate, applicationConfig)));

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(null, headers);
        ResponseEntity<Map> responseEntity = null;

        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        } catch (Exception e) {
            log.error("get chat users list. appkey : {}, error : {}", appkey, e.getMessage());
        }

        if (responseEntity == null) {
            return null;
        }

        List<Map<String, String>> data =
                (List<Map<String, String>>) responseEntity.getBody().get("entities");
        String responseCursor = (String) responseEntity.getBody().get("cursor");

        List<String> userIds = new ArrayList<>();
        if (!data.isEmpty()) {
            data.forEach(user -> {
                userIds.add(user.get("username"));
            });
        }

        return new ChatGroupListResponse(userIds, responseCursor);
    }

    @Override
    public String getChatGroupCustom(String appkey, String chatGroupId) {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];

        String url = baseUri + "/" + orgName + "/" + appName + "/chatgroups/" + chatGroupId;;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(Objects.requireNonNull(RestUtil.getToken(appkey, restTemplate, applicationConfig)));

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(null, headers);
        ResponseEntity<Map> responseEntity = null;

        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        } catch (Exception e) {
            log.error("get chat group created. appkey : {}, error : {}", appkey, e.getMessage());
        }

        if (responseEntity == null) {
            return null;
        }

        List<Map<String, Object>> data =
                (List<Map<String, Object>>) responseEntity.getBody().get("data");

        return (String) data.get(0).get("custom");
    }

    @Override public void updateGroupCustom(String appkey, String chatGroupId, String custom) {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];
        String url = baseUri + "/" + orgName + "/" + appName + "/chatgroups/" + chatGroupId;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(Objects.requireNonNull(RestUtil.getToken(appkey, restTemplate, applicationConfig)));

        Map<String, Object> body = new HashMap<>();
        body.put("custom", custom);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            restTemplate.exchange(url, HttpMethod.PUT, entity, Map.class);
        } catch (Exception e) {
            log.error("update chat group custom. appkey : {}, chatGroupId : {}, error : {}", appkey, chatGroupId, e.getMessage());
            throw new RestClientException("Update chat group custom error.");
        }
    }

    @Override
    public void deleteChatGroup(String appkey, String chatGroupId) {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];

        String url = baseUri + "/" + orgName + "/" + appName + "/chatgroups/" + chatGroupId;;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(Objects.requireNonNull(RestUtil.getToken(appkey, restTemplate, applicationConfig)));

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(null, headers);

        try {
            restTemplate.exchange(url, HttpMethod.DELETE, entity, Map.class);
        } catch (Exception e) {
            log.error("delete chat group. appkey : {}, error : {}", appkey, e.getMessage());
        }
    }

    @Override
    public Tuple2<String, String> getAppSecret(String appkey) {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];

        String url = baseUri + "/" + orgName + "/" + appName + "/credentials";
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(
                Objects.requireNonNull(RestUtil.getToken(appkey, restTemplate, applicationConfig)));

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(null, headers);
        ResponseEntity<Map> responseEntity = null;

        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        } catch (Exception e) {
            log.error("get chat group created. appkey : {}, error : {}", appkey, e.getMessage());
        }

        if (responseEntity == null || responseEntity.getStatusCode() != HttpStatus.OK) {
            return null;
        }

        Map<String, String> credentials =
                (Map<String, String>) responseEntity.getBody().get("credentials");
        String clientId = credentials.get("client_id");
        String clientSecret = credentials.get("client_secret");
        return Tuples.of(clientId, clientSecret);
    }

    @Override
    public List<String> getChatGroupMembers(String appkey, String chatGroupId) {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];

        String url = baseUri + "/" + orgName + "/" + appName + "/chatgroups/" + chatGroupId + "/users?pagenum=1&pagesize=9";

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(Objects.requireNonNull(RestUtil.getToken(appkey, restTemplate, applicationConfig)));

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(null, headers);
        ResponseEntity<Map> responseEntity = null;

        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        } catch (Exception e) {
            log.error("get chat group members. appkey : {}, groupId : {}, error : {}", appkey, chatGroupId, e.getMessage());
            if (e.getMessage().contains("Not Found")) {
                throw new ASNotFoundException("Chat group not found.");
            }
        }

        List<Map<String, String>> members =
                (List<Map<String, String>>) responseEntity.getBody().get("data");

        List<String> memberNames = new ArrayList<>();
        members.forEach(member -> {
            String username = member.get("member");
            if (username == null) {
                memberNames.add(member.get("owner"));
            } else {
                memberNames.add(username);
            }
        });

        Collections.reverse(memberNames);

        return memberNames;
    }

    @Override public String uploadFile(String appkey, String id, File file) {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];
        String intranetUrl = intranetBaseUri + "/" + orgName + "/" + appName + "/chatfiles";

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

    @Override public BufferedInputStream downloadThumbImage(String appkey, String urlPath) {

        String url = intranetBaseUri + urlPath;

        HttpHeaders headers = new HttpHeaders();
        headers.set("thumbnail", "true");
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
        headers.setBearerAuth(Objects.requireNonNull(RestUtil.getToken(appkey, restTemplate, applicationConfig)));


        HttpEntity<Map<String, String>> entity = new HttpEntity<>(null, headers);
        ResponseEntity<byte[]> responseEntity;

        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);
        } catch (Exception e) {
            log.error("download chat file error. appkey : {}, url : {}, error : {}", appkey, url, e.getMessage());
            return null;
        }

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(responseEntity.getBody());
        return new BufferedInputStream(byteArrayInputStream);
    }

}
