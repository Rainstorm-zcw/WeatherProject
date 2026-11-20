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
import org.example.config.WeatherVariableListDeserializer;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Open-Meteo 逐小时天气查询参数。
 * <p>
 * 既可以作为 GET 请求的查询参数（{@code @ModelAttribute}），
 * 也可以作为 POST 请求的 JSON 请求体（{@code @RequestBody}）。
 *
 * @author windy
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Open-Meteo 逐小时天气查询参数")
public class OpenMeteoParam {

    /**
     * 纬度，不传时使用配置中的默认纬度。
     */
    @DecimalMin(value = "-90.0", message = "纬度必须在 -90 ~ 90 之间")
    @DecimalMax(value = "90.0", message = "纬度必须在 -90 ~ 90 之间")
    @Schema(description = "纬度，不传时使用配置的默认值", example = "39.9042")
    private Double latitude;

    /**
     * 经度，不传时使用配置中的默认经度。
     */
    @DecimalMin(value = "-180.0", message = "经度必须在 -180 ~ 180 之间")
    @DecimalMax(value = "180.0", message = "经度必须在 -180 ~ 180 之间")
    @Schema(description = "经度，不传时使用配置的默认值", example = "116.4074")
    private Double longitude;

    /**
     * 需要获取的小时级天气变量，默认 10 项全取。
     */
    @JsonDeserialize(using = WeatherVariableListDeserializer.class)
    @Builder.Default
    @Schema(description = "小时级天气变量，默认取全部 10 项；GET 请求可用逗号分隔，例如 "
            + "temperature_2m,wind_speed_10m",
            example = "temperature_2m,wind_speed_10m,wind_direction_10m,shortwave_radiation,"
                    + "cloud_cover,surface_pressure,relative_humidity_2m,dew_point_2m,precipitation,snowfall")
    private List<WeatherVariable> hourly = new ArrayList<>(WeatherVariable.DEFAULT_VARIABLES);

    /**
     * 时区，决定 hourly.time 的本地时间口径。
     */
    @Size(max = 64, message = "时区名称长度不能超过 64")
    @Schema(description = "时区，例如 Asia/Shanghai；不传时使用配置的默认时区", example = "Asia/Shanghai")
    private String timezone;

    /**
     * 历史天数，与 startDate/endDate 互斥。
     */
    @Min(value = 0, message = "pastDays 不能小于 0")
    @Max(value = 92, message = "pastDays 不能大于 92")
    @Schema(description = "向前追溯的天数（0 ~ 92），与 startDate/endDate 互斥", example = "0")
    private Integer pastDays;

    /**
     * 预报天数，与 startDate/endDate 互斥。
     */
    @Min(value = 0, message = "forecastDays 不能小于 0")
    @Max(value = 16, message = "forecastDays 不能大于 16")
    @Schema(description = "预报天数（0 ~ 16），与 startDate/endDate 互斥", example = "7")
    private Integer forecastDays;

    /**
     * 查询开始日期（含），必须与 endDate 同时指定。
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "开始日期，格式 yyyy-MM-dd，必须与 endDate 同时指定", example = "2026-11-20")
    private LocalDate startDate;

    /**
     * 查询结束日期（含），必须与 startDate 同时指定。
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "结束日期，格式 yyyy-MM-dd，必须与 startDate 同时指定", example = "2026-11-21")
    private LocalDate endDate;

    /**
     * 温度单位，默认摄氏度。
     */
    @Schema(description = "温度单位，默认 celsius", example = "celsius",
            allowableValues = {"celsius", "fahrenheit"})
    private TemperatureUnit temperatureUnit;

    /**
     * 风速单位，默认 km/h。
     */
    @Schema(description = "风速单位，默认 kmh", example = "kmh",
            allowableValues = {"kmh", "ms", "mph", "kn"})
    private WindSpeedUnit windSpeedUnit;

    /**
     * 降水单位，默认毫米。
     */
    @Schema(description = "降水单位，默认 mm", example = "mm",
            allowableValues = {"mm", "inch"})
    private PrecipitationUnit precipitationUnit;

    /**
     * Open-Meteo 要求 start_date 与 end_date 必须成对出现。
     *
     * @return 日期区间是否合法
     */
    @JsonIgnore
    @Schema(hidden = true)
    @AssertTrue(message = "startDate 与 endDate 必须同时指定")
    public boolean isDateRangeValid() {
        return (startDate == null) == (endDate == null);
    }

    /**
     * 结束日期不能早于开始日期。
     *
     * @return 日期顺序是否合法
     */
    @JsonIgnore
    @Schema(hidden = true)
    @AssertTrue(message = "endDate 不能早于 startDate")
    public boolean isDateOrderValid() {
        return startDate == null || endDate == null || !endDate.isBefore(startDate);
    }
}
