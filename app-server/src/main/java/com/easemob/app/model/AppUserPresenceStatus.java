package com.easemob.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AppUserPresenceStatus {

    private String username;

    private Boolean onlineStatus;
}
