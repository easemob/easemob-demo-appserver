package com.easemob.live.server.rest.chatroom;

import com.easemob.live.server.rest.RestResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author shenchong@easemob.com 2020/2/19
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AssignChatroomOwnerResponse extends RestResponse {

    private Object entities;

    private DataWrapper data;

    @Data
    public static class DataWrapper {
        private Boolean newowner;
    }
}
