package com.easemob.live.server.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

/**
 * @author shenchong@easemob.com 2020/2/19
 */
@Slf4j
public class JsonUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    private JsonUtils() {

    }

    public static String mapToJsonString(Object contentsObj) {

        if (contentsObj == null) {
            return null;
        }

        try {
            return mapper.writeValueAsString(contentsObj);
        } catch (JsonProcessingException e) {
            log.error("Failed to write {} to string", contentsObj, e);
        }
        return StringUtils.EMPTY;
    }

    public static <T> T parse(String json, Class<T> clazz) {

        if (json == null) {
            return null;
        }

        try {
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            log.error("Error parsing JSON, json:{}", json, e);
            return null;
        }
    }
}
