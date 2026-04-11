package com.xiaoli.legal.common.core.config;

import com.xiaoli.legal.common.core.domain.Result;
import com.xiaoli.legal.common.core.domain.ResultCode;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Feign 调用异常处理器
 * 统一处理服务间调用异常
 */
@RestControllerAdvice
public class FeignExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(FeignExceptionHandler.class);

    /**
     * 处理 Feign 调用异常
     */
    @ExceptionHandler(FeignException.class)
    public Result<?> handleFeignException(FeignException e) {
        log.error("Feign 调用异常: status={}, message={}", e.status(), e.getMessage());
        return Result.fail(ResultCode.FORBIDDEN.getCode(), "服务调用失败: " + getErrorMessage(e));
    }

    /**
     * 处理服务不可用异常
     */
    @ExceptionHandler(FeignException.ServiceUnavailable.class)
    public Result<?> handleServiceUnavailable(FeignException.ServiceUnavailable e) {
        log.error("服务不可用: {}", e.getMessage());
        return Result.fail(ResultCode.FORBIDDEN.getCode(), "目标服务暂时不可用，请稍后重试");
    }

    /**
     * 处理超时异常
     */
    @ExceptionHandler(java.util.concurrent.TimeoutException.class)
    public Result<?> handleTimeout(java.util.concurrent.TimeoutException e) {
        log.error("服务调用超时: {}", e.getMessage());
        return Result.fail(ResultCode.FORBIDDEN.getCode(), "服务调用超时，请稍后重试");
    }

    /**
     * 处理 Socket 超时异常
     */
    @ExceptionHandler(java.net.SocketTimeoutException.class)
    public Result<?> handleSocketTimeout(java.net.SocketTimeoutException e) {
        log.error("Socket 超时: {}", e.getMessage());
        return Result.fail(ResultCode.FORBIDDEN.getCode(), "服务响应超时，请稍后重试");
    }

    /**
     * 处理通用运行时异常（不包括 FeignException）
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntimeException(RuntimeException e) {
        // 如果是 FeignException，让 Spring 处理
        if (e.getCause() instanceof FeignException) {
            throw e;
        }
        log.error("运行时异常: {}", e.getMessage(), e);
        return Result.fail(ResultCode.INTERNAL_ERROR.getCode(), "服务器内部错误: " + e.getMessage());
    }

    private String getErrorMessage(FeignException e) {
        switch (e.status()) {
            case 400: return "请求参数错误";
            case 401: return "未授权访问";
            case 403: return "禁止访问";
            case 404: return "服务接口不存在";
            case 500: return "服务内部错误";
            case 503: return "服务暂时不可用";
            default: return "未知错误(" + e.status() + ")";
        }
    }
}
