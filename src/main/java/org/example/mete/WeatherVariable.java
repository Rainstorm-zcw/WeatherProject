package org.example.mete;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * Open-Meteo 小时级天气变量。
 * <p>
 * 每个枚举值都对应 API 中 {@code hourly} 参数的一个变量名，并附带中文含义与默认单位，
 * 方便接口文档展示以及后续做单位换算。
 *
 * @author windy
 */
@Getter
@RequiredArgsConstructor
@Schema(description = "Open-Meteo 小时级天气变量")
public enum WeatherVariable {

    /**
     * 2 米温度，单位 °C。
     */
    TEMPERATURE_2M("temperature_2m", "温度", "°C"),

    /**
     * 10 米风速，单位 km/h。
     */
    WIND_SPEED_10M("wind_speed_10m", "风速", "km/h"),

    /**
     * 10 米风向，单位 °（0 表示北风，90 表示东风）。
     */
    WIND_DIRECTION_10M("wind_direction_10m", "风向", "°"),

    /**
     * 太阳短波辐射（水平面总辐照度），单位 W/m²。
     */
    SHORTWAVE_RADIATION("shortwave_radiation", "辐照", "W/m²"),

    /**
     * 总云量，单位 %。
     */
    CLOUD_COVER("cloud_cover", "云量", "%"),

    /**
     * 地面气压，单位 hPa。
     */
    SURFACE_PRESSURE("surface_pressure", "气压", "hPa"),

    /**
     * 2 米相对湿度，单位 %。
     */
    RELATIVE_HUMIDITY_2M("relative_humidity_2m", "相对湿度", "%"),

    /**
     * 2 米露点温度，单位 °C。
     */
    DEW_POINT_2M("dew_point_2m", "露点温度", "°C"),

    /**
     * 前 1 小时降水量，单位 mm。
     */
    PRECIPITATION("precipitation", "降水量", "mm"),

    /**
     * 前 1 小时降雪量，单位 cm。
     */
    SNOWFALL("snowfall", "降雪量", "cm");

    /**
     * 默认查询的 10 项天气变量，顺序与需求一致。
     */
    public static final List<WeatherVariable> DEFAULT_VARIABLES = List.of(values());

    /**
     * Open-Meteo API 中的变量名，例如 {@code temperature_2m}。
     */
    private final String apiName;

    /**
     * 中文名称。
     */
    private final String description;

    /**
     * 默认单位。
     */
    private final String unit;

    /**
     * 支持 {@code temperature_2m} 和 {@code TEMPERATURE_2M} 两种写法（忽略大小写）。
     *
     * @param apiName API 变量名或枚举名
     * @return 对应的枚举值
     */
    @JsonCreator
    public static WeatherVariable fromApiName(String apiName) {
        return Arrays.stream(values())
                .filter(variable -> variable.apiName.equalsIgnoreCase(apiName)
                        || variable.name().equalsIgnoreCase(apiName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持的 Open-Meteo 天气变量: " + apiName));
    }

    /**
     * 供自定义反序列化器使用：只接受 API 变量名，例如 {@code temperature_2m}。
     *
     * @param value API 变量名
     * @return 对应的枚举值
     */
    public static WeatherVariable fromString(String value) {
        return Arrays.stream(values())
                .filter(variable -> variable.apiName.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("未知变量: " + value));
    }

    /**
     * 序列化为 API 变量名，保证接口返回的 JSON 中不会出现枚举常量名。
     *
     * @return API 变量名
     */
    @JsonValue
    public String getApiName() {
        return apiName;
    }
}
