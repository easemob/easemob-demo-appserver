package com.easemob.agora.model;

import lombok.Data;

/**
 * @author skyfour
 * @date 2021/2/18
 * @email skyzhang@easemob.com
 */
@Data
public class UserAuth {

    private String orgName;

    private String appName;

    private String token;

    public UserAuth(String orgName, String appName, String token) {
        this.orgName = orgName;
        this.appName = appName;
        this.token = token;
    }
}
