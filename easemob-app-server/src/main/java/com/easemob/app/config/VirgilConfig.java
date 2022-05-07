package com.easemob.app.config;

import com.easemob.app.exception.ASException;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.virgilsecurity.sdk.common.TimeSpan;
import com.virgilsecurity.sdk.crypto.*;
import com.virgilsecurity.sdk.crypto.exceptions.CryptoException;
import com.virgilsecurity.sdk.jwt.JwtGenerator;
import com.virgilsecurity.sdk.utils.ConvertionUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "virgil.app")
public class VirgilConfig {
    private String id;

    @JsonProperty("private_key")
    private String privateKey;

    @JsonProperty("key_id")
    private String keyId;

    private Integer ttl;

    @Bean
    public JwtGenerator jwtGenerator() {
        VirgilCrypto crypto = new VirgilCrypto();

        VirgilKeyPair keyPair;
        try {
            keyPair = crypto.importPrivateKey(ConvertionUtils.base64ToBytes(this.privateKey));
        } catch (CryptoException e) {
            log.error("virgil jwt init fail. e : {}", e.getMessage());
            throw new ASException("virgil jwt init fail.");
        }

        VirgilAccessTokenSigner accessTokenSigner = new VirgilAccessTokenSigner();

        return new JwtGenerator(id, keyPair.getPrivateKey(), keyId,
                TimeSpan.fromTime(ttl, TimeUnit.HOURS), accessTokenSigner);
    }

}
