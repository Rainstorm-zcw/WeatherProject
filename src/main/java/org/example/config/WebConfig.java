package org.example.config;

import org.example.mete.PrecipitationUnit;
import org.example.mete.TemperatureUnit;
import org.example.mete.WeatherVariable;
import org.example.mete.WindSpeedUnit;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 注册字符串到枚举的转换器，使 GET 查询参数可以直接使用 Open-Meteo 的变量名
 * （例如 {@code hourly=temperature_2m,wind_speed_10m}）。
 *
 * @author windy
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(String.class, WeatherVariable.class, WeatherVariable::fromApiName);
        registry.addConverter(String.class, TemperatureUnit.class, TemperatureUnit::fromValue);
        registry.addConverter(String.class, WindSpeedUnit.class, WindSpeedUnit::fromValue);
        registry.addConverter(String.class, PrecipitationUnit.class, PrecipitationUnit::fromValue);
    }
}
