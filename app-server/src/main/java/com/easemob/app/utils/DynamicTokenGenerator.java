package com.easemob.app.utils;

import com.easemob.app.model.DynamicTokenResource;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;

public class DynamicTokenGenerator {
    private static ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    public static Tuple2<String, Long> generate(String appkey, String clientId, String clientSecret,
            String username, Long ttl) {
        long curTime = Instant.now().getEpochSecond();
        String signature = sha1(clientId + appkey + username + curTime + ttl + clientSecret);
        DynamicTokenResource resource =
                new DynamicTokenResource(signature, appkey, username, curTime, ttl);
        String json = toJson(resource);
        String token = Base64.getUrlEncoder().encodeToString(("dt-" + json).getBytes());
        return Tuples.of(token, curTime);
    }

    private static String toJson(DynamicTokenResource resource) {
        try {
            String s = objectMapper.writeValueAsString(resource);
            return s;
        } catch (JsonProcessingException e) {
            System.err.println(e);
            return null;
        }
    }

    @SneakyThrows
    private static String sha1(String input) {
        StringBuilder sb = new StringBuilder();
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] result = digest.digest(input.getBytes());
        for (byte b : result) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

}
