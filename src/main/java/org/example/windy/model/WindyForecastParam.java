package org.example.windy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Windy point forecast 查询参数。
 * <p>
 * 对应 {@code /forecast/point/{model}/v3.0/{lat}/{lon}} 的查询参数；
 * 其中 {@code uid}/{@code token2} 不传时由服务端自动使用当前 Windy 会话。
 *
 * @author windy
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Windy point forecast 查询参数")
public class WindyForecastParam {

    @NotNull(message = "纬度不能为空")
    @DecimalMin(value = "-90.0", message = "纬度必须在 -90 ~ 90 之间")
    @DecimalMax(value = "90.0", message = "纬度必须在 -90 ~ 90 之间")
    @Schema(description = "纬度", example = "31.232", required = true)
    private Double latitude;

    @NotNull(message = "经度不能为空")
    @DecimalMin(value = "-180.0", message = "经度必须在 -180 ~ 180 之间")
    @DecimalMax(value = "180.0", message = "经度必须在 -180 ~ 180 之间")
    @Schema(description = "经度", example = "121.469", required = true)
    private Double longitude;

    @Builder.Default
    @Schema(description = "天气模型，默认 icon", example = "icon")
    private String model = "icon";

    @Builder.Default
    @Min(value = 1, message = "预报天数不能小于 1")
    @Max(value = 7, message = "预报天数不能大于 7")
    @Schema(description = "预报天数（1 ~ 7），默认 5", example = "5")
    private Integer days = 5;

    @Builder.Default
    @Min(value = 1, message = "步长不能小于 1")
    @Max(value = 3, message = "步长不能大于 3")
    @Schema(description = "时间步长：1=逐小时（premium），3=3 小时；默认 1", example = "1")
    private Integer step = 1;

    @Builder.Default
    @Schema(description = "数据来源，固定 detail", example = "detail")
    private String source = "detail";

    @Builder.Default
    @Schema(description = "需要返回的数据块", example = "header,celestial,summary,meteogram")
    private String include = "header,celestial,summary,meteogram";

    @Schema(description = "时区；不传时优先使用 Windy 返回的地点时区，其次使用配置默认值",
            example = "Asia/Shanghai")
    private String timezone;

    @Schema(description = "模式起报时间，例如 2026-11-20T12:00:00Z；不传时由 Windy 使用最新起报",
            example = "2026-11-20T12:00:00Z")
    private String refTime;

    @Builder.Default
    @Schema(description = "客户端版本", example = "51.4.4")
    private String version = "51.4.4";

    @Builder.Default
    @Schema(description = "premium 参数", example = "0")
    private Integer pr = 0;

    @Builder.Default
    @Schema(description = "source code 参数", example = "11")
    private Integer sc = 11;

    @Builder.Default
    @Schema(description = "point of capture 参数", example = "1273")
    private Integer poc = 1273;

    @Schema(description = "设备 ID；不传时使用当前会话 uid", example = "b0067c3d-bff2-97a5-8bda-fabf7529bb5d")
    private String uid;

    @Schema(description = "访问 token；不传时使用当前会话 token")
    private String token2;
}
