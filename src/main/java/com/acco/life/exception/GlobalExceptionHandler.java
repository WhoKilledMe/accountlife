//package com.acco.life.exception;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import org.springframework.web.bind.support.WebExchangeBindException;
//import reactor.core.publisher.Mono;
//
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * 全局异常处理器
// *
// * @author wensen.zhang
// * @version V1.0.0
// */
//@Slf4j
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//    /**
//     * 处理业务异常
//     */
//    @ExceptionHandler(BusinessException.class)
//    public Mono<ResponseEntity<ErrorResponse>> handleBusinessException(BusinessException e) {
//        log.error("业务异常: {}", e.getMessage(), e);
//        ErrorResponse error = ErrorResponse.builder()
//                .timestamp(LocalDateTime.now())
//                .status(HttpStatus.BAD_REQUEST.value())
//                .error("Business Error")
//                .message(e.getMessage())
//                .path("/api")
//                .build();
//        return Mono.just(ResponseEntity.badRequest().body(error));
//    }
//
//    /**
//     * 处理参数校验异常
//     */
//    @ExceptionHandler(WebExchangeBindException.class)
//    public Mono<ResponseEntity<ErrorResponse>> handleValidationException(WebExchangeBindException e) {
//        log.error("参数校验异常: {}", e.getMessage(), e);
//        Map<String, String> errors = new HashMap<>();
//        e.getBindingResult().getFieldErrors().forEach(error ->
//            errors.put(error.getField(), error.getDefaultMessage())
//        );
//
//        ErrorResponse error = ErrorResponse.builder()
//                .timestamp(LocalDateTime.now())
//                .status(HttpStatus.BAD_REQUEST.value())
//                .error("Validation Error")
//                .message("参数校验失败")
//                .details(errors)
//                .path("/api")
//                .build();
//        return Mono.just(ResponseEntity.badRequest().body(error));
//    }
//
//    /**
//     * 处理通用异常
//     */
//    @ExceptionHandler(Exception.class)
//    public Mono<ResponseEntity<ErrorResponse>> handleGenericException(Exception e) {
//        log.error("系统异常: {}", e.getMessage(), e);
//        ErrorResponse error = ErrorResponse.builder()
//                .timestamp(LocalDateTime.now())
//                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
//                .error("Internal Server Error")
//                .message("系统内部错误")
//                .path("/api")
//                .build();
//        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error));
//    }
//}