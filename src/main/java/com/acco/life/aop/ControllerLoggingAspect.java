package com.acco.life.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class ControllerLoggingAspect {

    @Pointcut("within(com.acco.life.controller..*)")
    public void controllerPackage() {}

    @Around("controllerPackage()")
    public Object aroundController(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();
        Object[] args = pjp.getArgs();
        try {
            log.info("[Controller] -> {}.{} 入参: {}", className, methodName, Arrays.toString(args));
            Object result = pjp.proceed();
            long cost = System.currentTimeMillis() - start;

            if (result instanceof reactor.core.publisher.Mono) {
                return ((reactor.core.publisher.Mono<?>) result)
                        .doOnSuccess(r -> log.info(
                                "[Controller] <- {}.{} 出参(Mono): {} ({}ms)",
                                className, methodName, r, cost))
                        .doOnError(e -> log.error(
                                "[Controller] !! {}.{} 出错(Mono): {} ({}ms)",
                                className, methodName, e.getMessage(), cost, e));
            } else if (result instanceof reactor.core.publisher.Flux) {
                return ((reactor.core.publisher.Flux<?>) result)
                        .doOnNext(r -> log.info(
                                "[Controller] <- {}.{} 出参元素(Flux): {} ({}ms)",
                                className, methodName, r, cost))
                        .doOnError(e -> log.error(
                                "[Controller] !! {}.{} 出错(Flux): {} ({}ms)",
                                className, methodName, e.getMessage(), cost, e));
            } else {
                log.info("[Controller] <- {}.{} 出参: {} ({}ms)", className, methodName, result, cost);
                return result;
            }
        } catch (Throwable ex) {
            long cost = System.currentTimeMillis() - start;
            log.error("[Controller] !! {}.{} 异常: {} ({}ms)", className, methodName, ex.getMessage(), cost, ex);
            throw ex;
        }
    }

}


