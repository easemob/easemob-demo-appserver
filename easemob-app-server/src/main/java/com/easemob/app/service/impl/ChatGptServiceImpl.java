package com.easemob.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.easemob.app.model.ChatGptMessage;
import com.easemob.app.model.ChatGptRequest;
import com.easemob.app.service.ChatGptService;
import com.easemob.app.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ChatGptServiceImpl implements ChatGptService {

    private static final String CHOICES = "choices";
    private static final String MESSAGE = "message";
    private static final String CONTENT = "content";
    private static final String USER = "user";
    private static final String SYSTEM = "system";
    private static final String SYSTEM_CONTENT = "You are a helpful assistant.";

    private RestTemplate restTemplate;

    private RedisService redisService;

    @Value("${chatgpt.api.key}")
    private String apiKey;

    @Value("${chatgpt.url}")
    private String url;

    @Value("${chatgpt.model}")
    private String model;

    @Value("${chatgpt.max.tokens}")
    private Integer maxTokens;

    @Value("${chatgpt.temperature}")
    private Float temperature;

    @Value("${split.chatgpt.answer.message.length}")
    private Integer splitLength;

    @Value("${send.message.to.chatgpt.count.limit.switch}")
    private Boolean countLimitSwitch;

    @Value("${send.chatgpt.message.retry.interval.count}")
    private Integer retryCount;

    @Value("${send.chatgpt.message.retry.interval.milliseconds}")
    private Integer retryInterval;

    public ChatGptServiceImpl(RestTemplate restTemplate,
            RedisService redisService) {
        this.restTemplate = restTemplate;
        this.redisService = redisService;
    }

    @Override public String sendChatMessage(String appKey, String username, String messageContent) {
        log.info("start send chat message to chatGPT. appKey : {}, username : {}", appKey, username);

        List<ChatGptMessage> messageList = new ArrayList<>();
        ChatGptMessage messages = ChatGptMessage.builder()
                .role(USER)
                .content(messageContent)
                .build();
        messageList.add(messages);

        String result = null;
        int retry = retryCount;

        while (true) {
            try {
                result = sendChatMessageToChatGpt(appKey, username, messageList);
                break;
            } catch (Exception e) {
                log.error(
                        "chatGPT answer chat message error. appKey : {}, username : {}, e : {}",
                        appKey, username, e.getMessage());
                retry--;
            }

            try {
                Thread.sleep(retryInterval);
            } catch (InterruptedException e) {
                log.warn(
                        "chatGPT answer chat message | thread sleep fail. appKey : {}, username : {}, e : {}",
                        appKey, username, e.getMessage());
            }

            if (retry == 0) {
                break;
            }
        }

        return result;
    }

    @Override
    public String sendChatGroupMessage(String appKey, String username, String groupId, String messageContent) {
        log.info("start send chat group message to chatGPT. appKey : {}, groupId : {}, username : {}",
                appKey, groupId, username);

        List<ChatGptMessage> messageList = new ArrayList<>();

        redisService.getGroupContextMessages(appKey, groupId).forEach(message -> {
            messageList.add(JSONObject.parseObject(message, ChatGptMessage.class));
        });

        messageList.add(ChatGptMessage.builder()
                .role(SYSTEM)
                .content(SYSTEM_CONTENT)
                .build());

        Collections.reverse(messageList);

        ChatGptMessage userMessage = ChatGptMessage.builder()
                .role(USER)
                .content(String.format("%s said:%s", username, messageContent))
                .build();
        messageList.add(userMessage);

        redisService.addGroupMessageToRedis(appKey, groupId, userMessage);

        log.info("redis context messages. appkey : {}, groupId : {}, messages :{}", appKey, groupId, messageList);

        String result = null;
        int retry = retryCount;

        while (true) {
            try {
                result = sendGroupMessageToChatGpt(appKey, username, groupId, messageList);
                break;
            } catch (Exception e) {
                log.error(
                        "chatGPT answer chat group message error. appKey : {}, groupId : {}, username : {}, e : {}",
                        appKey, groupId, username, e.getMessage());
                retry--;
            }

            try {
                Thread.sleep(retryInterval);
            } catch (InterruptedException e) {
                log.warn(
                        "chatGPT answer chat group message | thread sleep fail. appKey : {}, groupId : {}, username : {}, e : {}",
                        appKey, groupId, username, e.getMessage());
            }

            if (retry == 0) {
                break;
            }
        }

        return result;
    }

    private String sendChatMessageToChatGpt(String appKey, String username, List<ChatGptMessage> messageList) {
        ChatGptRequest chatGptRequest = new ChatGptRequest(model, messageList, temperature, maxTokens);

        ResponseEntity<Map> responseEntity = restTemplate
                .exchange(url, HttpMethod.POST, getHeaderAndBody(chatGptRequest), Map.class);

        if (responseEntity.getBody() == null) {
            log.error("chatGPT answer message body is null.");
            return null;
        }

        List<Map> choices = (List<Map>) responseEntity.getBody().get(CHOICES);
        if (choices != null && choices.size() > 0) {
            Map<String, String> message = (Map<String, String>) choices.get(0).get(MESSAGE);
            if (message != null) {
                String content = message.get(CONTENT);
                if (countLimitSwitch) {
                    redisService.decrNumberOfSendMessageToChatGpt(appKey, username);
                }
                String tempText = removeLeadingNewlines(content);
                if (tempText.length() > splitLength) {
                    return StringUtils.substring(content,0, splitLength);
                } else {
                    return tempText;
                }
            } else {
                return null;
            }
        } else {
            log.error("chatGPT answer text is null.");
            return null;
        }
    }

    private String sendGroupMessageToChatGpt(String appKey, String username, String groupId, List<ChatGptMessage> messageList) {
        ChatGptRequest chatGptRequest = new ChatGptRequest(model, messageList, temperature, maxTokens);

        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.POST, getHeaderAndBody(chatGptRequest), Map.class);

        if (responseEntity.getBody() == null) {
            log.error("chat group | chatGPT answer message body is null. groupId : {}", groupId);
            return null;
        }

        List<Map> choices = (List<Map>) responseEntity.getBody().get(CHOICES);
        if (choices != null && choices.size() > 0) {
            Map<String, String> message = (Map<String, String>) choices.get(0).get(MESSAGE);
            if (message != null) {
                String content = message.get(CONTENT);
                if (countLimitSwitch) {
                    redisService.decrNumberOfSendMessageToChatGpt(appKey, username);
                }
                return removeLeadingNewlines(content);
            } else {
                return null;
            }
        } else {
            log.error("chat group | chatGPT answer text is null. groupId : {}", groupId);
            return null;
        }
    }

    private HttpEntity<Object> getHeaderAndBody(ChatGptRequest body) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Authorization", "Bearer " + apiKey);
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, requestHeaders);
    }

    private static String removeLeadingNewlines(String input) {
        String pattern = "^(\\r?\\n|\\?\\r?\\n)*";
        return input.replaceAll(pattern, "");
    }

}
