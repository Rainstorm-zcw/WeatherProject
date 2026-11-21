package org.example.windy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.common.R;
import org.example.windy.model.WindyForecastParam;
import org.example.windy.model.WindyForecastResponse;
import org.example.windy.model.WindyHourlyWeather;
import org.example.windy.model.WindySession;
import org.example.windy.service.WindySessionManager;
import org.example.windy.service.WindyWeatherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * Windy 平台天气接口。
 *
 * @author windy
 */
@Tag(name = "Windy 天气接口", description = "Windy 预登录 / 登录 / point forecast 逐小时天气")
@RestController
@RequestMapping("/api/windy")
@RequiredArgsConstructor
public class WindyController {

    private final WindyWeatherService windyWeatherService;

    private final WindySessionManager windySessionManager;

    @Operation(summary = "获取当前 Windy 会话",
            description = "首次调用会自动预登录，并按配置自动登录；uid 即 deviceId，token 即 token2")
    @GetMapping("/session")
    public R<WindySession> session() {
        return R.ok(windySessionManager.getSession());
    }

    @Operation(summary = "强制刷新 Windy 会话", description = "重新预登录/登录，返回新的 uid 与 token")
    @PostMapping("/session/refresh")
    public R<WindySession> refreshSession() {
        return R.ok(windySessionManager.getSession(true));
    }

    @Operation(summary = "逐小时天气（GET）",
            description = "返回温度、风速、风向、辐照、云量、气压、相对湿度、露点温度、降水量、降雪量")
    @GetMapping("/hourly")
    public R<List<WindyHourlyWeather>> hourly(@Valid @ModelAttribute WindyForecastParam param) {
        return R.ok(windyWeatherService.fetchHourly(param));
    }

    @Operation(summary = "逐小时天气（POST）", description = "请求体为 WindyForecastParam")
    @PostMapping("/hourly")
    public R<List<WindyHourlyWeather>> hourlyByPost(@Valid @RequestBody WindyForecastParam param) {
        return R.ok(windyWeatherService.fetchHourly(param));
    }

    @Operation(summary = "原始 point forecast 响应",
            description = "返回 Windy v3.0 原始 data / meteogram / celestial 结构，便于排查")
    @GetMapping("/forecast")
    public R<WindyForecastResponse> forecast(@Valid @ModelAttribute WindyForecastParam param) {
        return R.ok(windyWeatherService.fetchForecast(param));
    }
}
