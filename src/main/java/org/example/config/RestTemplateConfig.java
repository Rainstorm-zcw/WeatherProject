package org.example.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

/**
 * 调用 Open-Meteo 的 RestTemplate 配置（超时 + 请求日志）。
 *
 * @author windy
 */
@Slf4j
@Configuration
public class RestTemplateConfig {

    @Bean
    @Primary
    public RestTemplate openMeteoRestTemplate(RestTemplateBuilder builder, OpenMeteoProperties properties) {
        return builder
                .setConnectTimeout(properties.getConnectTimeout())
                .setReadTimeout(properties.getReadTimeout())
                .additionalInterceptors((request, body, execution) -> {
                    request.getHeaders().set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
                    request.getHeaders().set(HttpHeaders.USER_AGENT, "windy-open-meteo-project/1.0");
                    log.debug("Open-Meteo 请求 -> {} {}", request.getMethod(), request.getURI());
                    return execution.execute(request, body);
                })
                .build();
    }
}
