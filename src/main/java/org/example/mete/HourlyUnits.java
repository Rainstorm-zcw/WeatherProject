package org.example.mete;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Open-Meteo 响应中的 {@code hourly_units}，表示各变量单位。
 *
 * @author windy
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "小时级数据单位")
public class HourlyUnits {

    @Schema(description = "时间格式", example = "iso8601")
    private String time;

    @Schema(description = "温度单位", example = "°C")
    @JsonProperty("temperature_2m")
    private String temperature2m;

    @Schema(description = "风速单位", example = "km/h")
    @JsonProperty("wind_speed_10m")
    private String windSpeed10m;

    @Schema(description = "风向单位", example = "°")
    @JsonProperty("wind_direction_10m")
    private String windDirection10m;

    @Schema(description = "辐照单位", example = "W/m²")
    @JsonProperty("shortwave_radiation")
    private String shortwaveRadiation;

    @Schema(description = "云量单位", example = "%")
    @JsonProperty("cloud_cover")
    private String cloudCover;

    @Schema(description = "气压单位", example = "hPa")
    @JsonProperty("surface_pressure")
    private String surfacePressure;

    @Schema(description = "相对湿度单位", example = "%")
    @JsonProperty("relative_humidity_2m")
    private String relativeHumidity2m;

    @Schema(description = "露点温度单位", example = "°C")
    @JsonProperty("dew_point_2m")
    private String dewPoint2m;

    @Schema(description = "降水量单位", example = "mm")
    private String precipitation;

    @Schema(description = "降雪量单位", example = "cm")
    private String snowfall;
}
