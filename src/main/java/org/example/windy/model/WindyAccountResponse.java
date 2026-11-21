package org.example.windy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * Windy 账号接口（/api/info、/api/login）的统一响应。
 * <p>
 * 预登录响应：{@code {"message":"ok","auth":false,"token":"...","userInfo":{...}}}
 * <br>
 * 登录响应：{@code {"message":"ok","userInfo":{"token":"...","userInfo":{...}},"auth":true}}
 *
 * @author windy
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WindyAccountResponse {

    private String message;

    private Boolean auth;

    /**
     * 预登录返回的 token。
     */
    private String token;

    private LoginUser userInfo;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LoginUser {

        private String message;

        private Boolean auth;

        private String token;

        private UserInfo userInfo;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserInfo {

        private Long id;

        private String email;

        private String username;

        private String userslug;

        private String fullname;

        private List<String> subscriptionTiers;
    }
}
