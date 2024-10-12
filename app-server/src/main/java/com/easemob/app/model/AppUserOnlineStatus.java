package com.easemob.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AppUserOnlineStatus {

    private String username;

    private Boolean onlineStatus;
}
