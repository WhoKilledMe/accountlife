//package com.acco.life.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.web.server.SecurityWebFilterChain;
//
///**
// * 安全配置类
// *
// * @author wensen.zhang
// * @version V1.0.0
// */
//@Configuration
//@EnableWebFluxSecurity
//public class SecurityConfig {
//
//    /**
//     * 配置安全过滤器链
//     */
//    @Bean
//    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
//        http
//            .authorizeExchange(exchanges -> exchanges
//                .pathMatchers("/api/**").permitAll()
//                .pathMatchers("/swagger-ui/**", "/webjars/**","/v3/api-docs/**").permitAll()
//                .anyExchange().authenticated()
//            )
//            .csrf(csrf -> csrf.disable())
//            .httpBasic(httpBasic -> httpBasic.disable())
//            .formLogin(formLogin -> formLogin.disable());
//
//        return http.build();
//    }
//}