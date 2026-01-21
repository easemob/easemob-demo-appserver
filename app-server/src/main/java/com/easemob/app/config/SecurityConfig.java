package com.easemob.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author skyfour
 * @date 2021/2/2
 * @email skyzhang@easemob.com
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true
)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/**", "/agora/channel/mapper", "/token/**", "/app/user/**", "/app/chat/user/**", "/management/**")
                .permitAll()
                .anyRequest()
                .authenticated()
        );
        http.csrf(csrf -> csrf.disable());
        return http.build();
    }
}
