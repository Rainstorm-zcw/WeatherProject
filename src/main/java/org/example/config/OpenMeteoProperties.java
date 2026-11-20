package org.example.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Duration;

/**
 * Open-Meteo 相关配置，对应 application.yml 中的 {@code open-meteo} 前缀。
 *
 * @author windy
 */
@Data
@Validated
@ConfigurationProperties(prefix = "open-meteo")
public class OpenMeteoProperties {

    /**
     * Open-Meteo 服务地址，免费版无需 key。
     */
    @NotBlank
    private String baseUrl = "https://api.open-meteo.com";

    /**
     * 默认时区，决定返回的 hourly.time 使用哪个时区的本地时间。
     */
    @NotBlank
    private String timezone = "Asia/Shanghai";

    /**
     * 建立连接超时时间。
     */
    @NotNull
    private Duration connectTimeout = Duration.ofSeconds(5);

    /**
     * 读取响应超时时间。
     */
    @NotNull
    private Duration readTimeout = Duration.ofSeconds(15);

    /**
     * 未传经纬度时使用的默认纬度（默认北京）。
     */
    @DecimalMin(value = "-90.0", message = "默认纬度必须在 -90 ~ 90 之间")
    @DecimalMax(value = "90.0", message = "默认纬度必须在 -90 ~ 90 之间")
    private Double defaultLatitude = 39.9042D;

    /**
     * 未传经纬度时使用的默认经度（默认北京）。
     */
    @DecimalMin(value = "-180.0", message = "默认经度必须在 -180 ~ 180 之间")
    @DecimalMax(value = "180.0", message = "默认经度必须在 -180 ~ 180 之间")
    private Double defaultLongitude = 116.4074D;

    /**
     * 未指定查询区间时，默认预报天数（0 ~ 16）。
     */
    @Min(value = 0, message = "默认预报天数不能小于 0")
    @Max(value = 16, message = "默认预报天数不能大于 16")
    private Integer defaultForecastDays = 7;
}
