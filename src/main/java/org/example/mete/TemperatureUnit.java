package org.example.mete;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * 温度单位，对应 Open-Meteo {@code temperature_unit} 参数。
 *
 * @author windy
 */
@Getter
@RequiredArgsConstructor
@Schema(description = "温度单位")
public enum TemperatureUnit {

    CELSIUS("celsius"),
    FAHRENHEIT("fahrenheit");

    private final String value;

    @JsonCreator
    public static TemperatureUnit fromValue(String value) {
        return Arrays.stream(values())
                .filter(unit -> unit.value.equalsIgnoreCase(value) || unit.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持的温度单位: " + value));
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
