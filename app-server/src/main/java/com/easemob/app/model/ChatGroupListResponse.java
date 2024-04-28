package com.easemob.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ChatGroupListResponse {

    private List<String> chatGroupIds;

    private String cursor;
}
