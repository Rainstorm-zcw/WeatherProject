package org.example.service.impl;

import org.example.config.OpenMeteoProperties;
import org.example.mete.HourlyWeather;
import org.example.mete.OpenMeteoParam;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * {@link OpenMeteoServiceImpl} 单元测试，不依赖真实网络。
 *
 * @author windy
 */
class OpenMeteoServiceImplTest {

    private static final String RESPONSE_JSON = """
            {
              "latitude": 39.89455,
              "longitude": 116.35983,
              "generationtime_ms": 0.1,
              "utc_offset_seconds": 28800,
              "timezone": "Asia/Shanghai",
              "timezone_abbreviation": "CST",
              "elevation": 49.0,
              "hourly_units": {
                "time": "iso8601",
                "temperature_2m": "°C",
                "wind_speed_10m": "km/h",
                "wind_direction_10m": "°",
                "shortwave_radiation": "W/m²",
                "cloud_cover": "%",
                "surface_pressure": "hPa",
                "relative_humidity_2m": "%",
                "dew_point_2m": "°C",
                "precipitation": "mm",
                "snowfall": "cm"
              },
              "hourly": {
                "time": ["2026-11-20T00:00", "2026-11-20T01:00"],
                "temperature_2m": [1.5, 2.5],
                "wind_speed_10m": [10.0, 11.0],
                "wind_direction_10m": [180.0, 190.0],
                "shortwave_radiation": [0.0, 30.0],
                "cloud_cover": [25.0, 50.0],
                "surface_pressure": [1010.0, 1009.5],
                "relative_humidity_2m": [60.0, 62.0],
                "dew_point_2m": [-5.0, -4.0],
                "precipitation": [0.0, 0.2],
                "snowfall": [0.0, 0.0]
              }
            }
            """;

    private MockRestServiceServer server;

    private OpenMeteoServiceImpl service;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        server = MockRestServiceServer.bindTo(restTemplate).build();
        service = new OpenMeteoServiceImpl(restTemplate, new OpenMeteoProperties());
    }

    @Test
    void shouldFlattenHourlyArraysIntoRows() {
        server.expect(once(), requestTo(containsString("/v1/forecast")))
                .andExpect(method(HttpMethod.GET))
                .andExpect(requestTo(containsString("latitude=39.9042")))
                .andExpect(requestTo(containsString("longitude=116.4074")))
                .andExpect(requestTo(containsString("models=icon_global")))
                .andExpect(requestTo(containsString("temperature_2m")))
                .andExpect(requestTo(containsString("wind_speed_10m")))
                .andExpect(requestTo(containsString("shortwave_radiation")))
                .andExpect(requestTo(containsString("cloud_cover")))
                .andExpect(requestTo(containsString("surface_pressure")))
                .andExpect(requestTo(containsString("relative_humidity_2m")))
                .andExpect(requestTo(containsString("dew_point_2m")))
                .andExpect(requestTo(containsString("precipitation")))
                .andExpect(requestTo(containsString("snowfall")))
                .andRespond(withSuccess(RESPONSE_JSON, MediaType.APPLICATION_JSON));

        List<HourlyWeather> rows = service.fetchHourly(OpenMeteoParam.builder()
                .models("icon_global")
                .build());

        assertThat(rows).hasSize(2);
        HourlyWeather first = rows.get(0);
        assertThat(first.getTime()).isEqualTo(LocalDateTime.of(2026, 11, 20, 0, 0));
        assertThat(first.getTemperature()).isEqualTo(1.5);
        assertThat(first.getWindSpeed()).isEqualTo(10.0);
        assertThat(first.getWindDirection()).isEqualTo(180.0);
        assertThat(first.getShortwaveRadiation()).isZero();
        assertThat(first.getCloudCover()).isEqualTo(25.0);
        assertThat(first.getSurfacePressure()).isEqualTo(1010.0);
        assertThat(first.getRelativeHumidity()).isEqualTo(60.0);
        assertThat(first.getDewPoint()).isEqualTo(-5.0);
        assertThat(first.getPrecipitation()).isZero();
        assertThat(first.getSnowfall()).isZero();
        assertThat(rows.get(1).getTemperature()).isEqualTo(2.5);
        server.verify();
    }

    @Test
    void shouldRejectOneSidedDateRange() {
        OpenMeteoParam param = OpenMeteoParam.builder()
                .latitude(31.23)
                .longitude(121.47)
                .startDate(java.time.LocalDate.of(2026, 11, 20))
                .build();

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> service.fetchForecast(param))
                .isInstanceOf(org.example.exception.OpenMeteoException.class)
                .hasMessageContaining("startDate 与 endDate 必须同时指定");
    }
}
