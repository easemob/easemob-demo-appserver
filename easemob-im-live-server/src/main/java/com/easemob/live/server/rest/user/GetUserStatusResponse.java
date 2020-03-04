package com.easemob.live.server.rest.user;

import com.easemob.live.server.rest.RestResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * @author shenchong@easemob.com 2020/2/24
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GetUserStatusResponse extends RestResponse {

    private Map<String, UserStatus> data;
}
