package org.example.mete;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Open-Meteo 多点查询中单个坐标点的结果。
 *
 * @author windy
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Open-Meteo 单个坐标点的逐小时天气")
public class OpenMeteoLocationResult {

    @Schema(description = "实际返回数据对应的纬度（API 会做网格就近取整）", example = "39.875")
    private Double latitude;

    @Schema(description = "实际返回数据对应的经度", example = "116.375")
    private Double longitude;

    @Schema(description = "海拔（米）", example = "50.0")
    private Double elevation;

    @Schema(description = "时区", example = "Asia/Shanghai")
    private String timezone;

    @Schema(description = "逐小时天气数据")
    private List<HourlyWeather> hourly;
}
