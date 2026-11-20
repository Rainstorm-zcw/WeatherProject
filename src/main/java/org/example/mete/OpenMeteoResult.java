package org.example.mete;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Open-Meteo {@code /v1/forecast} 接口的原始响应。
 * <p>
 * 字段名与 API 返回的 snake_case 一一对应，未使用的字段会被忽略，
 * 便于后续 API 升级时保持兼容。
 *
 * @author windy
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Open-Meteo /v1/forecast 原始响应")
public class OpenMeteoResult {

    @Schema(description = "实际返回数据对应的纬度（API 会对坐标做网格就近取整）", example = "39.89455")
    private Double latitude;

    @Schema(description = "实际返回数据对应的经度", example = "116.35983")
    private Double longitude;

    @Schema(description = "接口生成耗时（毫秒）", example = "0.049")
    @JsonProperty("generationtime_ms")
    private Double generationTimeMs;

    @Schema(description = "时区偏移秒数", example = "28800")
    @JsonProperty("utc_offset_seconds")
    private Integer utcOffsetSeconds;

    @Schema(description = "时区", example = "Asia/Shanghai")
    private String timezone;

    @Schema(description = "时区缩写", example = "CST")
    @JsonProperty("timezone_abbreviation")
    private String timezoneAbbreviation;

    @Schema(description = "海拔（米）", example = "49.0")
    private Double elevation;

    @Schema(description = "小时级数据单位")
    @JsonProperty("hourly_units")
    private HourlyUnits hourlyUnits;

    @Schema(description = "小时级数据")
    private HourlyData hourly;
}
