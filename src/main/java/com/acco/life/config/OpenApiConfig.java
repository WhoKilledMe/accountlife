package com.acco.life.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.customizers.OperationCustomizer;
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
                        .version("1.0.0"));
    }

    @Bean
    public OperationCustomizer globalHeaderCustomizer() {
        return (operation, handlerMethod) -> {
            // 添加 token header
            operation.addParametersItem(new io.swagger.v3.oas.models.parameters.Parameter()
                    .in("header")
                    .schema(new io.swagger.v3.oas.models.media.StringSchema())
                    .name("userId")
                    .description("用户ID")
                    .required(false));

            return operation;
        };
    }

}
