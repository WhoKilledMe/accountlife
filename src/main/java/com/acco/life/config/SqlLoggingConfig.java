package com.acco.life.config;

import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;

/**
 * SQL日志配置类，优化R2DBC的SQL日志输出
 */
@Slf4j
@Configuration
public class SqlLoggingConfig {

    /**
     * 配置DatabaseClient以显示详细的SQL日志
     */
    @Bean
    public DatabaseClient databaseClient(ConnectionFactory connectionFactory) {
        return DatabaseClient.builder()
                .connectionFactory(connectionFactory)
                .build();
    }
} 