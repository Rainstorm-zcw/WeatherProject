package org.example.windy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Windy JWT token 中的 payload（仅读取，不做签名校验）。
 *
 * @author windy
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WindyTokenClaims {

    private Integer magic;

    /**
     * 签发时间（秒）。
     */
    private Long iat;

    /**
     * 过期时间（秒）。
     */
    private Long exp;

    @JsonProperty("userID")
    private Long userId;

    private List<String> subscriptionTiers;
}
