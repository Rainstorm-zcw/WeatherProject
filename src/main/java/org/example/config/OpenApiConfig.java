package org.example.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 文档基础信息，配合 Knife4j 使用。
 *
 * @author windy
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openMeteoOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Windy Open-Meteo 天气接口")
                        .description("基于 https://api.open-meteo.com 的逐小时天气数据服务，"
                                + "返回温度、风速、风向、辐照、云量、气压、相对湿度、露点温度、降水量、降雪量。")
                        .version("1.0.0"))
                .servers(List.of(new Server().url("/").description("当前服务")));
    }
}
