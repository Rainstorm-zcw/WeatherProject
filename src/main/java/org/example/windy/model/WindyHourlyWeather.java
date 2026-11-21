package org.example.windy.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Windy 逐小时天气数据（10 项要素）。
 *
 * @author windy
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Windy 逐小时天气数据")
public class WindyHourlyWeather {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Schema(description = "时间（请求时区下的本地时间）", example = "2026-11-21 08:00")
    private LocalDateTime time;

    @Schema(description = "温度，单位 °C", example = "2.1")
    private Double temperature;

    @Schema(description = "风速，单位 km/h", example = "7.2")
    private Double windSpeed;

    @Schema(description = "风向，单位 °（0 北风 / 90 东风 / 180 南风 / 270 西风）", example = "180.0")
    private Double windDirection;

    @Schema(description = "短波辐照，单位 W/m²（Windy point API 无此字段，默认由 Open-Meteo 补齐）",
            example = "0.0")
    private Double shortwaveRadiation;

    @Schema(description = "云量，单位 %（由 Windy 分层云量按随机重叠法合成）", example = "75.0")
    private Double cloudCover;

    @Schema(description = "地面气压，单位 hPa（由 Windy 海平面气压按海拔换算）", example = "1013.25")
    private Double surfacePressure;

    @Schema(description = "相对湿度，单位 %（由温度与露点计算）", example = "93.8")
    private Double relativeHumidity;

    @Schema(description = "露点温度，单位 °C", example = "1.0")
    private Double dewPoint;

    @Schema(description = "降水量，单位 mm", example = "0.2")
    private Double precipitation;

    @Schema(description = "降雪量，单位 cm", example = "0.0")
    private Double snowfall;

    @Schema(description = "辐照数据来源：OPEN_METEO 表示 Windy 无该字段时由 Open-Meteo 补齐",
            example = "OPEN_METEO")
    private String radiationSource;
}
