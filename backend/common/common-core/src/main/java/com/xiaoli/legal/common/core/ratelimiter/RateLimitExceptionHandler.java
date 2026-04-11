package com.xiaoli.legal.common.core.ratelimiter;

import com.xiaoli.legal.common.core.domain.Result;
import com.xiaoli.legal.common.core.domain.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 限流异常处理器
 * 统一处理限流异常，返回友好的错误信息
 *
 * @author Xiaoli Legal
 * @since 2026-03-31
 */
@Order(1) // 优先级最高
@RestControllerAdvice
public class RateLimitExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RateLimitExceptionHandler.class);

    /**
     * 处理限流异常
     *
     * @param ex 限流异常
     * @return 错误响应
     */
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    @ExceptionHandler(RateLimitException.class)
    public Result<Void> handleRateLimitException(RateLimitException ex) {
        log.warn("Rate limit triggered: key={}, message={}", ex.getKey(), ex.getMessage());

        return Result.fail(ResultCode.RATE_LIMIT.getCode(), ex.getMessage());
    }
}
