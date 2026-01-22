package com.easemob.app.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatGroup {
    /**
     * 自定义扩展信息
     */
    private String custom;

    /**
     * 创建时间戳
     */
    private Long created;
}
