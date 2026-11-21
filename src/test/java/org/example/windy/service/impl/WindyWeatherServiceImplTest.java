package org.example.windy.service.impl;

import org.example.mete.HourlyWeather;
import org.example.service.OpenMeteoService;
import org.example.windy.client.WindyApiClient;
import org.example.windy.config.WindyProperties;
import org.example.windy.model.WindyForecastParam;
import org.example.windy.model.WindyForecastResponse;
import org.example.windy.model.WindyHourlyWeather;
import org.example.windy.model.WindySession;
import org.example.windy.service.WindySessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * {@link WindyWeatherServiceImpl} 单元测试，不依赖真实网络。
 *
 * @author windy
 */
@ExtendWith(MockitoExtension.class)
class WindyWeatherServiceImplTest {

    /**
     * 2026-11-21T00:00:00Z（Asia/Shanghai 为 08:00）。
     */
    private static final long TIMESTAMP = 1795219200000L;

    @Mock
    private WindyApiClient windyApiClient;

    @Mock
    private WindySessionManager windySessionManager;

    @Mock
    private OpenMeteoService openMeteoService;

    private WindyWeatherServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new WindyWeatherServiceImpl(windyApiClient, windySessionManager,
                new WindyProperties(), openMeteoService);
    }

    @Test
    void shouldMapWindyDataIntoTenValues() {
        when(windySessionManager.getSession()).thenReturn(WindySession.builder()
                .uid("test-uid")
                .token("test-token")
                .build());
        when(windyApiClient.forecast(any(WindyForecastParam.class))).thenReturn(mockResponse());
        when(openMeteoService.fetchHourly(any())).thenReturn(List.of(HourlyWeather.builder()
                .time(LocalDateTime.of(2026, 11, 21, 0, 0))
                .shortwaveRadiation(120.0)
                .build()));

        List<WindyHourlyWeather> rows = service.fetchHourly(WindyForecastParam.builder()
                .latitude(31.232)
                .longitude(121.469)
                .days(1)
                .build());

        assertThat(rows).hasSize(1);
        WindyHourlyWeather row = rows.get(0);
        assertThat(row.getTime()).isEqualTo(LocalDateTime.of(2026, 11, 21, 8, 0));
        assertThat(row.getTemperature()).isEqualTo(2.0);
        assertThat(row.getWindSpeed()).isEqualTo(7.2);
        assertThat(row.getWindDirection()).isEqualTo(180.0);
        assertThat(row.getShortwaveRadiation()).isEqualTo(120.0);
        assertThat(row.getRadiationSource()).isEqualTo("OPEN_METEO");
        assertThat(row.getCloudCover()).isEqualTo(75.0);
        assertThat(row.getSurfacePressure()).isCloseTo(1007.0, within(0.5));
        assertThat(row.getRelativeHumidity()).isCloseTo(93.07, within(0.5));
        assertThat(row.getDewPoint()).isEqualTo(1.0);
        assertThat(row.getPrecipitation()).isEqualTo(0.2);
        assertThat(row.getSnowfall()).isEqualTo(0.5);
    }

    private WindyForecastResponse mockResponse() {
        WindyForecastResponse.Header header = new WindyForecastResponse.Header();
        header.setModel("ICON");
        header.setElevation(52.0);

        WindyForecastResponse.ForecastData data = new WindyForecastResponse.ForecastData();
        data.setTs(List.of(TIMESTAMP));
        data.setTemperature(List.of(275.15));
        data.setWind(List.of(2.0));
        data.setWindDir(List.of(180.0));
        data.setPressure(List.of(101325.0));
        data.setPrecipAmount(List.of(0.2));
        data.setPrecipSnowAmount(List.of(0.5));

        WindyForecastResponse.Meteogram meteogram = new WindyForecastResponse.Meteogram();
        meteogram.setTs(List.of(TIMESTAMP));
        meteogram.setDewPoint(List.of(274.15));
        meteogram.getCloudLayers().put("cloud-1000h", List.of(50.0));
        meteogram.getCloudLayers().put("cloud-500h", List.of(50.0));

        WindyForecastResponse.Celestial celestial = new WindyForecastResponse.Celestial();
        celestial.setTzName("Asia/Shanghai");

        WindyForecastResponse response = new WindyForecastResponse();
        response.setHeader(header);
        response.setData(data);
        response.setMeteogram(meteogram);
        response.setCelestial(celestial);
        return response;
    }
}
