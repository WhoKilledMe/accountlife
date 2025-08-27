package com.acco.life.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: OpenAPI配置类，用于配置Swagger文档信息和全局参数
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@Configuration
public class OpenApiConfig {

    /**
     * 配置OpenAPI文档基本信息
     *
     * @return OpenAPI对象包含文档元信息
     */
    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("个人资产管理系统")
                        .version("1.0.0"));
    }

    /**
     * 创建OperationCustomizer实例，用于添加全局请求参数
     *
     * @return OperationCustomizer 自定义操作配置器
     */
    @Bean
    public OperationCustomizer globalHeaderCustomizer() {
        return (operation, handlerMethod) -> {
            // 添加 userId header
            operation.addParametersItem(new io.swagger.v3.oas.models.parameters.Parameter()
                    .in("header")
                    .schema(new io.swagger.v3.oas.models.media.StringSchema())
                    .name("token")
                    .description("token")
                    .required(false));

            return operation;
        };
    }

}
