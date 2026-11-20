package org.example.mete;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * 风速单位，对应 Open-Meteo {@code wind_speed_unit} 参数。
 *
 * @author windy
 */
@Getter
@RequiredArgsConstructor
@Schema(description = "风速单位")
public enum WindSpeedUnit {

    KMH("kmh"),
    MS("ms"),
    MPH("mph"),
    KNOTS("kn");

    private final String value;

    @JsonCreator
    public static WindSpeedUnit fromValue(String value) {
        return Arrays.stream(values())
                .filter(unit -> unit.value.equalsIgnoreCase(value) || unit.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持的风速单位: " + value));
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
