package org.example.mete;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.config.DoubleListDeserializer;
import org.example.config.WeatherVariableListDeserializer;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Open-Meteo 多点逐小时天气查询参数。
 * <p>
 * 经纬度支持三种写法：
 * <ul>
 *     <li>JSON 数组：{@code "latitude": [39.9, 31.2]}</li>
 *     <li>逗号字符串：{@code "latitude": "39.9,31.2"}（GET 参数同样支持）</li>
 *     <li>单个数字：{@code "latitude": 39.9}</li>
 * </ul>
 * 两个列表长度必须一致。
 *
 * @author windy
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Open-Meteo 多点逐小时天气查询参数")
public class OpenMeteoBatchParam {

    /**
     * 纬度列表。
     */
    @NotEmpty(message = "纬度不能为空")
    @Size(max = 100, message = "单次最多查询 100 个纬度")
    @JsonDeserialize(using = DoubleListDeserializer.class)
    @Schema(description = "纬度列表，支持数组或逗号分隔字符串", example = "39.9042,31.2304")
    private List<@DecimalMin(value = "-90.0", message = "纬度必须在 -90 ~ 90 之间")
            @DecimalMax(value = "90.0", message = "纬度必须在 -90 ~ 90 之间") Double> latitude
            = new ArrayList<>();

    /**
     * 经度列表。
     */
    @NotEmpty(message = "经度不能为空")
    @Size(max = 100, message = "单次最多查询 100 个经度")
    @JsonDeserialize(using = DoubleListDeserializer.class)
    @Schema(description = "经度列表，支持数组或逗号分隔字符串", example = "116.4074,121.4737")
    private List<@DecimalMin(value = "-180.0", message = "经度必须在 -180 ~ 180 之间")
            @DecimalMax(value = "180.0", message = "经度必须在 -180 ~ 180 之间") Double> longitude
            = new ArrayList<>();

    /**
     * 小时级天气变量，默认 10 项全取。
     */
    @Builder.Default
    @JsonDeserialize(using = WeatherVariableListDeserializer.class)
    @Schema(description = "小时级天气变量，默认取全部 10 项",
            example = "temperature_2m,wind_speed_10m,wind_direction_10m,shortwave_radiation,"
                    + "cloud_cover,surface_pressure,relative_humidity_2m,dew_point_2m,precipitation,snowfall")
    private List<WeatherVariable> hourly = new ArrayList<>(WeatherVariable.DEFAULT_VARIABLES);

    @Size(max = 64, message = "时区名称长度不能超过 64")
    @Schema(description = "时区，不传时使用配置的默认时区", example = "Asia/Shanghai")
    private String timezone;

    @Min(value = 0, message = "pastDays 不能小于 0")
    @Max(value = 92, message = "pastDays 不能大于 92")
    @Schema(description = "向前追溯的天数（0 ~ 92），与 startDate/endDate 互斥", example = "0")
    private Integer pastDays;

    @Min(value = 0, message = "forecastDays 不能小于 0")
    @Max(value = 16, message = "forecastDays 不能大于 16")
    @Schema(description = "预报天数（0 ~ 16），与 startDate/endDate 互斥", example = "7")
    private Integer forecastDays;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "开始日期，格式 yyyy-MM-dd，必须与 endDate 同时指定", example = "2026-11-20")
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "结束日期，格式 yyyy-MM-dd，必须与 startDate 同时指定", example = "2026-11-21")
    private LocalDate endDate;

    @Schema(description = "温度单位，默认 celsius", example = "celsius",
            allowableValues = {"celsius", "fahrenheit"})
    private TemperatureUnit temperatureUnit;

    @Schema(description = "风速单位，默认 kmh", example = "kmh",
            allowableValues = {"kmh", "ms", "mph", "kn"})
    private WindSpeedUnit windSpeedUnit;

    @Schema(description = "降水单位，默认 mm", example = "mm",
            allowableValues = {"mm", "inch"})
    private PrecipitationUnit precipitationUnit;

    @Size(max = 200, message = "models 长度不能超过 200")
    @Schema(description = "Open-Meteo 模型，多个用逗号分隔；不传时使用 best_match", example = "icon_global")
    private String models;

    @JsonIgnore
    @Schema(hidden = true)
    @AssertTrue(message = "latitude 与 longitude 数量必须一致")
    public boolean isCoordinateSizeValid() {
        int latitudeSize = latitude == null ? 0 : latitude.size();
        int longitudeSize = longitude == null ? 0 : longitude.size();
        return latitudeSize == longitudeSize;
    }

    @JsonIgnore
    @Schema(hidden = true)
    @AssertTrue(message = "startDate 与 endDate 必须同时指定")
    public boolean isDateRangeValid() {
        return (startDate == null) == (endDate == null);
    }

    @JsonIgnore
    @Schema(hidden = true)
    @AssertTrue(message = "endDate 不能早于 startDate")
    public boolean isDateOrderValid() {
        return startDate == null || endDate == null || !endDate.isBefore(startDate);
    }
}
