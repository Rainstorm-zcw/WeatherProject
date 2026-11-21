package org.example.windy.client;

import lombok.extern.slf4j.Slf4j;
import org.example.windy.config.WindyProperties;
import org.example.windy.exception.WindyException;
import org.example.windy.model.WindyAccountResponse;
import org.example.windy.model.WindyForecastParam;
import org.example.windy.model.WindyForecastResponse;
import org.example.windy.model.WindyLoginParam;
import org.example.windy.model.WindySession;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Windy 账号与 point forecast 接口客户端。
 * <p>
 * 预登录：{@code GET /api/info?country=my&pr=1&sc=0&token2=pending&uid={uuid}&v=51.4.4&poc=4}
 * <br>
 * 登录：{@code POST /api/login?pr=0&sc=0&token2={预登录token}&uid={uuid}&v=51.4.4&poc=92}
 * <br>
 * 天气：{@code GET https://node.windy.com/forecast/point/{model}/v3.0/{lat}/{lon}}
 *
 * @author windy
 */
@Slf4j
@Component
public class WindyApiClient {

    private static final String PENDING_TOKEN = "pending";

    private static final int PRE_LOGIN_PR = 1;
    private static final int PRE_LOGIN_SC = 0;
    private static final int PRE_LOGIN_POC = 4;

    private static final int LOGIN_PR = 0;
    private static final int LOGIN_SC = 0;
    private static final int LOGIN_POC = 92;

    private static final int FORECAST_PR = 0;
    private static final int FORECAST_SC = 11;
    private static final int FORECAST_POC = 1273;

    private static final String WINDY_ORIGIN = "https://www.windy.com";

    private final RestTemplate windyRestTemplate;

    private final WindyProperties properties;

    public WindyApiClient(@Qualifier("windyRestTemplate") RestTemplate windyRestTemplate,
                          WindyProperties properties) {
        this.windyRestTemplate = windyRestTemplate;
        this.properties = properties;
    }

    /**
     * 预登录：随机生成 uid（UUID），拿到游客 token。
     *
     * @return 游客会话
     */
    public WindySession preLogin() {
        String uid = UUID.randomUUID().toString();
        URI uri = UriComponentsBuilder.fromHttpUrl(properties.getAccountBaseUrl())
                .path("/api/info")
                .queryParam("country", properties.getCountry())
                .queryParam("pr", PRE_LOGIN_PR)
                .queryParam("sc", PRE_LOGIN_SC)
                .queryParam("token2", PENDING_TOKEN)
                .queryParam("uid", uid)
                .queryParam("v", properties.getVersion())
                .queryParam("poc", PRE_LOGIN_POC)
                .build()
                .encode()
                .toUri();

        WindyAccountResponse response = execute(
                () -> windyRestTemplate.getForObject(uri, WindyAccountResponse.class), "Windy 预登录");
        String token = Optional.ofNullable(response)
                .map(WindyAccountResponse::getToken)
                .filter(StringUtils::hasText)
                .orElseThrow(() -> new WindyException("Windy 预登录响应中缺少 token"));

        log.info("Windy 预登录成功: uid={}", uid);
        return WindySession.builder()
                .uid(uid)
                .token(token)
                .auth(false)
                .premium(false)
                .build();
    }

    /**
     * 登录：deviceId 必须与预登录的 uid 相同。
     *
     * @param uid            预登录生成的 uid
     * @param preLoginToken  预登录返回的 token
     * @param loginParam     登录请求体（deviceId 会被强制设置为 uid）
     * @return 登录后的会话
     */
    public WindySession login(String uid, String preLoginToken, WindyLoginParam loginParam) {
        WindyLoginParam body = Optional.ofNullable(loginParam)
                .orElseGet(WindyLoginParam::new)
                .toBuilder()
                .deviceId(uid)
                .build();

        URI uri = UriComponentsBuilder.fromHttpUrl(properties.getAccountBaseUrl())
                .path("/api/login")
                .queryParam("pr", LOGIN_PR)
                .queryParam("sc", LOGIN_SC)
                .queryParam("token2", preLoginToken)
                .queryParam("uid", uid)
                .queryParam("v", properties.getVersion())
                .queryParam("poc", LOGIN_POC)
                .build()
                .encode()
                .toUri();

        WindyAccountResponse response = execute(
                () -> windyRestTemplate.postForObject(uri, jsonEntity(body), WindyAccountResponse.class),
                "Windy 登录");

        WindyAccountResponse.LoginUser loginUser = Optional.ofNullable(response)
                .map(WindyAccountResponse::getUserInfo)
                .orElse(null);
        String token = Optional.ofNullable(loginUser)
                .map(WindyAccountResponse.LoginUser::getToken)
                .filter(StringUtils::hasText)
                .orElseGet(() -> Optional.ofNullable(response)
                        .map(WindyAccountResponse::getToken)
                        .orElse(null));
        if (!StringUtils.hasText(token)) {
            throw new WindyException("Windy 登录响应中缺少 token");
        }

        WindyAccountResponse.UserInfo userInfo = Optional.ofNullable(loginUser)
                .map(WindyAccountResponse.LoginUser::getUserInfo)
                .orElse(null);
        log.info("Windy 登录成功: uid={}, email={}", uid,
                Optional.ofNullable(userInfo).map(WindyAccountResponse.UserInfo::getEmail).orElse("-"));

        return WindySession.builder()
                .uid(uid)
                .token(token)
                .auth(true)
                .premium(false)
                .email(Optional.ofNullable(userInfo).map(WindyAccountResponse.UserInfo::getEmail).orElse(null))
                .username(Optional.ofNullable(userInfo).map(WindyAccountResponse.UserInfo::getUsername).orElse(null))
                .userId(Optional.ofNullable(userInfo).map(WindyAccountResponse.UserInfo::getId).orElse(null))
                .build();
    }

    /**
     * 获取 point forecast 原始响应。
     *
     * @param param 查询参数（uid/token2 必须已经填好）
     * @return 原始响应
     */
    public WindyForecastResponse forecast(WindyForecastParam param) {
        URI uri = buildForecastUri(param);
        log.info("请求 Windy point forecast: {}", uri);
        WindyForecastResponse response = execute(
                () -> windyRestTemplate.getForObject(uri, WindyForecastResponse.class),
                "Windy point forecast");
        if (response == null || response.getData() == null) {
            throw new WindyException("Windy point forecast 响应为空或缺少 data 字段");
        }
        return response;
    }

    private URI buildForecastUri(WindyForecastParam param) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getNodeBaseUrl())
                .path("/forecast/point/{model}/v3.0/{latitude}/{longitude}")
                .queryParam("days", param.getDays())
                .queryParam("include", param.getInclude())
                .queryParam("source", param.getSource())
                .queryParam("step", param.getStep())
                .queryParam("pr", Optional.ofNullable(param.getPr()).orElse(FORECAST_PR))
                .queryParam("sc", Optional.ofNullable(param.getSc()).orElse(FORECAST_SC))
                .queryParam("token2", param.getToken2())
                .queryParam("uid", param.getUid())
                .queryParam("v", Optional.ofNullable(param.getVersion()).orElse(properties.getVersion()))
                .queryParam("poc", Optional.ofNullable(param.getPoc()).orElse(FORECAST_POC));
        Optional.ofNullable(param.getRefTime())
                .filter(StringUtils::hasText)
                .ifPresent(refTime -> builder.queryParam("refTime", refTime));

        Map<String, Object> variables = Map.of(
                "model", Optional.ofNullable(param.getModel()).orElse(properties.getDefaultModel()),
                "latitude", param.getLatitude(),
                "longitude", param.getLongitude());
        return builder.build().expand(variables).encode().toUri();
    }

    private HttpEntity<WindyLoginParam> jsonEntity(WindyLoginParam body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.ORIGIN, WINDY_ORIGIN);
        headers.set(HttpHeaders.REFERER, WINDY_ORIGIN + "/");
        return new HttpEntity<>(body, headers);
    }

    private <T> T execute(Supplier<T> action, String actionName) {
        try {
            return action.get();
        } catch (RestClientResponseException exception) {
            throw new WindyException(actionName + " 失败: HTTP " + exception.getRawStatusCode()
                    + " " + exception.getResponseBodyAsString(), exception);
        } catch (RestClientException exception) {
            throw new WindyException(actionName + " 失败: " + exception.getMessage(), exception);
        }
    }
}
