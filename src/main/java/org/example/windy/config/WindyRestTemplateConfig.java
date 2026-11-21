package org.example.windy.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

/**
 * 调用 Windy 账号 / 数据服务专用的 RestTemplate。
 *
 * @author windy
 */
@Slf4j
@Configuration
public class WindyRestTemplateConfig {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
            + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36";

    @Bean
    public RestTemplate windyRestTemplate(RestTemplateBuilder builder, WindyProperties properties) {
        return builder
                .setConnectTimeout(properties.getConnectTimeout())
                .setReadTimeout(properties.getReadTimeout())
                .additionalInterceptors((request, body, execution) -> {
                    request.getHeaders().set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
                    request.getHeaders().set(HttpHeaders.USER_AGENT, USER_AGENT);
                    request.getHeaders().set(HttpHeaders.ORIGIN, "https://www.windy.com");
                    request.getHeaders().set(HttpHeaders.REFERER, "https://www.windy.com/");
                    log.debug("Windy 请求 -> {} {}", request.getMethod(), request.getURI());
                    return execution.execute(request, body);
                })
                .build();
    }
}
