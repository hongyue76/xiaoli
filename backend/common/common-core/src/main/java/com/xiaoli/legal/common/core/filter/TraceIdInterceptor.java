package com.xiaoli.legal.common.core.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 链路追踪拦截器
 * 在请求处理完成后将 traceId 写入响应头，并在日志中记录请求耗时。
 * 配合 TraceIdFilter 使用。
 *
 * @author Xiaoli Legal
 */
@Component
public class TraceIdInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(TraceIdInterceptor.class);

    private static final String TRACE_ID = "traceId";
    private static final String START_TIME = "requestStartTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 记录请求开始时间
        request.setAttribute(START_TIME, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex
    ) {
        String traceId = MDC.get(TRACE_ID);
        Long startTime = (Long) request.getAttribute(START_TIME);

        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            log.info("[{}] {} {} - status: {}, cost: {}ms",
                    traceId,
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration);
        }
    }
}
