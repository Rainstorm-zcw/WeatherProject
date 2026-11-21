package org.example.windy.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Windy 登录请求体（POST /api/login 的 JSON body）。
 * <p>
 * {@code deviceId} 就是预登录时生成的 {@code uid}，Windy 用它把 token 和客户端设备绑定。
 *
 * @author windy
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Windy 登录请求体")
public class WindyLoginParam {

    @Schema(description = "设备 ID，与预登录的 uid 相同", example = "b0067c3d-bff2-97a5-8bda-fabf7529bb5d")
    private String deviceId;

    @Schema(description = "Windy 账号", example = "spot@cpirhzl.com")
    private String email;

    @Schema(description = "Windy 密码")
    private String password;

    @Builder.Default
    @Schema(description = "平台，固定 desktop", example = "desktop")
    private String platform = "desktop";

    @Builder.Default
    @Schema(description = "登录后跳转目标，固定 index", example = "index")
    private String target = "index";
}
