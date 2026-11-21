package org.example.windy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Duration;

/**
 * Windy 平台相关配置，对应 application.yml 中的 {@code windy} 前缀。
 *
 * @author windy
 */
@Data
@Validated
@ConfigurationProperties(prefix = "windy")
public class WindyProperties {

    /**
     * 账号服务地址（预登录 / 登录）。
     */
    @NotBlank
    private String accountBaseUrl = "https://account.windy.com";

    /**
     * 数据服务地址（point forecast）。
     */
    @NotBlank
    private String nodeBaseUrl = "https://node.windy.com";

    /**
     * 客户端版本号，Windy 前端会带这个参数。
     */
    @NotBlank
    private String version = "51.4.4";

    /**
     * 预登录国家参数，当前固定 my。
     */
    @NotBlank
    private String country = "my";

    /**
     * 默认天气模型。
     */
    @NotBlank
    private String defaultModel = "icon";

    /**
     * 默认预报天数（Windy v3.0 最多 7 天）。
     */
    @Min(value = 1, message = "默认预报天数不能小于 1")
    @Max(value = 7, message = "默认预报天数不能大于 7")
    private Integer defaultDays = 5;

    /**
     * 默认时间步长：1 表示逐小时（需要 premium 账号），3 表示 3 小时。
     */
    @Min(value = 1, message = "默认步长不能小于 1")
    @Max(value = 3, message = "默认步长不能大于 3")
    private Integer defaultStep = 1;

    /**
     * 默认时区，用于把毫秒时间戳转成本地时间。
     */
    @NotBlank
    private String timezone = "Asia/Shanghai";

    @NotNull
    private Duration connectTimeout = Duration.ofSeconds(5);

    @NotNull
    private Duration readTimeout = Duration.ofSeconds(30);

    /**
     * token 到期前多久主动刷新。
     */
    @NotNull
    private Duration refreshBefore = Duration.ofMinutes(10);

    /**
     * 是否自动登录（登录后才有 premium 的逐小时数据）。
     */
    private boolean loginEnabled = true;

    /**
     * Windy 账号。
     */
    private String email;

    private String password;

    /**
     * 辐照数据回退配置。
     */
    private Radiation radiation = new Radiation();

    @Data
    public static class Radiation {

        /**
         * Windy point API 不提供辐照，开启后用 Open-Meteo 的 shortwave_radiation 补齐。
         */
        private boolean openMeteoFallback = true;
    }
}
