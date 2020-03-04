package com.easemob.live.server.rest.chatroom;

import lombok.Builder;
import lombok.Data;

/**
 * @author shenchong@easemob.com 2020/2/20
 */
@Data
@Builder
public class AssignChatroomOwnerRequest {
    private String newowner;
}
