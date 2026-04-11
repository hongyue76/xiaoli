package com.xiaoli.legal.common.core.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.UUID;

/**
 * 链路追踪过滤器
 * 为每个请求生成唯一的 traceId，写入 MDC，便于日志追踪和问题排查。
 * 支持从上游请求头（如 X-Trace-Id、X-Request-Id）中继承 traceId，实现跨服务链路追踪。
 *
 * @author Xiaoli Legal
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceIdFilter extends org.springframework.web.filter.OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(TraceIdFilter.class);

    private static final String TRACE_ID = "traceId";
    private static final String HEADER_TRACE_ID = "X-Trace-Id";
    private static final String HEADER_REQUEST_ID = "X-Request-Id";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            jakarta.servlet.FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            // 优先从请求头获取 traceId（支持跨服务传递），否则生成新的
            String traceId = resolveTraceId(request);
            MDC.put(TRACE_ID, traceId);

            // 将 traceId 写入响应头，方便客户端追踪
            response.setHeader(HEADER_TRACE_ID, traceId);

            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }

    /**
     * 解析 traceId：优先从上游请求头获取，否则生成新的
     */
    private String resolveTraceId(HttpServletRequest request) {
        String traceId = request.getHeader(HEADER_TRACE_ID);
        if (StringUtils.hasText(traceId)) {
            return traceId;
        }
        traceId = request.getHeader(HEADER_REQUEST_ID);
        if (StringUtils.hasText(traceId)) {
            return traceId;
        }
        return generateTraceId();
    }

    /**
     * 生成短格式的 traceId（32位，去掉 UUID 中的连字符）
     */
    private String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
