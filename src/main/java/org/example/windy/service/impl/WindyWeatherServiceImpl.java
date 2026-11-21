package org.example.windy.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mete.HourlyWeather;
import org.example.mete.OpenMeteoParam;
import org.example.mete.WeatherVariable;
import org.example.service.OpenMeteoService;
import org.example.windy.client.WindyApiClient;
import org.example.windy.config.WindyProperties;
import org.example.windy.exception.WindyException;
import org.example.windy.model.WindyForecastParam;
import org.example.windy.model.WindyForecastResponse;
import org.example.windy.model.WindyHourlyWeather;
import org.example.windy.model.WindySession;
import org.example.windy.service.WindySessionManager;
import org.example.windy.service.WindyWeatherService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Windy 天气数据服务实现。
 * <p>
 * 数据来源与换算：
 * <ul>
 *     <li>温度、露点：K -> °C</li>
 *     <li>风速：m/s -> km/h</li>
 *     <li>气压：Pa -> hPa</li>
 *     <li>云量：Windy 分层云量（cloud-XXXh）按随机重叠法合成 %</li>
 *     <li>相对湿度：由温度与露点按 Magnus 公式计算 %</li>
 *     <li>辐照：Windy point API 无此字段，默认用 Open-Meteo shortwave_radiation 补齐</li>
 * </ul>
 *
 * @author windy
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WindyWeatherServiceImpl implements WindyWeatherService {

    private static final String RADIATION_SOURCE_OPEN_METEO = "OPEN_METEO";

    private static final long ONE_HOUR_MILLIS = 3_600_000L;

    private final WindyApiClient windyApiClient;

    private final WindySessionManager windySessionManager;

    private final WindyProperties windyProperties;

    private final OpenMeteoService openMeteoService;

    @Override
    public List<WindyHourlyWeather> fetchHourly(WindyForecastParam param) {
        WindyForecastResponse response = fetchForecast(param);
        ZoneId zoneId = resolveZoneId(param, response);
        Map<Long, Double> radiation = loadRadiation(param);
        List<WindyHourlyWeather> rows = toHourlyWeather(response, zoneId, radiation);
        log.info("Windy 返回 {} 条逐小时数据，模型: {}", rows.size(),
                Optional.ofNullable(response.getHeader()).map(WindyForecastResponse.Header::getModel).orElse("-"));
        return rows;
    }

    @Override
    public WindyForecastResponse fetchForecast(WindyForecastParam param) {
        WindyForecastParam effectiveParam = withSession(param);
        return windyApiClient.forecast(effectiveParam);
    }

    /**
     * 未显式传 uid/token2 时，自动使用服务端缓存的 Windy 会话。
     */
    private WindyForecastParam withSession(WindyForecastParam param) {
        boolean hasUid = StringUtils.hasText(param.getUid());
        boolean hasToken = StringUtils.hasText(param.getToken2());
        if (hasUid && hasToken) {
            return param;
        }
        if (hasUid || hasToken) {
            throw new WindyException("uid 与 token2 必须同时提供，或同时留空使用服务端会话");
        }
        WindySession session = windySessionManager.getSession();
        return param.toBuilder()
                .uid(session.getUid())
                .token2(session.getToken())
                .build();
    }

    /**
     * 时区优先级：请求参数 > Windy 返回的地点时区 > 配置默认时区。
     */
    private ZoneId resolveZoneId(WindyForecastParam param, WindyForecastResponse response) {
        String timezone = Stream.of(
                        param.getTimezone(),
                        Optional.ofNullable(response.getCelestial())
                                .map(WindyForecastResponse.Celestial::getTzName)
                                .orElse(null),
                        windyProperties.getTimezone())
                .filter(StringUtils::hasText)
                .findFirst()
                .orElse(windyProperties.getTimezone());
        try {
            return ZoneId.of(timezone);
        } catch (DateTimeException exception) {
            log.warn("无法识别的时区 {}，使用默认时区 {}", timezone, windyProperties.getTimezone());
            return ZoneId.of(windyProperties.getTimezone());
        }
    }

    /**
     * Windy point API 不返回辐照，使用 Open-Meteo 的 shortwave_radiation 补齐。
     * 失败时只记录日志，不影响 Windy 主数据。
     */
    private Map<Long, Double> loadRadiation(WindyForecastParam param) {
        if (!windyProperties.getRadiation().isOpenMeteoFallback()) {
            return Collections.emptyMap();
        }
        int days = Math.min(Math.max(
                Optional.ofNullable(param.getDays()).orElse(windyProperties.getDefaultDays()), 1), 16);
        try {
            OpenMeteoParam openMeteoParam = OpenMeteoParam.builder()
                    .latitude(param.getLatitude())
                    .longitude(param.getLongitude())
                    .timezone("GMT")
                    // Windy 的一天从地点本地 00:00 开始，比 GMT 当天多出前一段，需要多取一天
                    .pastDays(1)
                    .forecastDays(Math.min(days + 1, 16))
                    .hourly(List.of(WeatherVariable.SHORTWAVE_RADIATION))
                    .build();
            return openMeteoService.fetchHourly(openMeteoParam).stream()
                    .filter(row -> row.getTime() != null && row.getShortwaveRadiation() != null)
                    .collect(Collectors.toMap(
                            row -> row.getTime().toInstant(ZoneOffset.UTC).toEpochMilli(),
                            HourlyWeather::getShortwaveRadiation,
                            (left, right) -> left));
        } catch (Exception exception) {
            log.warn("Open-Meteo 辐照回退失败，shortwaveRadiation 将为空: {}", exception.getMessage());
            return Collections.emptyMap();
        }
    }

    private List<WindyHourlyWeather> toHourlyWeather(WindyForecastResponse response,
                                                     ZoneId zoneId,
                                                     Map<Long, Double> radiation) {
        WindyForecastResponse.ForecastData data = response.getData();
        List<Long> timestamps = Optional.ofNullable(data.getTs()).orElseGet(Collections::emptyList);
        WindyForecastResponse.Meteogram meteogram = response.getMeteogram();
        Map<Long, Integer> meteogramIndex = indexByTimestamp(meteogram);
        Double elevation = Optional.ofNullable(response.getHeader())
                .map(WindyForecastResponse.Header::getElevation)
                .orElse(0.0);

        return IntStream.range(0, timestamps.size())
                .mapToObj(index -> {
                    Long timestamp = timestamps.get(index);
                    int meteoIndex = meteogramIndex.getOrDefault(timestamp, index);
                    Double temperatureK = valueAt(data.getTemperature(), index);
                    Double dewPointK = meteogram == null ? null : valueAt(meteogram.getDewPoint(), meteoIndex);
                    Double radiationValue = radiation.get(truncateToHour(timestamp));

                    return WindyHourlyWeather.builder()
                            .time(Instant.ofEpochMilli(timestamp).atZone(zoneId).toLocalDateTime())
                            .temperature(kelvinToCelsius(temperatureK))
                            .windSpeed(msToKmh(valueAt(data.getWind(), index)))
                            .windDirection(round(valueAt(data.getWindDir(), index), 1))
                            .shortwaveRadiation(round(radiationValue, 2))
                            .cloudCover(totalCloudCover(meteogram, meteoIndex))
                            .surfacePressure(mslPaToSurfaceHpa(valueAt(data.getPressure(), index), elevation))
                            .relativeHumidity(relativeHumidity(temperatureK, dewPointK))
                            .dewPoint(kelvinToCelsius(dewPointK))
                            .precipitation(round(valueAt(data.getPrecipAmount(), index), 2))
                            .snowfall(round(valueAt(data.getPrecipSnowAmount(), index), 2))
                            .radiationSource(radiationValue == null ? null : RADIATION_SOURCE_OPEN_METEO)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private Map<Long, Integer> indexByTimestamp(WindyForecastResponse.Meteogram meteogram) {
        if (meteogram == null || meteogram.getTs() == null) {
            return Collections.emptyMap();
        }
        List<Long> timestamps = meteogram.getTs();
        return IntStream.range(0, timestamps.size())
                .filter(index -> timestamps.get(index) != null)
                .boxed()
                .collect(Collectors.toMap(timestamps::get, index -> index, (left, right) -> left));
    }

    /**
     * 分层云量按随机重叠法合成总云量：total = 1 - Π(1 - cloud_i)。
     */
    private Double totalCloudCover(WindyForecastResponse.Meteogram meteogram, int index) {
        if (meteogram == null || meteogram.getCloudLayers().isEmpty()) {
            return null;
        }
        List<Double> cloudLayerValues = meteogram.getCloudLayers().values().stream()
                .map(layer -> valueAt(layer, index))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (cloudLayerValues.isEmpty()) {
            return null;
        }
        double clearSky = cloudLayerValues.stream()
                .mapToDouble(value -> 1.0 - clamp(value, 0, 100) / 100.0)
                .reduce(1.0, (left, right) -> left * right);
        return round((1.0 - clearSky) * 100.0, 1);
    }

    /**
     * Magnus 公式：RH = e(Td) / es(T) * 100%。
     */
    private static Double relativeHumidity(Double temperatureK, Double dewPointK) {
        if (temperatureK == null || dewPointK == null) {
            return null;
        }
        double temperature = temperatureK - 273.15;
        double dewPoint = dewPointK - 273.15;
        double saturation = 6.112 * Math.exp(17.67 * temperature / (temperature + 243.5));
        double actual = 6.112 * Math.exp(17.67 * dewPoint / (dewPoint + 243.5));
        if (saturation <= 0) {
            return null;
        }
        return round(clamp(actual / saturation * 100.0, 0, 100), 2);
    }

    private static Double kelvinToCelsius(Double kelvin) {
        return kelvin == null ? null : round(kelvin - 273.15, 2);
    }

    private static Double msToKmh(Double metersPerSecond) {
        return metersPerSecond == null ? null : round(metersPerSecond * 3.6, 2);
    }

    /**
     * Windy 的 {@code pressure} 是海平面气压（MSL，单位 Pa），按测点海拔换算成站点气压（hPa）。
     * <p>
     * 公式：p_surface = p_msl * (1 - 2.25577e-5 * h) ^ 5.25588。
     */
    private static Double mslPaToSurfaceHpa(Double pressurePa, Double elevationMeters) {
        if (pressurePa == null) {
            return null;
        }
        double elevation = Optional.ofNullable(elevationMeters).orElse(0.0);
        double factor = Math.pow(1.0 - 2.25577e-5 * elevation, 5.25588);
        return round(pressurePa / 100.0 * factor, 2);
    }

    private static long truncateToHour(long timestamp) {
        return timestamp - Math.floorMod(timestamp, ONE_HOUR_MILLIS);
    }

    private static <T> T valueAt(List<T> values, int index) {
        return values != null && index >= 0 && index < values.size() ? values.get(index) : null;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private static Double round(Double value, int scale) {
        if (value == null || !Double.isFinite(value)) {
            return null;
        }
        double factor = Math.pow(10, scale);
        return Math.round(value * factor) / factor;
    }
}
