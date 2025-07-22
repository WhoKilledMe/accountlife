package com.acco.life.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wensenzhang
 * @version V1.0.0
 * 星星之火，可以燎原
 * @desc OpenApiConfig
 * @date 2025/7/15 17:17
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("个人资产管理系统")
                        .description("")
                        .version("1.0.0"));
    }
}