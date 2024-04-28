package com.easemob.app.utils;

import com.alibaba.fastjson.JSONObject;
import com.easemob.app.config.ApplicationConfig;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.concurrent.TimeUnit;

@Slf4j
@UtilityClass
public class RestUtil {

    /**
     * token cache
     */
    private Cache<String, String>
            tokenCache = CacheBuilder.newBuilder().maximumSize(1).expireAfterWrite(1, TimeUnit.DAYS).build();

    /**
     * get app token
     *
     * @param appkey appkey
     * @param restTemplate restTemplate
     * @param applicationConfig applicationConfig
     *
     * @return token
     */
    public String getToken(String appkey, RestTemplate restTemplate, ApplicationConfig applicationConfig) {
        String orgName = appkey.split("#")[0];
        String appName = appkey.split("#")[1];

        try {
            return tokenCache.get(String.format("%s-token", appkey),
                    () -> {

                        //  set header
                        JSONObject body = new JSONObject();
                        body.put("client_id", applicationConfig.getClientId());
                        body.put("client_secret", applicationConfig.getClientSecret());
                        body.put("grant_type", "client_credentials");

                        HttpHeaders header = new HttpHeaders();
                        header.setContentType(MediaType.APPLICATION_JSON_UTF8);

                        //  pull request
                        ResponseEntity<String> responseEntity = restTemplate
                                .exchange(
                                        UriComponentsBuilder
                                                .fromUriString(applicationConfig.getBaseHttpUri())
                                                .path(String.format("/%s/%s/token", orgName, appName))
                                                .build()
                                                .encode()
                                                .toUri(),
                                        HttpMethod.POST,
                                        new HttpEntity<>(body, header),
                                        String.class
                                );

                        if (responseEntity.getStatusCode().is2xxSuccessful()) {
                            return JSONObject.parseObject(responseEntity.getBody()).getString("access_token");
                        } else {
                            return null;
                        }
                    }
            );
        } catch (Exception e) {
            log.error("|{}|get exception when get token using |{}|, exception |{}|", appkey, applicationConfig, e.getMessage());
            return null;
        }
    }
}
