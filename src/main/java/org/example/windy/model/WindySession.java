package org.example.windy.model;

import io.swagger.v3.oas.annotations.media.Schema;import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Windy 会话：预登录 + 登录之后得到的 uid / token。
 *
 * @author windy
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Windy 会话信息")
public class WindySession {

    @Schema(description = "设备 ID，随机 UUID；登录时的 deviceId 与它相同",
            example = "b0067c3d-bff2-97a5-8bda-fabf7529bb5d")
    private String uid;

    @Schema(description = "Windy 访问 token（token2）")
    private String token;

    @Schema(description = "是否已登录", example = "true")
    private Boolean auth;

    @Schema(description = "是否 premium 账号", example = "true")
    private Boolean premium;

    @Schema(description = "账号邮箱", example = "spot@cpirhzl.com")
    private String email;

    @Schema(description = "账号用户名", example = "融和")
    private String username;

    @Schema(description = "账号 ID", example = "18213719")
    private Long userId;

    @Schema(description = "token 过期时间")
    private Instant tokenExpiresAt;
}
