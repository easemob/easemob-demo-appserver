package com.easemob.live.server.rest.chatroom;

import com.easemob.live.server.rest.RestResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author shenchong@easemob.com 2020/2/24
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DeleteChatroomResponse extends RestResponse {

    private DataWrapper data;

    @Data
    public static class DataWrapper {

        private Boolean success;

        private String id;
    }
}
