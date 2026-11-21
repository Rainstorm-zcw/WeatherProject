package org.example.windy.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Windy point forecast（v3.0）响应。
 * <p>
 * 只声明用到的字段，其余字段由 {@code @JsonIgnoreProperties} 忽略。
 *
 * @author windy
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WindyForecastResponse {

    private Header header;

    private ForecastData data;

    private Meteogram meteogram;

    private Celestial celestial;

    private List<Map<String, Object>> summary;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Header {

        private String model;

        private String refTime;

        private Double utcOffset;

        private Integer daysAvail;

        private Double elevation;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Celestial {

        @JsonProperty("TZname")
        private String tzName;

        @JsonProperty("TZoffset")
        private Double tzOffset;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ForecastData {

        /**
         * 毫秒时间戳（UTC）。
         */
        private List<Long> ts;

        private List<Integer> hour;

        /**
         * 温度，单位 K。
         */
        private List<Double> temperature;

        /**
         * 风速，单位 m/s。
         */
        private List<Double> wind;

        /**
         * 风向，单位 °。
         */
        @JsonProperty("windDir")
        private List<Double> windDir;

        /**
         * 气压，单位 Pa。
         */
        private List<Double> pressure;

        /**
         * 降水量，单位 mm。
         */
        private List<Double> precipAmount;

        /**
         * 降雪量，单位 cm。
         */
        private List<Double> precipSnowAmount;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Meteogram {

        private List<Long> ts;

        /**
         * 露点温度，单位 K。
         */
        private List<Double> dewPoint;

        /**
         * 分层云量，key 形如 cloud-1000h / cloud-500h，value 为 0~100 的百分比数组。
         */
        @JsonIgnore
        private final Map<String, List<Double>> cloudLayers = new LinkedHashMap<>();

        @JsonAnySetter
        public void addLayer(String name, Object value) {
            if (name.startsWith("cloud-") && value instanceof List<?> list) {
                cloudLayers.put(name, list.stream()
                        .map(item -> item instanceof Number number ? number.doubleValue() : null)
                        .collect(Collectors.toList()));
            }
        }
    }
}
