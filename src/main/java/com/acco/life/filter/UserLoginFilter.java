package com.acco.life.filter;

import com.acco.life.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import reactor.core.publisher.Flux;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * description: 用户登录过滤器，用于从请求头中提取用户ID并放入上下文中
 *
 * @date: 2025-07-24 17:54:23
 * @author wensen.zhang
 * @version V1.0.0
 */
@Component
public class UserLoginFilter implements WebFilter {

    @Autowired
    private AuthService authService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public @NonNull Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {

        String path = exchange.getRequest().getPath().value();
        if ("/api/auth/login".equals(path) || "/api/auth/logout".equals(path)) {
            return chain.filter(exchange);
        }

        String tokenHeader = exchange.getRequest().getHeaders().getFirst("token");
        String authorization = exchange.getRequest().getHeaders().getFirst("Authorization");
        String token = null;
        if (authorization != null && authorization.startsWith("Bearer ")) {
            token = authorization.substring(7);
        }
        if (token == null && tokenHeader != null && !tokenHeader.isEmpty()) {
            token = tokenHeader;
        }

        if (token == null) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return authService.getUserIdByToken(token)
                .flatMap(uid -> {
                    // 将 userId 放入 Reactor 上下文
                    Mono<Void> next;

                    if (shouldInjectUserId(exchange)) {
                        // 仅对 JSON 且非流式请求注入 userId 字段
                        next = DataBufferUtils.join(exchange.getRequest().getBody())
                                .defaultIfEmpty(exchange.getResponse().bufferFactory().wrap(new byte[0]))
                                .flatMap(buffer -> {
                                    try {
                                        byte[] bytes = new byte[buffer.readableByteCount()];
                                        buffer.read(bytes);
                                        DataBufferUtils.release(buffer);

                                        Charset charset = resolveCharset(exchange.getRequest().getHeaders());
                                        String bodyStr = new String(bytes, charset);

                                        JsonNode parsed;
                                        if (bodyStr.isEmpty()) {
                                            parsed = objectMapper.createObjectNode();
                                        } else {
                                            parsed = objectMapper.readTree(bodyStr);
                                        }

                                        JsonNode mutatedNode;
                                        if (parsed.isArray()) {
                                            // 入参为集合：为数组中每个对象元素追加 userId
                                            for (JsonNode node : parsed) {
                                                if (node.isObject()) {
                                                    ((ObjectNode) node).put("userId", String.valueOf(uid));
                                                }
                                            }
                                            mutatedNode = parsed;
                                        } else if (parsed.isObject()) {
                                            ((ObjectNode) parsed).put("userId", String.valueOf(uid));
                                            mutatedNode = parsed;
                                        } else {
                                            // 非对象/数组，包装为对象
                                            ObjectNode obj = objectMapper.createObjectNode();
                                            obj.set("value", parsed);
                                            obj.put("userId", String.valueOf(uid));
                                            mutatedNode = obj;
                                        }

                                        byte[] newBody = objectMapper.writeValueAsBytes(mutatedNode);
                                        DataBufferFactory factory = exchange.getResponse().bufferFactory();
                                        DataBuffer newBuffer = factory.wrap(newBody);

                                        ServerHttpRequest decorated = new ServerHttpRequestDecorator(exchange.getRequest()) {
                                            @Override
                                            public @NonNull Flux<DataBuffer> getBody() {
                                                return Flux.just(newBuffer);
                                            }

                                            @Override
                                            public @NonNull HttpHeaders getHeaders() {
                                                HttpHeaders headers = new HttpHeaders();
                                                headers.putAll(super.getHeaders());
                                                headers.setContentLength(newBody.length);
                                                headers.setContentType(MediaType.APPLICATION_JSON);
                                                return headers;
                                            }
                                        };

                                        ServerWebExchange mutated = exchange.mutate().request(decorated).build();
                                        return chain.filter(mutated);
                                    } catch (Exception e) {
                                        // 解析失败则直接透传原始请求
                                        return chain.filter(exchange);
                                    }
                                });
                    } else {
                        next = chain.filter(exchange);
                    }

                    return next.contextWrite(ctx -> ctx.put("userId", String.valueOf(uid)));
                })
                .switchIfEmpty(Mono.defer(() -> {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }));
    }

    private boolean shouldInjectUserId(ServerWebExchange exchange) {
        MediaType contentType = exchange.getRequest().getHeaders().getContentType();
        if (contentType == null) {
            return false;
        }
        // 仅处理 application/json
        if (!MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
            return false;
        }
        // 排除流式/多部分/二进制上传等
        if (isStreamingContentType(contentType)) {
            return false;
        }
        HttpMethod method = exchange.getRequest().getMethod();
        return method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.PATCH;
    }

    private boolean isStreamingContentType(MediaType type) {
        return MediaType.APPLICATION_STREAM_JSON.isCompatibleWith(type)
                || MediaType.TEXT_EVENT_STREAM.isCompatibleWith(type)
                || MediaType.MULTIPART_FORM_DATA.isCompatibleWith(type)
                || MediaType.APPLICATION_OCTET_STREAM.isCompatibleWith(type);
    }

    private Charset resolveCharset(HttpHeaders headers) {
        MediaType mediaType = headers.getContentType();
        if (mediaType != null && mediaType.getCharset() != null) {
            return mediaType.getCharset();
        }
        return StandardCharsets.UTF_8;
    }
}