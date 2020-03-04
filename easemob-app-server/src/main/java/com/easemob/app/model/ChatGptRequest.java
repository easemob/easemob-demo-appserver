package com.easemob.app.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ChatGptRequest {

    private String model;

    private List<ChatGptMessage> messages;

    private Float temperature;

    @JsonProperty("max_tokens")
    private Integer maxTokens;

    @JsonCreator
    public ChatGptRequest(String model, List<ChatGptMessage> messages,
            Float temperature, @JsonProperty("max_tokens") Integer maxTokens) {
        this.model = model;
        this.messages = messages;
        this.temperature = temperature;
        this.maxTokens = maxTokens;
    }
}
