package com.easemob.live.server.utils;

import com.easemob.live.server.liveroom.exception.LiveRoomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * @author shenchong@easemob.com 2020/2/21
 */
@Slf4j
public class HttpTemplate {

    /**
     * Http request without token
     */
    public static <T> T execute(RestTemplate restTemplate, String url, HttpMethod method,
            Object request, Class<T> responseType, Object... uriVariables) {
        return execute(restTemplate, url, method, null, request, responseType, uriVariables);
    }

    /**
     * Http request without request body
     */
    public static <T> T execute(RestTemplate restTemplate, String url, HttpMethod method,
            String token, Class<T> responseType, Object... uriVariables) {
        return execute(restTemplate, url, method, token, null, responseType, uriVariables);
    }

    /**
     * Http request method
     */
    public static <T> T execute(RestTemplate restTemplate, String url, HttpMethod method,
            String token, Object request, Class<T> responseType, Object... uriVariables) {

        ResponseEntity<T> response;

        try {
            response = restTemplate
                    .exchange(url, method, buildHttpEntity(token, request), responseType,
                            uriVariables);
        } catch (HttpClientErrorException e) {
            log.error("http execute failed, request : {}, error code : {}, error info : {}",
                    request, e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }

        return nonNull(response.getBody());
    }

    /**
     * Build http entity for RestTemplate exchange
     *
     * @return HttpEntity
     */
    private static HttpEntity<String> buildHttpEntity(@Nullable String token,
            @Nullable Object body) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        if (token != null) {
            headers.setBearerAuth(token);
        }

        if (body == null) {
            return new HttpEntity<>(headers);
        }

        return new HttpEntity<>(JsonUtils.mapToJsonString(body), headers);
    }

    /**
     * Check if the response body is null,
     *
     * @return if null, throw LiveRoomException, else return result.
     */
    private static <T> T nonNull(@Nullable T result) {

        if (result == null) {
            throw new LiveRoomException("http execute failed, response is null");
        }
        return result;
    }
}
