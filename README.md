# windy-openMeteoProject

基于 [Open-Meteo](https://open-meteo.com/) 免费天气 API 的**逐小时天气数据服务**，Spring Boot 2.7 + Java 17 实现。
不需要 API Key，开箱即用。

## 功能

- 调用 `https://api.open-meteo.com/v1/forecast`，按 **1 小时** 粒度返回数据；
- 一次返回 10 项要素：温度、风速、风向、辐照、云量、气压、相对湿度、露点温度、降水量、降雪量；
- 按数组下标把 Open-Meteo 的并行数组扁平化成 `List<HourlyWeather>`（一行一小时）；
- 支持 GET 查询参数与 POST JSON 两种调用方式；
- 参数校验（Bean Validation）、统一响应结构、全局异常处理；
- Knife4j / OpenAPI3 在线接口文档；
- 单元测试使用 `MockRestServiceServer`，不依赖真实网络。

## 技术栈

| 组件 | 版本 |
| --- | --- |
| Java | 17 |
| Spring Boot | 2.7.18 |
| Maven | 3.6+ |
| Lombok | 由 Spring Boot 管理 |
| Knife4j | 4.3.0（springdoc-openapi3） |
| JUnit 5 / AssertJ | 由 Spring Boot 管理 |

## 目录结构

```
src/main/java/org/example
├── OpenMeteoApplication.java          # 启动类
├── common
│   └── R.java                         # 统一响应结构
├── config
│   ├── OpenApiConfig.java             # OpenAPI 文档信息
│   ├── OpenMeteoProperties.java       # open-meteo.* 配置绑定
│   ├── RestTemplateConfig.java        # RestTemplate 超时/日志
│   └── WebConfig.java                 # String -> 枚举转换器
├── controller
│   ├── OpenMeteController.java        # 天气接口
│   └── WindyController.java           # 预留的 Windy 平台接口
├── exception
│   ├── GlobalExceptionHandler.java    # 全局异常处理
│   └── OpenMeteoException.java        # 调用异常
├── mete                               # Open-Meteo 参数/结果模型
│   ├── OpenMeteoParam.java            # 查询参数类
│   ├── OpenMeteoResult.java           # 原始结果类
│   ├── HourlyData.java                # hourly 并行数组
│   ├── HourlyUnits.java               # hourly_units 单位
│   ├── HourlyWeather.java             # 扁平化后的逐小时数据
│   ├── WeatherVariable.java           # 10 项天气变量枚举
│   ├── TemperatureUnit.java
│   ├── WindSpeedUnit.java
│   └── PrecipitationUnit.java
└── service
    ├── OpenMeteoService.java
    └── impl/OpenMeteoServiceImpl.java

src/main/resources/application.yml
src/test/java/org/example/...          # 上下文测试 + 服务单元测试
```

## 快速开始

```bash
# 1. 编译
mvn clean package

# 2. 运行
mvn spring-boot:run
# 或者
java -jar target/windy-openMeteoProject-1.0-SNAPSHOT.jar
```

启动后：

- 接口文档（Knife4j）：<http://localhost:8080/doc.html>
- OpenAPI JSON：<http://localhost:8080/v3/api-docs>

## 配置说明（application.yml）

| 配置项 | 默认值 | 说明 |
| --- | --- | --- |
| `open-meteo.base-url` | `https://api.open-meteo.com` | Open-Meteo 服务地址 |
| `open-meteo.timezone` | `Asia/Shanghai` | 返回时间的时区口径 |
| `open-meteo.connect-timeout` | `5s` | 连接超时 |
| `open-meteo.read-timeout` | `15s` | 读取超时 |
| `open-meteo.default-latitude` | `39.9042` | 未传纬度时的默认纬度（北京） |
| `open-meteo.default-longitude` | `116.4074` | 未传经度时的默认经度 |
| `open-meteo.default-forecast-days` | `7` | 未指定日期区间时的预报天数（0~16） |

## 接口说明

### 1）GET 逐小时天气

```http
GET /api/open-meteo/hourly?latitude=39.9042&longitude=116.4074&forecastDays=1
```

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `latitude` | double | 否 | 纬度，默认 39.9042 |
| `longitude` | double | 否 | 经度，默认 116.4074 |
| `hourly` | string | 否 | 变量名，逗号分隔，默认 10 项全取 |
| `timezone` | string | 否 | 时区，默认 `Asia/Shanghai` |
| `forecastDays` | int | 否 | 预报天数 0~16，默认 7 |
| `pastDays` | int | 否 | 追溯天数 0~92 |
| `startDate` / `endDate` | date | 否 | 日期区间（yyyy-MM-dd），**必须成对出现**，与 `forecastDays`/`pastDays` 互斥 |
| `temperatureUnit` | enum | 否 | `celsius` / `fahrenheit` |
| `windSpeedUnit` | enum | 否 | `kmh` / `ms` / `mph` / `kn` |
| `precipitationUnit` | enum | 否 | `mm` / `inch` |

示例：

```bash
curl "http://localhost:8080/api/open-meteo/hourly?latitude=31.2304&longitude=121.4737&forecastDays=1"
```

只取部分变量：

```bash
curl "http://localhost:8080/api/open-meteo/hourly?latitude=31.2304&longitude=121.4737&forecastDays=1&hourly=temperature_2m,wind_speed_10m,precipitation"
```

指定日期区间：

```bash
curl "http://localhost:8080/api/open-meteo/hourly?startDate=2026-11-20&endDate=2026-11-21"
```

### 2）POST 逐小时天气

```bash
curl -X POST "http://localhost:8080/api/open-meteo/hourly" \
  -H "Content-Type: application/json" \
  -d '{
    "latitude": 31.2304,
    "longitude": 121.4737,
    "forecastDays": 1,
    "temperatureUnit": "celsius",
    "hourly": ["temperature_2m", "wind_speed_10m", "precipitation"]
  }'
```

### 3）原始响应

```http
GET /api/open-meteo/forecast?latitude=31.2304&longitude=121.4737&forecastDays=1
```

返回 Open-Meteo 原始的 `OpenMeteoResult`（含 `hourly_units`、`utc_offset_seconds` 等字段）。

## 返回示例

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "time": "2026-11-20 00:00",
      "temperature": 5.1,
      "windSpeed": 12.7,
      "windDirection": 180.0,
      "shortwaveRadiation": 0.0,
      "cloudCover": 25.0,
      "surfacePressure": 1012.3,
      "relativeHumidity": 60.0,
      "dewPoint": -2.1,
      "precipitation": 0.0,
      "snowfall": 0.0
    }
  ]
}
```

## 10 项要素与 Open-Meteo 变量对照

| 要素 | 变量名 | 默认单位 |
| --- | --- | --- |
| 温度 | `temperature_2m` | °C |
| 风速 | `wind_speed_10m` | km/h |
| 风向 | `wind_direction_10m` | ° |
| 辐照 | `shortwave_radiation` | W/m² |
| 云量 | `cloud_cover` | % |
| 气压 | `surface_pressure` | hPa |
| 相对湿度 | `relative_humidity_2m` | % |
| 露点温度 | `dew_point_2m` | °C |
| 降水量 | `precipitation` | mm |
| 降雪量 | `snowfall` | cm |

## 测试

```bash
mvn test
```

- `OpenMeteoApplicationTest`：验证 Spring 上下文、配置绑定、Knife4j 自动配置；
- `OpenMeteoServiceImplTest`：用 `MockRestServiceServer` 模拟 Open-Meteo 响应，验证参数拼接与数组扁平化逻辑。

## 网络与代理

本机若需要代理才能访问 `api.open-meteo.com`，启动时给 JVM 加上代理参数即可，代码无需改动：

```bash
java -Dhttps.proxyHost=127.0.0.1 -Dhttps.proxyPort=7890 \
     -jar target/windy-openMeteoProject-1.0-SNAPSHOT.jar
```

IDEA 中可把同样的参数加到运行配置的 **VM options** 里。
Maven 构建如需代理，在 Maven `settings.xml` 的 `<proxies>` 中配置。

## 备注

- Open-Meteo 免费接口对非商业用途有频率限制，生产环境建议增加缓存或限流；
- `start_date`/`end_date` 与 `forecast_days`/`past_days` 在 Open-Meteo 中互斥，代码里已按日期区间优先处理；
- 时间字段为**请求时区下的本地时间**（默认 `Asia/Shanghai`）。
