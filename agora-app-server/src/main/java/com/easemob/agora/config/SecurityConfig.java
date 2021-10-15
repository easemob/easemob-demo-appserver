package com.easemob.agora.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author skyfour
 * @date 2021/2/2
 * @email skyzhang@easemob.com
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    }

    @Override public void configure(WebSecurity http) throws Exception {
        http.ignoring().antMatchers("/token/**", "/app/user/**", "/app/chat/user/**", "/management/**");
    }
}
