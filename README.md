# windy-openMeteoProject

基于 [Open-Meteo](https://open-meteo.com/) 与 [Windy](https://www.windy.com/) 的**逐小时天气数据服务**，Spring Boot 2.7 + Java 17 实现。
Open-Meteo 不需要 API Key；Windy 通过预登录 + 账号登录拿 token。

## 功能

- 调用 `https://api.open-meteo.com/v1/forecast`，按 **1 小时** 粒度返回数据；
- 调用 Windy `account.windy.com` 预登录 / 登录，再调用 `node.windy.com` point forecast；
- 一次返回 10 项要素：温度、风速、风向、辐照、云量、气压、相对湿度、露点温度、降水量、降雪量；
- 按数组下标把 Open-Meteo 的并行数组扁平化成 `List<HourlyWeather>`（一行一小时）；
- Windy 返回的并行数组同样扁平化成 `List<WindyHourlyWeather>`；
- 支持 GET 查询参数与 POST JSON 两种调用方式；
- 参数校验（Bean Validation）、统一响应结构、全局异常处理；
- Knife4j / OpenAPI3 在线接口文档；
- 单元测试使用 `MockRestServiceServer` / Mockito，不依赖真实网络。

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
│   └── OpenMeteController.java        # Open-Meteo 天气接口
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
├── service
│   ├── OpenMeteoService.java
│   └── impl/OpenMeteoServiceImpl.java
└── windy                              # Windy 平台相关代码（独立目录）
    ├── client/WindyApiClient.java     # 预登录 / 登录 / point forecast
    ├── config
    │   ├── WindyProperties.java
    │   └── WindyRestTemplateConfig.java
    ├── controller/WindyController.java
    ├── exception/WindyException.java
    ├── model
    │   ├── WindyForecastParam.java    # 查询参数 bean
    │   ├── WindyLoginParam.java       # 登录请求体 bean
    │   ├── WindySession.java
    │   ├── WindyAccountResponse.java
    │   ├── WindyForecastResponse.java
    │   ├── WindyHourlyWeather.java
    │   └── WindyTokenClaims.java
    └── service
        ├── WindySessionManager.java
        ├── WindyWeatherService.java
        └── impl/WindyWeatherServiceImpl.java

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

Windy 配置：

| 配置项 | 默认值 | 说明 |
| --- | --- | --- |
| `windy.account-base-url` | `https://account.windy.com` | 预登录 / 登录地址 |
| `windy.node-base-url` | `https://node.windy.com` | point forecast 地址 |
| `windy.version` | `51.4.4` | 客户端版本参数 `v` |
| `windy.country` | `my` | 预登录国家参数 |
| `windy.default-model` | `icon` | 默认模型 |
| `windy.default-days` | `5` | 默认预报天数（1~7） |
| `windy.default-step` | `1` | 1=逐小时（premium），3=3 小时 |
| `windy.timezone` | `Asia/Shanghai` | 时间戳转本地时间使用的时区 |
| `windy.proxy.host` | `127.0.0.1` | Windy HTTP 代理主机，留空表示直连 |
| `windy.proxy.port` | `7890` | Windy HTTP 代理端口 |
| `windy.login-enabled` | `true` | 是否自动登录 |
| `windy.email` / `windy.password` | 环境变量 `WINDY_EMAIL` / `WINDY_PASSWORD` | Windy 账号，建议用环境变量覆盖 |
| `windy.radiation.open-meteo-fallback` | `true` | Windy 无辐照字段时用 Open-Meteo 补齐 |

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
| `models` | string | 否 | 模型名，逗号分隔；默认 `best_match`，例如 `icon_global` |
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

指定模型（`icon_global` 对应 Windy 的 `icon`）：

```bash
curl "http://localhost:8080/api/open-meteo/hourly?latitude=31.232&longitude=121.469&forecastDays=1&models=icon_global&timezone=Asia/Shanghai"
```

> 即使选择同一个 ICON 模型，Open-Meteo 与 Windy 的网格点、海拔订正和起报时次也可能不同，
> 数值不会完全一致；做对比时请按 `time` 对齐，不要按数组下标。

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

### 4）多点批量查询

`latitude` / `longitude` 支持一次传多个坐标，按坐标返回数组；单点接口保持不变。

```bash
# GET：逗号分隔
curl "http://localhost:9989/api/open-meteo/hourly/batch?latitude=39.9042,31.2304&longitude=116.4074,121.4737&forecastDays=1&models=icon_global&hourly=temperature_2m,wind_speed_10m"
```

```bash
# POST：数组、逗号字符串、单个数字都支持
curl -X POST "http://localhost:9989/api/open-meteo/hourly/batch" \
  -H "Content-Type: application/json" \
  -d '{
    "latitude": [39.9042, 31.2304],
    "longitude": [116.4074, 121.4737],
    "forecastDays": 1,
    "models": "icon_global",
    "hourly": ["temperature_2m", "wind_speed_10m"]
  }'
```

返回结构：

```json
{
  "code": 200,
  "data": [
    {
      "latitude": 39.875,
      "longitude": 116.375,
      "elevation": 47.0,
      "timezone": "Asia/Shanghai",
      "hourly": [{ "time": "2026-11-24 00:00", "temperature": 0.6, "windSpeed": 4.1 }]
    },
    {
      "latitude": 31.25,
      "longitude": 121.5,
      "elevation": 5.0,
      "timezone": "Asia/Shanghai",
      "hourly": [{ "time": "2026-11-24 00:00", "temperature": 12.5, "windSpeed": 6.8 }]
    }
  ]
}
```

> `latitude` 与 `longitude` 数量必须一致，单次最多 100 个坐标；返回的 `latitude/longitude` 是 API 就近取整后的网格坐标。

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

## Windy 平台接口

### 预登录与登录流程

1. **预登录**：随机生成 `uid`（UUID），请求
   `GET https://account.windy.com/api/info?country=my&pr=1&sc=0&token2=pending&uid={uid}&v=51.4.4&poc=4`
   ，响应里的 `token` 就是后续的 `token2`。
2. **登录**：请求
   `POST https://account.windy.com/api/login?pr=0&sc=0&token2={预登录token}&uid={uid}&v=51.4.4&poc=92`
   ，body 为：
   ```json
   {
     "deviceId": "{uid}",
     "email": "账号",
     "password": "密码",
     "platform": "desktop",
     "target": "index"
   }
   ```
   **`deviceId` 就是预登录生成的那个 `uid`**，Windy 用它把 token 与客户端设备绑定；两处必须一致。
3. 登录后拿到带 `userID`、`subscriptionTiers` 的 token；只有 premium 账号的 `step=1` 才会真正返回逐小时数据，游客 token 会被降级为 3 小时。

服务启动后首次调用会自动完成上述流程，并把 token 缓存在内存中，过期前 10 分钟自动刷新。

### 请求头（很重要）

调用 `node.windy.com` 时，`Accept` 必须带上官网的自定义类型：

```http
Accept: application/json binary/hdagag$indc444
```

实测同一个 URL、同一个 `refTime`：

- 只发 `Accept: application/json`：北京 00:00 返回 **-8.3°C**；
- 带上 `binary/hdagag$indc444`：同一时刻返回 **+1.11°C**，才和官网、Open-Meteo 对得上。

也就是说，这个头决定了 Windy 返回哪一套数据，不是可有可无的装饰。Spring 的 `HttpHeaders#getAccept()` 解析空格分隔会报错，所以项目里配置成逗号分隔：

```yaml
windy:
  accept-header: "application/json, binary/hdagag$indc444"
```

其余请求头：`User-Agent`、`Origin`、`Referer` 必须带；`Cache-Control/Pragma: no-cache` 建议带；`Sec-Ch-Ua*`、`Sec-Fetch-*`、`Priority` 可选；不要手动设置 `Accept-Encoding`。

### 接口

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/windy/session` | 获取当前会话（自动预登录/登录），返回 uid、token、auth、premium |
| POST | `/api/windy/session/refresh` | 强制刷新会话 |
| GET | `/api/windy/hourly` | 逐小时 10 项要素 |
| POST | `/api/windy/hourly` | 同上，参数走 JSON body |
| GET | `/api/windy/forecast` | Windy point forecast 原始结构 |

`/api/windy/hourly` 参数：

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `latitude` | double | 是 | 纬度 |
| `longitude` | double | 是 | 经度 |
| `model` | string | 否 | 默认 `icon` |
| `days` | int | 否 | 1~7，默认 5 |
| `step` | int | 否 | 1=逐小时，3=3 小时，默认 1 |
| `timezone` | string | 否 | 不传时用 Windy 返回的地点时区 |
| `uid` / `token2` | string | 否 | 同时留空则使用服务端缓存的会话 |

```bash
# 查看当前会话
curl "http://localhost:8080/api/windy/session"

# 上海逐小时
curl "http://localhost:8080/api/windy/hourly?latitude=31.232&longitude=121.469&days=1&step=1"
```

### Windy 字段与 10 项要素对照

| 要素 | Windy 字段 | 处理方式 |
| --- | --- | --- |
| 温度 | `data.temperature` | K → °C |
| 风速 | `data.wind` | m/s → km/h |
| 风向 | `data.windDir` | 直接使用 ° |
| 辐照 | 无 | **用 Open-Meteo `shortwave_radiation` 补齐**，`radiationSource=OPEN_METEO` |
| 云量 | `meteogram.cloud-XXXh` | 分层云量按随机重叠法合成 % |
| 气压 | `data.pressure` | Pa → hPa |
| 相对湿度 | 无 | 由 `temperature` + `meteogram.dewPoint` 按 Magnus 公式计算 |
| 露点温度 | `meteogram.dewPoint` | K → °C |
| 降水量 | `data.precipAmount` | mm |
| 降雪量 | `data.precipSnowAmount` | cm |

> Windy 的 point forecast 接口本身没有辐照字段（官方 Point Forecast API 参数表也没有 radiation），
> 所以默认用项目中已有的 Open-Meteo 数据补齐；把 `windy.radiation.open-meteo-fallback` 设为 `false` 可关闭。

## 测试

```bash
mvn test
```

- `OpenMeteoApplicationTest`：验证 Spring 上下文、配置绑定、Knife4j 自动配置；
- `OpenMeteoServiceImplTest`：用 `MockRestServiceServer` 模拟 Open-Meteo 响应，验证参数拼接与数组扁平化逻辑；
- `OpenMeteoParamValidationTest`：验证经纬度、日期区间等参数校验；
- `OpenMeteoControllerTest`：MockMvc 验证查询参数绑定与统一异常响应；
- `WindyWeatherServiceImplTest`：验证 Windy 单位换算、RH/云量计算与辐照回退。

## 网络与代理

Java 默认**不会使用** Windows/Clash 的系统代理，浏览器能秒开不代表 Java 能直连。

Windy 已支持代码级代理，直接在 `application.yml` 配置即可，不需要 VM options：

```yaml
windy:
  proxy:
    host: 127.0.0.1   # 留空表示直连
    port: 7890
```

默认就是本机 Clash 的 `127.0.0.1:7890`；如果代理端口不同，改 `port` 或设置环境变量 `WINDY_PROXY_HOST` / `WINDY_PROXY_PORT`。

Open-Meteo 或 Maven 构建如需代理，仍可给 JVM 加参数：

```bash
java -Dhttps.proxyHost=127.0.0.1 -Dhttps.proxyPort=7890 \
     -jar target/windy-openMeteoProject-1.0-SNAPSHOT.jar
```

IDEA 中可把同样的参数加到运行配置的 **VM options** 里；Maven 构建在 `settings.xml` 的 `<proxies>` 中配置。

## 备注

- Open-Meteo 免费接口对非商业用途有频率限制，生产环境建议增加缓存或限流；
- `start_date`/`end_date` 与 `forecast_days`/`past_days` 在 Open-Meteo 中互斥，代码里已按日期区间优先处理；
- 时间字段为**请求时区下的本地时间**（默认 `Asia/Shanghai`）；
- Windy token 有效期约 2 天，服务内部会缓存并提前刷新；
- Windy 游客 token 只能拿 3 小时步长，逐小时需要 premium 账号且 `windy.login-enabled=true`；
- Windy 的 `precipSnowAmount` 按 cm 处理（与 Windy 平台雪量单位一致）；
- 账号密码默认写在 `application.yml` 的占位符里，生产环境请改用 `WINDY_EMAIL` / `WINDY_PASSWORD` 环境变量。
