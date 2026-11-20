package org.example.mete;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 逐小时天气数据（扁平化后的业务对象）。
 * <p>
 * 由 {@link HourlyData} 的并行数组按下标合并而成，一行代表一个小时。
 *
 * @author windy
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "逐小时天气数据")
public class HourlyWeather {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Schema(description = "时间（请求时区下的本地时间）", example = "2026-11-20 00:00")
    private LocalDateTime time;

    @Schema(description = "温度，默认单位 °C", example = "1.5")
    private Double temperature;

    @Schema(description = "风速，默认单位 km/h", example = "10.0")
    private Double windSpeed;

    @Schema(description = "风向，单位 °（0 北风 / 90 东风 / 180 南风 / 270 西风）", example = "180.0")
    private Double windDirection;

    @Schema(description = "短波辐照（水平面总辐照度），单位 W/m²", example = "0.0")
    private Double shortwaveRadiation;

    @Schema(description = "云量，单位 %", example = "25.0")
    private Double cloudCover;

    @Schema(description = "地面气压，单位 hPa", example = "1010.0")
    private Double surfacePressure;

    @Schema(description = "相对湿度，单位 %", example = "60.0")
    private Double relativeHumidity;

    @Schema(description = "露点温度，默认单位 °C", example = "-5.0")
    private Double dewPoint;

    @Schema(description = "前 1 小时降水量，默认单位 mm", example = "0.0")
    private Double precipitation;

    @Schema(description = "前 1 小时降雪量，默认单位 cm", example = "0.0")
    private Double snowfall;
}
