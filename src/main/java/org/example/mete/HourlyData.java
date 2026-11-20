package org.example.mete;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Open-Meteo 响应中的 {@code hourly} 节点，按数组并行存放每个变量。
 * <p>
 * 数组下标一一对应，例如 {@code time[0]} 对应 {@code temperature_2m[0]}。
 *
 * @author windy
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "小时级数据（并行数组）")
public class HourlyData {

    @Schema(description = "时间数组，ISO8601 本地时间", example = "[\"2026-11-20T00:00\",\"2026-11-20T01:00\"]")
    private List<String> time;

    @Schema(description = "2 米温度数组，单位 °C", example = "[1.5,2.5]")
    @JsonProperty("temperature_2m")
    private List<Double> temperature2m;

    @Schema(description = "10 米风速数组，单位 km/h", example = "[10.0,11.0]")
    @JsonProperty("wind_speed_10m")
    private List<Double> windSpeed10m;

    @Schema(description = "10 米风向数组，单位 °", example = "[180.0,190.0]")
    @JsonProperty("wind_direction_10m")
    private List<Double> windDirection10m;

    @Schema(description = "短波辐照数组，单位 W/m²", example = "[0.0,30.0]")
    @JsonProperty("shortwave_radiation")
    private List<Double> shortwaveRadiation;

    @Schema(description = "云量数组，单位 %", example = "[25.0,50.0]")
    @JsonProperty("cloud_cover")
    private List<Double> cloudCover;

    @Schema(description = "地面气压数组，单位 hPa", example = "[1010.0,1009.5]")
    @JsonProperty("surface_pressure")
    private List<Double> surfacePressure;

    @Schema(description = "相对湿度数组，单位 %", example = "[60.0,62.0]")
    @JsonProperty("relative_humidity_2m")
    private List<Double> relativeHumidity2m;

    @Schema(description = "露点温度数组，单位 °C", example = "[-5.0,-4.0]")
    @JsonProperty("dew_point_2m")
    private List<Double> dewPoint2m;

    @Schema(description = "降水量数组，单位 mm", example = "[0.0,0.2]")
    private List<Double> precipitation;

    @Schema(description = "降雪量数组，单位 cm", example = "[0.0,0.0]")
    private List<Double> snowfall;
}
