package org.example.windy.service;

import org.example.windy.model.WindyForecastParam;
import org.example.windy.model.WindyForecastResponse;
import org.example.windy.model.WindyHourlyWeather;

import java.util.List;

/**
 * Windy 天气数据服务。
 *
 * @author windy
 */
public interface WindyWeatherService {

    /**
     * 获取逐小时天气（10 项要素）。
     *
     * @param param 查询参数
     * @return 逐小时天气
     */
    List<WindyHourlyWeather> fetchHourly(WindyForecastParam param);

    /**
     * 获取 Windy point forecast 原始响应。
     *
     * @param param 查询参数
     * @return 原始响应
     */
    WindyForecastResponse fetchForecast(WindyForecastParam param);
}
