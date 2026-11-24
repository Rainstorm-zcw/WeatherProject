package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.common.R;
import org.example.mete.HourlyWeather;
import org.example.mete.OpenMeteoBatchParam;
import org.example.mete.OpenMeteoLocationResult;
import org.example.mete.OpenMeteoParam;
import org.example.mete.OpenMeteoResult;
import org.example.service.OpenMeteoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * Open-Meteo 天气接口。
 *
 * @author windy
 */
@Tag(name = "Open-Meteo 天气接口", description = "基于 api.open-meteo.com 的逐小时天气数据")
@RestController
@RequestMapping("/api/open-meteo")
@RequiredArgsConstructor
public class OpenMeteController {

    private final OpenMeteoService openMeteoService;

    /**
     * GET 方式获取逐小时天气，参数通过 URL 传递，方便浏览器 / curl 直接调用。
     */
    @Operation(summary = "逐小时天气（GET）",
            description = "按 1 小时返回温度、风速、风向、辐照、云量、气压、相对湿度、露点温度、降水量、降雪量")
    @GetMapping("/hourly")
    public R<List<HourlyWeather>> hourly(@Valid @ModelAttribute OpenMeteoParam param) {
        return R.ok(openMeteoService.fetchHourly(param));
    }

    /**
     * POST 方式获取逐小时天气，参数通过 JSON 请求体传递，适合变量较多或前端统一封装的场景。
     */
    @Operation(summary = "逐小时天气（POST）",
            description = "请求体为 OpenMeteoParam，返回结构同 GET 接口")
    @PostMapping("/hourly")
    public R<List<HourlyWeather>> hourlyByPost(@Valid @RequestBody OpenMeteoParam param) {
        return R.ok(openMeteoService.fetchHourly(param));
    }

    /**
     * 多坐标点查询：latitude/longitude 支持逗号分隔多个坐标，按坐标返回数组。
     */
    @Operation(summary = "批量逐小时天气（GET）",
            description = "latitude/longitude 支持逗号分隔多个坐标，例如 latitude=39.9,31.2&longitude=116.4,121.5")
    @GetMapping("/hourly/batch")
    public R<List<OpenMeteoLocationResult>> hourlyBatch(@Valid @ModelAttribute OpenMeteoBatchParam param) {
        return R.ok(openMeteoService.fetchHourlyBatch(param));
    }

    /**
     * 多坐标点查询：POST JSON 请求体，latitude/longitude 支持数组或逗号字符串。
     */
    @Operation(summary = "批量逐小时天气（POST）",
            description = "请求体为 OpenMeteoBatchParam，latitude/longitude 支持数组、逗号字符串或单个数字")
    @PostMapping("/hourly/batch")
    public R<List<OpenMeteoLocationResult>> hourlyBatchByPost(@Valid @RequestBody OpenMeteoBatchParam param) {
        return R.ok(openMeteoService.fetchHourlyBatch(param));
    }

    /**
     * 返回 Open-Meteo 原始响应，便于排查问题或读取未封装的字段。
     */
    @Operation(summary = "原始响应", description = "直接返回 Open-Meteo /v1/forecast 的原始 JSON 结构")
    @GetMapping("/forecast")
    public R<OpenMeteoResult> forecast(@Valid @ModelAttribute OpenMeteoParam param) {
        return R.ok(openMeteoService.fetchForecast(param));
    }
}
