package org.example;

import org.example.config.OpenMeteoProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * 项目启动类。
 *
 * @author windy
 */
@SpringBootApplication
@EnableConfigurationProperties(OpenMeteoProperties.class)
public class OpenMeteoApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenMeteoApplication.class, args);
    }
}
