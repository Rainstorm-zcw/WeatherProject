package org.example.service;

import org.example.mete.HourlyWeather;
import org.example.mete.OpenMeteoBatchParam;
import org.example.mete.OpenMeteoLocationResult;
import org.example.mete.OpenMeteoParam;
import org.example.mete.OpenMeteoResult;

import java.util.List;

/**
 * Open-Meteo 天气数据服务。
 *
 * @author windy
 */
public interface OpenMeteoService {

    /**
     * 按小时获取天气数据，并扁平化成一行一小时的结构。
     *
     * @param param 查询参数
     * @return 逐小时天气列表
     */
    List<HourlyWeather> fetchHourly(OpenMeteoParam param);

    /**
     * 多坐标点按小时获取天气数据。
     *
     * @param param 批量查询参数
     * @return 每个坐标点一份逐小时天气
     */
    List<OpenMeteoLocationResult> fetchHourlyBatch(OpenMeteoBatchParam param);

    /**
     * 获取 Open-Meteo 原始响应，便于排查问题或使用未封装的字段。
     *
     * @param param 查询参数
     * @return 原始响应
     */
    OpenMeteoResult fetchForecast(OpenMeteoParam param);
}
