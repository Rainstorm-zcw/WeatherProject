package org.example.controller;

import org.example.mete.HourlyData;
import org.example.mete.OpenMeteoResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 控制器集成测试：验证查询参数绑定、统一响应与异常处理。
 *
 * @author windy
 */
@SpringBootTest
@AutoConfigureMockMvc
class OpenMeteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    void shouldBindCommaSeparatedHourlyVariables() throws Exception {
        when(restTemplate.getForObject(any(URI.class), eq(OpenMeteoResult.class)))
                .thenReturn(mockResult());

        mockMvc.perform(get("/api/open-meteo/hourly")
                        .param("latitude", "39.9")
                        .param("longitude", "116.4")
                        .param("hourly", "temperature_2m,wind_speed_10m")
                        .param("forecastDays", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data[0].time").value("2026-11-20 00:00"))
                .andExpect(jsonPath("$.data[0].temperature").value(1.5))
                .andExpect(jsonPath("$.data[0].windSpeed").value(10.0));
    }

    @Test
    void shouldReturnBadRequestWhenLatitudeInvalid() throws Exception {
        mockMvc.perform(get("/api/open-meteo/hourly").param("latitude", "100"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void shouldReturnBadRequestWhenDateRangeIncomplete() throws Exception {
        mockMvc.perform(get("/api/open-meteo/hourly").param("startDate", "2026-11-20"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    private OpenMeteoResult mockResult() {
        HourlyData hourly = new HourlyData();
        hourly.setTime(List.of("2026-11-20T00:00"));
        hourly.setTemperature2m(List.of(1.5));
        hourly.setWindSpeed10m(List.of(10.0));
        hourly.setWindDirection10m(List.of(180.0));
        hourly.setShortwaveRadiation(List.of(0.0));
        hourly.setCloudCover(List.of(25.0));
        hourly.setSurfacePressure(List.of(1010.0));
        hourly.setRelativeHumidity2m(List.of(60.0));
        hourly.setDewPoint2m(List.of(-5.0));
        hourly.setPrecipitation(List.of(0.0));
        hourly.setSnowfall(List.of(0.0));

        OpenMeteoResult result = new OpenMeteoResult();
        result.setHourly(hourly);
        return result;
    }
}
