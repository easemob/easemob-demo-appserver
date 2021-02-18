package com.easemob.agora.utils;

import com.easemob.agora.config.ApplicationConf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author skyfour
 */
@Slf4j
@Component
public class RestManger {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ApplicationConf applicationConf;

    private static final String GET_TOKEN = "http://%s/%s/%s/token";

    private static final String GET_USER = "http://%s/%s/%s/user/%s";

    private static final String USERNAME = "username";

    public String getToken(String username, String password, String orgName, String appName) {
        if (StringUtils.isEmpty(orgName) || StringUtils.isEmpty(appName)) {
            orgName = applicationConf.getOrgName();
            appName = applicationConf.getAppName();
        }
        String url = String.format(GET_TOKEN, applicationConf.getRestServer(),
                orgName, appName);

        Map<String, String> body = new HashMap<>();
        body.put(USERNAME, username);
        body.put("password", password);
        body.put("grant_type", "password");
        ResponseEntity<Map> responseEntity =
                null;
        try {
            responseEntity = restTemplate
                    .exchange(url, HttpMethod.POST, getHeaderAndBody(body, null), Map.class);
        } catch (RestClientException e) {
            log.error("getToken failed.username:{},orgName:{},appName:{}", username, orgName,
                    appName, e);
            return null;
        }
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            log.error("getAppId | failed connection to im-rest2-server, statusCode : {}",
                    responseEntity.getStatusCode());
            return null;
        }
        return (String) responseEntity.getBody().get("access_token");
    }

    public String getUser(String username, String token, String orgName, String appName) {
        if (StringUtils.isEmpty(orgName) || StringUtils.isEmpty(appName)) {
            orgName = applicationConf.getOrgName();
            appName = applicationConf.getAppName();
        }
        String url = String.format(GET_USER, applicationConf.getRestServer(),
                orgName, appName, username);
        ResponseEntity<Map> responseEntity;
        try {
            responseEntity = restTemplate
                    .exchange(url, HttpMethod.GET, getHeaderAndBody(null, token), Map.class);
        } catch (RestClientException e) {
            log.error("getUser failed.username:{},orgName:{},appName:{}", username, orgName,
                    appName, e);
            return null;
        }
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            log.error("getUserDetail | failed connection to im-rest2-server, statusCode : {}",
                    responseEntity.getStatusCode());
            return null;
        }
        Object entities = responseEntity.getBody().get("entities");
        if (entities != null) {
            List<Map> entitiesList = (List<Map>) entities;
            if (entitiesList.size() == 1) {
                Map<String, String> map = entitiesList.get(0);
                return map.containsKey(USERNAME) ?
                        entitiesList.get(0).get(USERNAME).toString() :
                        "";
            } else {
                log.error(" getUserDetail | is wrong entitiesList size !=1 |{}|", entitiesList);
            }
        } else {
            log.error("entities is null |{}|", username);
            return null;
        }
        return null;
    }

    private HttpEntity<Object> getHeaderAndBody(Object body, String token) {

        HttpHeaders requestHeaders = new HttpHeaders();
        if (!StringUtils.isEmpty(token)) {
            requestHeaders.add("Authorization", "Bearer " + token);
        }
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, requestHeaders);
    }
}
