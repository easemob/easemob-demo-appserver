package com.easemob.discord.config;

import lombok.Data;

@Data
public class JdbcConfigProperties {

    private String url;

    private String username;

    private String password;

    private String driverClassName = "com.mysql.jdbc.Driver";

    private int minimumIdle = 10;

    private int maximumPoolSize = 100;
}
