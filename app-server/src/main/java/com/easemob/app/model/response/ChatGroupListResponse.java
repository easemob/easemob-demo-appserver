package com.easemob.app.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ChatGroupListResponse {

    /**
     * 群组 ID 列表
     */
    private List<String> chatGroupIds;

    /**
     * 分页游标
     */
    private String cursor;
}
