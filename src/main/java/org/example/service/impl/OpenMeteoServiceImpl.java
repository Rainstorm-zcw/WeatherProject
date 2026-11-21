package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.config.OpenMeteoProperties;
import org.example.exception.OpenMeteoException;
import org.example.mete.HourlyData;
import org.example.mete.HourlyWeather;
import org.example.mete.OpenMeteoParam;
import org.example.mete.OpenMeteoResult;
import org.example.mete.WeatherVariable;
import org.example.service.OpenMeteoService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Open-Meteo 天气数据服务实现。
 * <p>
 * 负责拼接 {@code /v1/forecast} 查询参数、调用接口，并把并行数组转换成逐小时对象列表。
 *
 * @author windy
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OpenMeteoServiceImpl implements OpenMeteoService {

    private static final String FORECAST_PATH = "/v1/forecast";

    private final RestTemplate openMeteoRestTemplate;

    private final OpenMeteoProperties openMeteoProperties;

    @Override
    public List<HourlyWeather> fetchHourly(OpenMeteoParam param) {
        OpenMeteoResult result = fetchForecast(param);
        List<HourlyWeather> rows = toHourlyWeather(result);
        log.info("Open-Meteo 返回 {} 条逐小时数据，变量: {}", rows.size(),
                resolveVariables(Optional.ofNullable(param).orElseGet(OpenMeteoParam::new)));
        return rows;
    }

    @Override
    public OpenMeteoResult fetchForecast(OpenMeteoParam param) {
        OpenMeteoParam query = Optional.ofNullable(param).orElseGet(OpenMeteoParam::new);
        URI uri = buildUri(query);
        log.info("请求 Open-Meteo: {}", uri);
        try {
            OpenMeteoResult result = openMeteoRestTemplate.getForObject(uri, OpenMeteoResult.class);
            if (result == null || result.getHourly() == null) {
                throw new OpenMeteoException("Open-Meteo 响应为空或缺少 hourly 字段");
            }
            return result;
        } catch (RestClientException exception) {
            throw new OpenMeteoException("请求 Open-Meteo 失败: " + exception.getMessage(), exception);
        }
    }

    /**
     * 拼接查询参数。
     * <p>
     * 注意：{@code forecast_days}/{@code past_days} 与 {@code start_date}/{@code end_date}
     * 在 Open-Meteo 中互斥，因此按日期区间优先处理。
     *
     * @param param 查询参数
     * @return 请求 URI
     */
    private URI buildUri(OpenMeteoParam param) {
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("latitude", Optional.ofNullable(param.getLatitude())
                .orElse(openMeteoProperties.getDefaultLatitude()));
        query.put("longitude", Optional.ofNullable(param.getLongitude())
                .orElse(openMeteoProperties.getDefaultLongitude()));
        query.put("hourly", resolveVariables(param).stream()
                .map(WeatherVariable::getApiName)
                .collect(Collectors.joining(",")));
        query.put("timezone", Optional.ofNullable(param.getTimezone())
                .filter(StringUtils::hasText)
                .orElse(openMeteoProperties.getTimezone()));
        Optional.ofNullable(param.getModels())
                .filter(StringUtils::hasText)
                .map(String::trim)
                .ifPresent(models -> query.put("models", models));

        boolean hasStartDate = param.getStartDate() != null;
        boolean hasEndDate = param.getEndDate() != null;
        if (hasStartDate && hasEndDate) {
            query.put("start_date", param.getStartDate());
            query.put("end_date", param.getEndDate());
        } else if (hasStartDate || hasEndDate) {
            throw new OpenMeteoException("startDate 与 endDate 必须同时指定");
        } else {
            query.put("forecast_days", Optional.ofNullable(param.getForecastDays())
                    .orElse(openMeteoProperties.getDefaultForecastDays()));
            Optional.ofNullable(param.getPastDays())
                    .ifPresent(days -> query.put("past_days", days));
        }

        Optional.ofNullable(param.getTemperatureUnit())
                .ifPresent(unit -> query.put("temperature_unit", unit.getValue()));
        Optional.ofNullable(param.getWindSpeedUnit())
                .ifPresent(unit -> query.put("wind_speed_unit", unit.getValue()));
        Optional.ofNullable(param.getPrecipitationUnit())
                .ifPresent(unit -> query.put("precipitation_unit", unit.getValue()));

        String baseUrl = StringUtils.trimTrailingCharacter(openMeteoProperties.getBaseUrl(), '/');
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl).path(FORECAST_PATH);
        query.forEach((name, value) -> builder.queryParam(name, value));
        return builder.encode().build().toUri();
    }

    /**
     * 未指定变量时默认取 10 项全量。
     */
    private List<WeatherVariable> resolveVariables(OpenMeteoParam param) {
        return Optional.ofNullable(param.getHourly())
                .filter(variables -> !variables.isEmpty())
                .orElse(WeatherVariable.DEFAULT_VARIABLES);
    }

    /**
     * 按下标把并行数组合并成逐小时对象列表。
     */
    private List<HourlyWeather> toHourlyWeather(OpenMeteoResult result) {
        HourlyData hourly = result.getHourly();
        List<String> times = Optional.ofNullable(hourly.getTime()).orElseGet(Collections::emptyList);
        return IntStream.range(0, times.size())
                .mapToObj(index -> HourlyWeather.builder()
                        .time(parseTime(times.get(index)))
                        .temperature(valueAt(hourly.getTemperature2m(), index))
                        .windSpeed(valueAt(hourly.getWindSpeed10m(), index))
                        .windDirection(valueAt(hourly.getWindDirection10m(), index))
                        .shortwaveRadiation(valueAt(hourly.getShortwaveRadiation(), index))
                        .cloudCover(valueAt(hourly.getCloudCover(), index))
                        .surfacePressure(valueAt(hourly.getSurfacePressure(), index))
                        .relativeHumidity(valueAt(hourly.getRelativeHumidity2m(), index))
                        .dewPoint(valueAt(hourly.getDewPoint2m(), index))
                        .precipitation(valueAt(hourly.getPrecipitation(), index))
                        .snowfall(valueAt(hourly.getSnowfall(), index))
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 安全取值：数组为 null 或长度不足时返回 null，避免下标越界。
     */
    private static Double valueAt(List<Double> values, int index) {
        return values != null && index < values.size() ? values.get(index) : null;
    }

    private static LocalDateTime parseTime(String time) {
        return StringUtils.hasText(time) ? LocalDateTime.parse(time.trim()) : null;
    }
}
