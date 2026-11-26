package org.example.windy.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;

/**
 * 调用 Windy 账号 / 数据服务专用的 RestTemplate。
 * <p>
 * 支持通过 {@code windy.proxy.host/port} 配置 HTTP 代理；
 * Java 默认不会使用 Windows/Clash 的系统代理，所以国内网络下通常需要配置。
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
    public RestTemplate windyRestTemplate(WindyProperties properties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) properties.getConnectTimeout().toMillis());
        factory.setReadTimeout((int) properties.getReadTimeout().toMillis());

        WindyProperties.HttpProxy proxy = properties.getProxy();
        if (proxy != null && StringUtils.hasText(proxy.getHost())) {
            int port = proxy.getPort() > 0 ? proxy.getPort() : 80;
            factory.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy.getHost(), port)));
            log.info("Windy 使用 HTTP 代理 {}:{}", proxy.getHost(), port);
        }

        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.setInterceptors(List.of((request, body, execution) -> {
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

            // 注意：不要手动设置 Accept-Encoding，交给 RestTemplate 自动解压
            log.debug("Windy 请求 -> {} {}", request.getMethod(), request.getURI());
            return execution.execute(request, body);
        }));
        return restTemplate;
    }
}
