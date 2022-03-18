package com.easemob.agora.config;

import com.easemob.agora.config.auth.token.TokenAuthenticationProcessingFilter;
import com.easemob.agora.config.auth.token.TokenAuthenticationProvider;
import com.easemob.agora.config.auth.username.UsernameAuthenticationProcessingFilter;
import com.easemob.agora.config.auth.username.UsernameAuthenticationProvider;
import com.easemob.agora.config.security.SecurityAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;

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

    /*@Autowired
    private SecurityAuthenticationEntryPoint unauthorizedHandler;

    @Autowired
    private TokenAuthenticationProvider tokenAuthenticationProvider;

    @Autowired
    private UsernameAuthenticationProvider usernameProvider;

    @Bean
    public TokenAuthenticationProcessingFilter tokenFilter() {
        return new TokenAuthenticationProcessingFilter();
    }

    @Bean
    public UsernameAuthenticationProcessingFilter usernameFilter() {
        return new UsernameAuthenticationProcessingFilter();
    }*/

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    /*@Override protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(tokenAuthenticationProvider);
        auth.authenticationProvider(usernameProvider);
    }*/

    @Override protected void configure(HttpSecurity http) throws Exception {

        http
                .cors()
                .disable()
                .csrf()
                .disable()
                .exceptionHandling()
                //.authenticationEntryPoint(unauthorizedHandler)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .anonymous();
//                .authorizeRequests()
//                .antMatchers("/",
//                        "/management/**",
//                        "/favicon.ico",
//                        "/**/*.png",
//                        "/**/*.gif",
//                        "/**/*.svg",
//                        "/**/*.jpg",
//                        "/**/*.html",
//                        "/**/*.css",
//                        "/**/*.js")
//                .permitAll()
//                .antMatchers("/token/**", "/channel/**")
//                .hasAuthority("user")
//                .anyRequest()
//                .authenticated();

        // 添加token auth filter
        // 也可以支持username password的方式只需要配置相应的filter和provider
        //http.addFilterBefore(tokenFilter(), UsernamePasswordAuthenticationFilter.class);
        //http.addFilterAfter(usernameFilter(), TokenAuthenticationProcessingFilter.class);

    }
}
