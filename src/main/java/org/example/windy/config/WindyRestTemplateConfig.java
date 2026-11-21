package org.example.windy.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

/**
 * 调用 Windy 账号 / 数据服务专用的 RestTemplate。
 *
 * @author windy
 */
@Slf4j
@Configuration
public class WindyRestTemplateConfig {

    private static final String WINDY_ORIGIN = "https://www.windy.com";

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
            + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/157.0.0.0 Safari/537.36";

    private static final String SEC_CH_UA =
            "\"Not-A.Brand\";v=\"99\", \"Google Chrome\";v=\"157\", \"Chromium\";v=\"157\"";

    @Bean
    public RestTemplate windyRestTemplate(RestTemplateBuilder builder, WindyProperties properties) {
        return builder
                .setConnectTimeout(properties.getConnectTimeout())
                .setReadTimeout(properties.getReadTimeout())
                .additionalInterceptors((request, body, execution) -> {
                    HttpHeaders headers = request.getHeaders();

                    // 必需：Windy 会校验 UA / Accept / Origin / Referer
                    headers.set(HttpHeaders.ACCEPT, properties.getAcceptHeader());
                    headers.set(HttpHeaders.USER_AGENT, USER_AGENT);
                    headers.set(HttpHeaders.ORIGIN, WINDY_ORIGIN);
                    headers.set(HttpHeaders.REFERER, WINDY_ORIGIN + "/");

                    // 建议：禁止 CDN 缓存，避免拿到旧起报的数据
                    headers.set(HttpHeaders.CACHE_CONTROL, "no-cache");
                    headers.set(HttpHeaders.PRAGMA, "no-cache");
                    headers.set(HttpHeaders.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.9,en;q=0.8");

                    // 可选：模拟 Chrome 的浏览器指纹，部分 WAF 会检查
                    headers.set("Sec-Ch-Ua", SEC_CH_UA);
                    headers.set("Sec-Ch-Ua-Mobile", "?0");
                    headers.set("Sec-Ch-Ua-Platform", "\"Windows\"");
                    headers.set("Sec-Fetch-Dest", "empty");
                    headers.set("Sec-Fetch-Mode", "cors");
                    headers.set("Sec-Fetch-Site", "same-site");
                    headers.set("Priority", "u=1, i");

                    // 注意：不要手动设置 Accept-Encoding，交给 RestTemplate/HttpClient 自动解压
                    log.debug("Windy 请求 -> {} {}", request.getMethod(), request.getURI());
                    return execution.execute(request, body);
                })
                .build();
    }
}
